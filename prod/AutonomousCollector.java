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

        Robot.getPosition();
        Robot.moveCapstoneOutOfWay();
        Robot.collect();
        Robot.fwdToBlock();
        Robot.fwdTicksSlow(500,0);
        Robot.fwdTicks(-750,0);

        while(opModeIsActive()) {
            telemetry.addData("Pos", Robot.pb);
            telemetry.update();
        }

        //L>350 = C
        //L in 350-150 = L

        //FWD 1500
        //L = -280, C = 200, R = 650
    }

    private void say(Object o) {
        telemetry.addLine(o.toString());
        telemetry.update();
    }
}
