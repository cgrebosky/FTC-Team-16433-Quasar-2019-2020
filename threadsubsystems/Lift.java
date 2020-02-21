package quasar.threadsubsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.GamepadState;
import quasar.lib.ThreadSubSystem;
import quasar.lib.macro.MacroSystem;

public final class Lift extends ThreadSubSystem implements MacroSystem {

    private final double clawLeftClosed = 0.67, clawLeftOpen = 0.44, clawRightClosed = 0.3, clawRightOpen = 0.1, angleIn = 0.23, angleOut = 1;

    private DcMotor liftLeft, liftRight;
    private CRServo extenderLeft, extenderRight;
    private Servo clawLeft, clawRight, clawAngle;

    private DigitalChannel limitLeft, limitRight;

    private boolean clawIsOpen = false, angleIsOut = false;

    //region SubSystem
    @Override
    protected void _init() {
        liftLeft = hmap.dcMotor.get("liftLeft");
        liftRight = hmap.dcMotor.get("liftRight");
        liftLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        extenderLeft = hmap.crservo.get("extenderLeft");
        extenderRight = hmap.crservo.get("extenderRight");
        extenderRight.setDirection(DcMotorSimple.Direction.REVERSE);
        extenderLeft.setDirection(DcMotorSimple.Direction.FORWARD);

        clawLeft = hmap.servo.get("clawLeft");
        clawRight = hmap.servo.get("clawRight");
        clawAngle = hmap.servo.get("clawAngle");
        clawLeft.setPosition(clawLeftOpen);
        clawRight.setPosition(clawRightOpen);
        clawAngle.setPosition(angleIn);

        limitLeft = hmap.get(DigitalChannel.class, "liftLimitLeft");
        limitRight = hmap.get(DigitalChannel.class, "liftLimitRight");

        limitLeft.setMode(DigitalChannel.Mode.INPUT);
        limitRight.setMode(DigitalChannel.Mode.INPUT);
    }

    @Override
    protected void _loop() {
        controlClaw();
        controlLift();
        controlExtender();
    }

    @Override
    protected void _stop() {
        liftLeft.setPower(0);
        liftRight.setPower(0);
        extenderLeft.setPower(0);
        extenderRight.setPower(0);
    }

    @Override
    protected void _telemetry() {
        telemetry.addLine("LIFT");
        telemetry.addData("    Lift Position", liftLeft.getCurrentPosition());
        telemetry.addData("    Limits Triggered", !limitLeft.getState() || !limitRight.getState());
        telemetry.addData("    Lift Power", liftLeft.getPower());
        telemetry.addLine();
        telemetry.addData("    Claw State", clawLeft.getPosition() == clawLeftClosed?"CLOSED":"OPEN");
        telemetry.addData("    Claw Angle", clawAngle.getPosition() == angleOut?"OUT":"IN");
        telemetry.addData("    Arm Power", extenderLeft.getPower());
    }
    //endregion

    private void controlClaw() {
        clawIsOpen = GamepadState.toggle(gamepad2.right_bumper, prev2.right_bumper, clawIsOpen);
        angleIsOut = GamepadState.toggle(gamepad2.left_bumper, prev2.left_bumper, angleIsOut);

        if(clawIsOpen) openClaw();
        else           closeClaw();

        if(angleIsOut) angleClawOut();
        else           angleClawIn();
    }
    public synchronized void openClaw() {
        clawIsOpen = true;
        clawLeft.setPosition(clawLeftOpen);
        clawRight.setPosition(clawRightOpen);
    }
    public synchronized void closeClaw() {
        clawIsOpen = false;
        clawLeft.setPosition(clawLeftClosed);
        clawRight.setPosition(clawRightClosed);
    }
    public synchronized void angleClawOut() {
        clawIsOpen = true;
        clawAngle.setPosition(angleOut);
    }
    public synchronized void angleClawIn() {
        clawIsOpen = false;
        clawAngle.setPosition(angleIn);
    }

    private void controlExtender() {
        double pwr = -gamepad2.left_stick_y;
        extenderLeft.setPower(pwr);
        extenderRight.setPower(pwr);
    }

    private void controlLift() {
        double pwr = gamepad2.left_trigger - gamepad2.right_trigger;
        double rightPwr = pwr, leftPwr = pwr;

        if(!limitLeft.getState() && leftPwr > 0) leftPwr = 0;
        if(!limitRight.getState() && rightPwr > 0) rightPwr = 0;

        liftLeft.setPower(leftPwr);
        liftRight.setPower(rightPwr);
    }

    //region Macro
    @Override
    public void setMacroState() {

    }

    @Override
    public void getMacroState() {

    }
    //endregion
}
