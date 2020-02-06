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

    }
}
