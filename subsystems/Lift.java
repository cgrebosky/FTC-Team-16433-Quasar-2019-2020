package quasar.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.GamepadState;
import quasar.lib.SubSystem;
import quasar.lib.ThreadSubSystem;
import quasar.lib.macro.MacroState;
import quasar.lib.macro.MacroSystem;

public final class Lift extends SubSystem implements MacroSystem {

    private final double CLAW_LEFT_CLOSED = 0.67, CLAW_LEFT_OPEN = 0.44, CLAW_RIGHT_CLOSED = 0.3, CLAW_RIGHT_OPEN = 0.1, ANGLE_IN = 0.23, ANGLE_OUT = 1;

    private DcMotor liftLeft, liftRight;
    private CRServo extenderLeft, extenderRight;
    private Servo clawLeft, clawRight, clawAngle;

    private DigitalChannel limitLeft, limitRight;

    private boolean clawIsOpen = false, angleIsOut = false;

    //region SubSystem
    @Override
    public void init() {
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
        clawLeft.setPosition(CLAW_LEFT_OPEN);
        clawRight.setPosition(CLAW_RIGHT_OPEN);
        clawAngle.setPosition(ANGLE_IN);

        limitLeft = hmap.get(DigitalChannel.class, "liftLimitLeft");
        limitRight = hmap.get(DigitalChannel.class, "liftLimitRight");

        limitLeft.setMode(DigitalChannel.Mode.INPUT);
        limitRight.setMode(DigitalChannel.Mode.INPUT);
    }

    @Override
    public void loop() {
        controlClaw();
        controlLift();
        controlExtender();

        postLoop();
    }

    @Override
    public void stop() {
        liftLeft.setPower(0);
        liftRight.setPower(0);
        extenderLeft.setPower(0);
        extenderRight.setPower(0);
    }

    @Override
    protected void telemetry() {
        telemetry.addLine("LIFT");
        telemetry.addData("    Lift Position", liftLeft.getCurrentPosition());
        telemetry.addData("    Limits Triggered", !limitLeft.getState() || !limitRight.getState());
        telemetry.addData("    Lift Power", liftLeft.getPower());
        telemetry.addLine();
        telemetry.addData("    Claw State", clawLeft.getPosition() == CLAW_LEFT_CLOSED ?"CLOSED":"OPEN");
        telemetry.addData("    Claw Angle", clawAngle.getPosition() == ANGLE_OUT ?"OUT":"IN");
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
    private void openClaw() {
        clawIsOpen = true;
        clawLeft.setPosition(CLAW_LEFT_OPEN);
        clawRight.setPosition(CLAW_RIGHT_OPEN);
    }
    private void closeClaw() {
        clawIsOpen = false;
        clawLeft.setPosition(CLAW_LEFT_CLOSED);
        clawRight.setPosition(CLAW_RIGHT_CLOSED);
    }
    private void angleClawOut() {
        clawIsOpen = true;
        clawAngle.setPosition(ANGLE_OUT);
    }
    private void angleClawIn() {
        clawIsOpen = false;
        clawAngle.setPosition(ANGLE_IN);
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
    public void recordMacroState() {
        MacroState.Companion.getCurrentMacroState().setLiftPow(liftLeft.getPower());
        MacroState.Companion.getCurrentMacroState().setExtenderPow(extenderLeft.getPower());

        MacroState.Companion.getCurrentMacroState().setGrabberPos(clawLeft.getPosition());
        MacroState.Companion.getCurrentMacroState().setAnglePos(clawAngle.getPosition());
    }

    @Override
    public void playMacroState(MacroState m) {
        liftLeft.setPower(m.getLiftPow());
        liftRight.setPower(m.getLiftPow());
        extenderLeft.setPower(m.getExtenderPow());
        extenderRight.setPower(m.getExtenderPow());
        clawAngle.setPosition(m.getAnglePos());
        clawLeft.setPosition(m.getGrabberPos());
    }
    //endregion
}
