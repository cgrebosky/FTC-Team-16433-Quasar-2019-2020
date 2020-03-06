package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import quasar.subsystems.SubSystem;

@Autonomous(name = "2 Block & Platform Autonomous", group = "Prod")
public final class Autonomous2BP extends LinearOpMode {


    @Override
    public void runOpMode() throws InterruptedException {
        Robot.create(this);
        Robot.autoInit();

        waitForStart();

        Robot.fwdTicks(1000,0);
        Robot.fwdTicks(-1000,0,0.5);
        Robot.strafeTicks(1000,0,0.5);
        Robot.strafeTicks(-1000, 0, 0.5);
        Robot.FRDiagonalTicks(1000, 0, 0.5);
    }
}
