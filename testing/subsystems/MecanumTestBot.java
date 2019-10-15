package quasar.testing.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.subsystems.Mecanum;

@TeleOp(name = "Mecanum Test (Test Bot)", group = "Testing")
public class MecanumTestBot extends OpMode {

    Mecanum m = new Mecanum();

    @Override
    public void init() {
        m.create(this);
        m.init();
        m.useTestBotConfig();
    }

    @Override
    public void loop() {
        m.loop();

        telemetry.update();
    }
}