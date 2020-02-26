package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;

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

        //If we put the multithreaded systems first, that gives it a bit more time to start up
        i.create(this, false);
        i.start();
        say("IMU Ready");
        v.create(this, false);
        v.start();
        say("Vuforia Ready");

        c.create(this);
        c.init();
        say("Collector Ready");
        m.create(this);
        m.init();
        say("Mecanum Ready");
        l.create(this);
        l.init();
        say("Lift Ready");
        p.create(this);
        p.init();
        say("Platform Mover Ready");

        say("All Subsystems initialized successfully\n\nWaiting for Start");
        waitForStart();
        say("Started");

        m.strafeTicks(1500, 0, i);
        m.strafeTicks(-400, 0, i);
        m.fwdTicks(-3800, 0, i);
        m.fwdTicks(4000, 0, i);

    }

    private void say(Object o) {
        telemetry.addLine(o.toString());
        telemetry.update();
    }
}
