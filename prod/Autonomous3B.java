package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import quasar.subsystems.*;
import quasar.subsystems.threaded.*;

@Autonomous(name = "3 Block Autonomous", group = "prod")
public final class Autonomous3B extends LinearOpMode {

    private Collector c     = new Collector();
    private Mecanum m       = new Mecanum();
    private Lift l          = new Lift();
    private PlatformMover p = new PlatformMover();

    private IMUHandler i              = new IMUHandler();
    private VuforiaPositionDetector v = new VuforiaPositionDetector();

    @Override
    public void runOpMode() {

        c.create(this);
        c.init();
        m.create(this);
        m.init();
        l.create(this);
        l.init();
        p.create(this);
        p.init();

        i.create(this, false);
        i.start();
        v.create(this, false);
        v.start();

        waitForStart();

        m.fwdTicks(1000, 0, i);

        while(opModeIsActive()) telemetry.update();


    }


}
