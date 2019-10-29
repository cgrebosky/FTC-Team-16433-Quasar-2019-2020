package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Motor Tester", group = "Tools")
public class MotorTester extends OpMode {

    private DcMotor motor;

    private double position; //[0,250]

    @Override
    public void init() {
        motor = hardwareMap.dcMotor.get("motor");

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setTargetPosition(0);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    @Override
    public void loop() {
        motor.setTargetPosition((int) position);
        motor.setPower(0.7);

        telemetry.addData("Position", motor.getCurrentPosition());
        telemetry.addData("Direction", motor.getDirection());
        telemetry.addData("Mode", motor.getMode());
        telemetry.addData("Name", motor.getDeviceName());
        telemetry.addData("Power",motor.getPower());
        telemetry.addData("Target Position",motor.getTargetPosition());
        telemetry.addData("Zero Power Behaviour", motor.getZeroPowerBehavior());
        telemetry.update();

        position += gamepad1.left_trigger;
        position -= gamepad1.right_trigger;
    }
    @Override
    public void stop() {
        motor.setPower(0);
    }
}
