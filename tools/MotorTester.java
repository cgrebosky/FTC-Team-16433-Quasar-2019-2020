package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Motor Tester", group = "Tools")
public class MotorTester extends OpMode {

    private DcMotor motor;

    @Override
    public void init() {
        motor = hardwareMap.dcMotor.get("motor");
    }
    @Override
    public void loop() {
        telemetry.addData("Position", motor.getCurrentPosition());
        telemetry.addData("Direction", motor.getDirection());
        telemetry.addData("Mode", motor.getMode());
        telemetry.addData("Name", motor.getDeviceName());
        telemetry.addData("Power",motor.getPower());
        telemetry.addData("Target Position",motor.getTargetPosition());
        telemetry.addData("Zero Power Behaviour", motor.getZeroPowerBehavior());
        telemetry.update();
    }
    @Override
    public void stop() {
        motor.setPower(0);
    }
}
