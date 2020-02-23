package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Motor Tool", group = "Tools")
@Disabled
public class MotorTool extends OpMode {

    private DcMotor motor, rightArm;

    private double position; //[0,250]

    @Override
    public void init() {
        motor = hardwareMap.dcMotor.get("motor");
        rightArm = hardwareMap.dcMotor.get("rightArm");

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setTargetPosition(0);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        rightArm.setDirection(DcMotorSimple.Direction.REVERSE);
        rightArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightArm.setTargetPosition(0);
        rightArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    @Override
    public void loop() {
        motor.setTargetPosition((int) position);
        motor.setPower(1);
        rightArm.setTargetPosition(motor.getTargetPosition());
        rightArm.setPower(1);

        telemetry.addData("Position", motor.getCurrentPosition());
        telemetry.addData("Direction", motor.getDirection());
        telemetry.addData("Mode", motor.getMode());
        telemetry.addData("Name", motor.getDeviceName());
        telemetry.addData("Power",motor.getPower());
        telemetry.addData("Target Position",motor.getTargetPosition());
        telemetry.addData("Zero Power Behaviour", motor.getZeroPowerBehavior());
        telemetry.update();

        position += 3 * gamepad1.left_trigger;
        position -= 3 * gamepad1.right_trigger;
        if(gamepad1.a) position = -550;
        if(gamepad1.b) position = -700;
        if(gamepad1.x) position = -800;
        if(gamepad1.y) position = -100;
    }
    @Override
    public void stop() {
        motor.setPower(0);
    }
}
