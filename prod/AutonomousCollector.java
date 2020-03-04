package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Collector Autonomous", group = "Prod")
public final class AutonomousCollector extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Robot.create(this);
        Robot.autoInit();

        waitForStart();

        Robot.fwdTicks(1000,0);
        Robot.fwdToBlocks() {

        }
    }

    private void say(Object o) {
        telemetry.addLine(o.toString());
        telemetry.update();
    }
}
