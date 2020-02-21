package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import quasar.lib.MoreMath;
import quasar.threadsubsystems.Mecanum;
import quasar.threadsubsystems.PlatformMover;
import quasar.threadsubsystems.PositionDetector;

@Autonomous(name = "3 Blck Autonomous", group = "prod")
public final class Autonomous3B extends LinearOpMode {

    Mecanum m           = new Mecanum();
    PlatformMover pf    = new PlatformMover();
    PositionDetector pd = new PositionDetector();

    @Override
    public void runOpMode() throws InterruptedException {
        m.create(this, true);
        m.start();

        pf.create(this, true);
        pf.start();

        pd.create(this, false);
        pd.start();

        waitForStart();

    }


}
