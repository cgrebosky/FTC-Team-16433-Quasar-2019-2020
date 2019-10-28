package quasar.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.GamepadState;
import quasar.lib.SubSystem;

public class Arms extends SubSystem {

    DcMotor left, right;

    Servo claw, hinge;

    private final double CLOSED = 1, OPEN = 0.8;
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

        postLoop();
    }
    @Override
    public void stop() {
        left.setPower(0);
        right.setPower(0);
    }
    @Override
    protected void telemetry() {
        opm.telemetry.addData("Power", left.getPower());
        opm.telemetry.addData("LPosition", left.getCurrentPosition());
        opm.telemetry.addData("Targe Pot", targetPos);
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
    private void toggleClaw() {
        clawClosed = GamepadState.toggle(gamepad1.a, prev1.a, clawClosed);
        if(clawClosed) claw.setPosition(CLOSED);
        else claw.setPosition(OPEN);
    }
}
