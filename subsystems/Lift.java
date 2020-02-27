package quasar.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.MoreMath;
import quasar.lib.macro.MacroState;
import quasar.lib.macro.MacroSystem;

public final class Lift extends SubSystem implements MacroSystem {

    private final double CLAW_CLOSED = 0.8, CLAW_OPEN = 0.35, ANGLE_IN = 0.82, ANGLE_OUT = .07;

    private DcMotor liftLeft, liftRight;
    private CRServo extenderLeft, extenderRight;
    private Servo claw, clawAngle;

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

        claw = hmap.servo.get("claw");
        clawAngle = hmap.servo.get("angle");
        claw.setPosition(CLAW_OPEN);
        //clawAngle.setPosition(ANGLE_IN); //This has caused us more pain than benefit, tbh

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
        telemetry.addData("    Claw State", claw.getPosition() == CLAW_CLOSED ?"CLOSED":"OPEN");
        telemetry.addData("    Claw Angle", clawAngle.getPosition() == ANGLE_OUT ?"OUT":"IN");
        telemetry.addData("    Arm Power", extenderLeft.getPower());
        telemetry.addLine();
    }
    //endregion

    private boolean prevRightBumper = false;
    private boolean prevLeftBumper = false;
    private void controlClaw() {
        if(!prevLeftBumper && gamepad2.left_bumper) angleIsOut = !angleIsOut;
        if(!prevRightBumper && gamepad2.right_bumper) clawIsOpen = !clawIsOpen;

        prevLeftBumper = gamepad2.left_bumper;
        prevRightBumper = gamepad2.right_bumper;

        if(clawIsOpen) claw.setPosition(CLAW_OPEN);
        else           claw.setPosition(CLAW_CLOSED);

        if(angleIsOut) clawAngle.setPosition(ANGLE_OUT);
        else           clawAngle.setPosition(ANGLE_IN);
    }

    private void controlExtender() {
        double pwr = -gamepad2.left_stick_y;
        pwr = MoreMath.clip(pwr, -0.95, 0.95); //The servos only work in about this range.  At pwr = 1/-1, they have wierd behaviour...
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

        MacroState.Companion.getCurrentMacroState().setGrabberPos(claw.getPosition());
        MacroState.Companion.getCurrentMacroState().setAnglePos(clawAngle.getPosition());
    }

    @Override
    public void playMacroState(MacroState m) {
        liftLeft.setPower(m.getLiftPow());
        liftRight.setPower(m.getLiftPow());
        extenderLeft.setPower(m.getExtenderPow());
        extenderRight.setPower(m.getExtenderPow());
        clawAngle.setPosition(m.getAnglePos());
        claw.setPosition(m.getGrabberPos());
    }
    //endregion
}
