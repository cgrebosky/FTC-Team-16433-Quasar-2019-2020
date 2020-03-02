package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import quasar.prod.Side;
import quasar.subsystems.AutoBlockMover;
import quasar.subsystems.Mecanum;

@Autonomous(name = "AutoBlockMover Tool", group = "Tools")
public final class AutoBlockMoverTool extends LinearOpMode {
    AutoBlockMover ab = new AutoBlockMover();
    Mecanum me = new Mecanum();

    @Override
    public void runOpMode() throws InterruptedException {
        ab.create(this);
        ab.init();
        me.create(this);
        me.init();

        waitForStart();

        ab.halfLower(Side.RED);
        sleep(800);
        ab.close(Side.RED);
        sleep(300);
        ab.raise(Side.RED);
        sleep(500);
        ab.release(Side.RED);

    }
}
