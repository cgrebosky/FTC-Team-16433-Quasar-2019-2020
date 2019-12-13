package quasar.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import quasar.lib.SubSystem;

public class Collector extends SubSystem {

    public DcMotor left, right;

    private final double LEFT_COEF = 0.8; //This makes 1 wheel go a bit slower, allowing us to collect
                                           //blocks against the wall (in theory :( )

    @Override
    public void init() {
        left = hardwareMap.dcMotor.get("collectorLeft");
        right = hardwareMap.dcMotor.get("collectorRight");

        left.setDirection(DcMotorSimple.Direction.FORWARD);
        right.setDirection(DcMotorSimple.Direction.REVERSE);

        left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {
        double power = gamepad1.left_trigger - gamepad1.right_trigger;

        left.setPower(power * LEFT_COEF);
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

    public void collect() {
        left.setPower(0.8);
        right.setPower(1);
    }
    public void push() {
        left.setPower(-1);
        right.setPower(-1);
    }
    public void zeroMotors() {
        left.setPower(0);
        right.setPower(0);
    }
}
