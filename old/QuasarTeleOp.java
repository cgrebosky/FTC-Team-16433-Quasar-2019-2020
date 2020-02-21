package quasar.old;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.old.subsystems.Collector;
import quasar.old.subsystems.Lift;
import quasar.old.subsystems.Mecanum;
import quasar.old.subsystems.PlatformMover;

@TeleOp(name = "Quasar TeleOp", group = "Production")
@Disabled
public class QuasarTeleOp extends OpMode {

    Mecanum m = new Mecanum();
    PlatformMover pm = new PlatformMover();
    Collector c = new Collector();
    Lift l = new Lift();

    @Override
    public void init() {
        m.create(this);
        m.init();
        m.useCompBotConfig();

        pm.create(this);
        pm.init();

        c.create(this);
        c.init();

        l.create(this);
        l.init();
    }

    @Override
    public void loop() {
        m.loop();
        pm.loop();
        c.loop();
        l.loop();

        telemetry.update();
    }

    @Override
    public void stop() {
        m.stop();
        pm.stop();
        c.stop();
        l.stop();

        super.stop();
    }
}
