package quasar.prod;

import android.graphics.Paint;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.subsystems.CapstoneDepositor;
import quasar.subsystems.Collector;
import quasar.subsystems.Lift;
import quasar.subsystems.Mecanum;
import quasar.subsystems.PlatformMover;

@TeleOp(name = "Quasar TeleOp", group = "Prod")
public class QuasarTeleOp extends OpMode {

    private Collector c     = new Collector();
    private Lift l          = new Lift();
    private Mecanum m       = new Mecanum();
    private PlatformMover p = new PlatformMover();
    private CapstoneDepositor cd = new CapstoneDepositor();

    @Override
    public void init() {
        c.create(this);
        l.create(this);
        m.create(this);
        p.create(this);
        cd.create(this);

        c.init();
        l.init();
        m.init();
        p.init();
        cd.init();
    }

    @Override
    public void loop() {
        c.loop();
        l.loop();
        m.loop();
        p.loop();
        cd.loop();

        telemetry.update();
    }

    @Override
    public void stop() {
        c.stop();
        l.stop();
        m.stop();
        p.stop();
        cd.stop();
    }
}
