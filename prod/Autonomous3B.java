package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import quasar.threadsubsystems.Mecanum;
import quasar.threadsubsystems.PlatformMover;
import quasar.threadsubsystems.VuforiaPositionDetector;

@Autonomous(name = "3 Blck Autonomous", group = "prod")
public final class Autonomous3B extends LinearOpMode {

    private Mecanum m           = new Mecanum();
    private PlatformMover pf    = new PlatformMover();
    private VuforiaPositionDetector pd = new VuforiaPositionDetector();

    @Override
    public void runOpMode() {
        m.create(this, true);
        m.start();

        pf.create(this, true);
        pf.start();

        pd.create(this, false);
        pd.start();

        waitForStart();

    }


}
