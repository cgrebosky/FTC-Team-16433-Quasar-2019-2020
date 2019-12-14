package quasar.testing.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.subsystems.Mecanum;

@Disabled
@TeleOp(name = "Mecanum Test (Competition)", group = "Testing")
public class MecanumCompBot extends OpMode {

    Mecanum m = new Mecanum();

    @Override
    public void init() {
        m.create(this);
        m.init();
        m.useCompBotConfig();
    }

    @Override
    public void loop() {
        m.loop();

        telemetry.update();
    }


}