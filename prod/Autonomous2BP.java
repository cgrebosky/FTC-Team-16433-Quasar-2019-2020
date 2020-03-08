package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import quasar.lib.macro.PartialMacroPlayer;

@Autonomous(name = "2 Block & Platform Autonomous", group = "Prod")
public final class Autonomous2BP extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Robot.create(this);
        Robot.autoInit();

        waitForStart();

        Robot.fwdTicks(450,0);
        Robot.miscLateInit();
        Robot.getPosition();
        Robot.collect1stBlock();
        Robot.deliver1stBlock();
        Robot.collect2ndBlock();
        Robot.deliver2ndBlock();
        Robot.movePlatform();
    }
}
