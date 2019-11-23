package quasar.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import quasar.lib.SubSystem;

public class Collector extends SubSystem {

    public DcMotor left, right;

    @Override
    public void init() {
        left = hardwareMap.dcMotor.get("collectorLeft");
        right = hardwareMap.dcMotor.get("collectorRight");

        left.setDirection(DcMotorSimple.Direction.FORWARD);
        right.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop() {
        double power = gamepad1.left_trigger - gamepad1.right_trigger;

        left.setPower(power);
        right.setPower(power);

        postLoop();
    }

    @Override
    public void stop() {
        left.setPower(0);
        right.setPower(0);
    }

    @Override
    protected void telemetry() {
        opm.telemetry.addLine("COLLECTOR");
        opm.telemetry.addData("    Power", gamepad1.left_trigger - gamepad1.right_trigger);
    }
}
