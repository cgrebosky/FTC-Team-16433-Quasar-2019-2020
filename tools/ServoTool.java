package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Servo Tool", group = "Tools")
@Disabled
public final class ServoTool extends OpMode {

    private Servo servo;

    @Override
    public void init() {
        servo = hardwareMap.servo.get("servo");
    }

    @Override
    public void loop() {
        double pos = ( -gamepad1.left_stick_y + 1 ) / 2;

        servo.setPosition(pos);
        telemetry.addData("Position", pos);

        telemetry.update();
    }
}
