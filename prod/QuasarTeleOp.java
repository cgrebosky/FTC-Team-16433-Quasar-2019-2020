package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.subsystems.Arms;
import quasar.subsystems.Mecanum;
import quasar.subsystems.PlatformMover;

@TeleOp(name = "Quasar TeleOp", group = "Production")
public class QuasarTeleOp extends OpMode {

    Mecanum m = new Mecanum();
    PlatformMover pm = new PlatformMover();
    Arms a = new Arms();

    @Override
    public void init() {
        m.create(this);
        m.init();
        m.useCompBotConfig();

        pm.create(this);
        pm.init();

        a.create(this);
        a.init();

    }

    @Override
    public void loop() {
        m.loop();
        pm.loop();
        a.loop();

        telemetry.update();
    }

    @Override
    public void stop() {
        m.stop();
        pm.stop();

        super.stop();
    }
}
