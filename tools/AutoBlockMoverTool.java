package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.prod.Side;
import quasar.subsystems.AutoBlockMover;
import quasar.subsystems.Mecanum;

@TeleOp(name = "AutoBlockMover Tool", group = "Tools")
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
        sleep(3000);
        ab.close(Side.RED);
        sleep(200);
        ab.raise(Side.RED);

    }
}
