package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.threadsubsystems.Collector;
import quasar.threadsubsystems.Lift;
import quasar.threadsubsystems.Mecanum;
import quasar.threadsubsystems.PlatformMover;

@TeleOp(name = "Quasar TeleOp Threaded", group = "Prod")
public final class QuasarTeleOpThread extends LinearOpMode {

    private Collector c      = new Collector();
    private PlatformMover pf = new PlatformMover();
    private Lift l           = new Lift();
    private Mecanum m        = new Mecanum();

    @Override
    public void runOpMode() {
        c.create(this, false);
        c.disableTelemetry();
        c.start();

        pf.create(this, false);
        pf.disableTelemetry();
        pf.start();

        l.create(this, false);
        l.disableTelemetry();
        l.start();

        m.create(this, false);
        m.start();

        waitForStart();

        while(opModeIsActive()) idle();
    }
}
