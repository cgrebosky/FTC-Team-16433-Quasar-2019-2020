package quasar.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.SubSystem;

public class Collector extends SubSystem {

    public DcMotor left, right;

    private final double LEFT_COEF = 1; //This makes 1 wheel go a bit slower, allowing us to collect
                                           //blocks against the wall (in theory :( )
    public Servo limiterLeft, limiterRight;

    private final double LEFT_IN = 0, LEFT_OUT = 1, LEFT_HALF = 0.77, RIGHT_IN = 1, RIGHT_OUT = 0, RIGHT_HALF = 0.4;

    @Override
    public void init() {
        left = hardwareMap.dcMotor.get("collectorLeft");
        right = hardwareMap.dcMotor.get("collectorRight");

        left.setDirection(DcMotorSimple.Direction.FORWARD);
        right.setDirection(DcMotorSimple.Direction.REVERSE);

        left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        limiterLeft = hardwareMap.servo.get("limiterLeft");
        limiterRight = hardwareMap.servo.get("limiterRight");
        close();

    }

    @Override
    public void loop() {
        double power = gamepad1.left_trigger - gamepad1.right_trigger;

        left.setPower(power * LEFT_COEF);
        right.setPower(power);

        if(gamepad1.left_bumper) {
            open();
        } else {
            halfClose();
        }

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
        String pos = "?";
        if(limiterLeft.getPosition() == LEFT_HALF) pos = "HALF";
        if(limiterLeft.getPosition() == LEFT_OUT) pos = "OUT";
        if(limiterLeft.getPosition() == LEFT_IN) pos = "IN";
        opm.telemetry.addData("    Position", pos);
    }

    public void collect() {
        left.setPower(-0.8);
        right.setPower(-1);
    }
    public void push() {
        left.setPower(1);
        right.setPower(1);
    }
    public void zeroMotors() {
        left.setPower(0);
        right.setPower(0);
    }
    public void open() {
        limiterLeft.setPosition(LEFT_OUT);
        limiterRight.setPosition(RIGHT_OUT);
    }
    public void close() {
        limiterLeft.setPosition(LEFT_IN);
        limiterRight.setPosition(RIGHT_IN);
    }
    public void halfClose() {
        limiterLeft.setPosition(LEFT_HALF);
        limiterRight.setPosition(RIGHT_HALF);
    }
}
