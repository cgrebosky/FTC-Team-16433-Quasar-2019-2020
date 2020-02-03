package quasar.testing.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.subsystems.Mecanum;

@TeleOp(name = "Mecanum Tick Testing", group = "Testing")
public class MecanumTickTesting extends OpMode {

    Mecanum m = new Mecanum();

    @Override
    public void init() {
        m.create(this);
        m.init();
        m.useCompBotConfig();
    }

    @Override
    public void loop() {
        if(-gamepad1.left_stick_y > gamepad1.left_stick_x) {
            m.moveVector(0,-gamepad1.left_stick_y);
        } else {
            m.zeroMotors();
        }
        telemetry.addData("Encoder Sum", m.sumEncoderValues());
        telemetry.update();
    }
}
