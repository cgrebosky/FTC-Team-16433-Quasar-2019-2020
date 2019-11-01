package quasar.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.GamepadState;
import quasar.lib.SubSystem;

public class Arms extends SubSystem {

    private DcMotor left, right;

    private Servo claw, hinge;

    private final double CLAW_CLOSED = 1, CLAW_OPEN = 0;
    private final int ARM_IN = 0, ARM_1 = 550, ARM_2 = 700, ARM_3 = 800;

    private boolean clawClosed = false;
    private int targetPos = 0;

    @Override
    public void init() {
        left = hardwareMap.dcMotor.get("leftArm");
        right = hardwareMap.dcMotor.get("rightArm");

        claw = hardwareMap.servo.get("claw");
        hinge = hardwareMap.servo.get("clawHinge");

        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        left.setDirection(DcMotorSimple.Direction.REVERSE);
        right.setDirection(DcMotorSimple.Direction.FORWARD);

        left.setTargetPosition(0);
        left.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        right.setTargetPosition(0);
        right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
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
        opm.telemetry.addData("    Hinge position", hinge.getPosition());
        opm.telemetry.addData("    Claw is currently", clawClosed?"CLOSED":"OPEN");
    }

    private void moveArms() {
        setTargetPos();
        left.setTargetPosition(targetPos);
        right.setTargetPosition(targetPos);

        setArmPowers();
    }
    private double getArmPos() {
        return (double) ( right.getCurrentPosition() + left.getCurrentPosition() ) / 2;
    }
    private void setTargetPos() {
        if(gamepad1.a) targetPos = ARM_IN;
        else if(gamepad1.b) targetPos = ARM_1;
        else if(gamepad1.x) targetPos = ARM_2;
        else if(gamepad1.y) targetPos = ARM_3;
    }

    private void setArmPowers() {
        if(targetPos == ARM_IN && isNear(left.getCurrentPosition(), ARM_IN, 10)) {
            left.setPower(0);
            right.setPower(0);
        } else if(targetPos == ARM_1 && isNear(left.getCurrentPosition(), ARM_1, 10)) {
            left.setPower(0);
            right.setPower(0);
        } else if(targetPos == ARM_2 && isNear(left.getCurrentPosition(), ARM_2, 10)) {
            left.setPower(0);
            right.setPower(0);
        } else {
            left.setPower(0.2);
            right.setPower(0.2);
        }
    }

    private void orientClaw() {
        double clawPos = (gamepad1.left_trigger + gamepad1.right_trigger) / 2;
        hinge.setPosition(clawPos);
    }
    private void toggleClaw() {
        clawClosed = GamepadState.toggle(gamepad1.left_bumper, prev1.left_bumper, clawClosed);
        if(clawClosed) claw.setPosition(CLAW_CLOSED);
        else claw.setPosition(CLAW_OPEN);
    }

    private boolean isNear(int val, int targetVal, int err) {
        return (val < targetVal + err && val > targetVal - err);
    }
}
