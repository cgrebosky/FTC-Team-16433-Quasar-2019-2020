package quasar.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.GamepadState;
import quasar.lib.SubSystem;

public class Arms extends SubSystem {

    private DcMotor left, right;

    private Servo claw, hinge;

    private final double CLAW_CLOSED = 1, CLAW_OPEN = 0.8;
    private final int ARM_IN = 0, ARM_OUT = 250;
    private final double HINGE_IN = 0, HINGE_OUT = 0.8;

    private boolean clawClosed = false;
    private double targetPos = 0;

    @Override
    public void init() {
        left = hardwareMap.dcMotor.get("leftArm");
        right = hardwareMap.dcMotor.get("rightArm");

        claw = hardwareMap.servo.get("claw");
        hinge = hardwareMap.servo.get("clawHinge");

        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        left.setTargetPosition(0);

        left.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        right.setDirection(DcMotorSimple.Direction.REVERSE);


    }
    @Override
    public void loop() {
        moveArms();
        toggleClaw();
        orientClaw();

        postLoop();
    }
    @Override
    public void stop() {
        left.setPower(0);
        right.setPower(0);
    }

    @Override
    protected void telemetry() {
        opm.telemetry.addLine("ARMS");
        opm.telemetry.addData("    Power", left.getPower());
        opm.telemetry.addData("    Arm Position", getArmPos());
        opm.telemetry.addData("    Target Position", targetPos);
        opm.telemetry.addData("    Claw Position", claw.getPosition());
    }

    private void moveArms() {
        targetPos += gamepad1.left_trigger;
        targetPos -= gamepad1.right_trigger;

        left.setTargetPosition((int) targetPos);
        left.setPower(getPower());
        right.setPower(getPower());
    }
    private double getPower() {
        double dist = left.getTargetPosition() - left.getCurrentPosition();
        double pwr = dist / 50;

        if(Math.abs(pwr) < 0.35) pwr = Math.signum(pwr) * 0.35;

        if(Math.abs(pwr) > 1) pwr = Math.signum(pwr);

        return pwr;
    }
    private double getArmPos() {
        return (double) ( right.getCurrentPosition() + left.getCurrentPosition() ) / 2;
    }

    private void orientClaw() {
        //This is calculated with some really basic trig / geometry, just draw a diagram & it should make sense
        double unitArmAngle = ( (double) left.getCurrentPosition() ) / ARM_OUT;
        double s = HINGE_OUT * (1 - unitArmAngle);

        claw.setPosition(s);
    }

    private void toggleClaw() {
        clawClosed = GamepadState.toggle(gamepad1.a, prev1.a, clawClosed);
        if(clawClosed) claw.setPosition(CLAW_CLOSED);
        else claw.setPosition(CLAW_OPEN);
    }
}
