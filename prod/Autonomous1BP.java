package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import quasar.lib.macro.PartialMacroPlayer;

import static quasar.prod.Robot.fwdTicks;

@Autonomous(name = "1 Block & Platform Autonomous", group = "Prod")
public final class Autonomous1BP extends LinearOpMode {

    PartialMacroPlayer pm = new PartialMacroPlayer(this, "AUTO Block Delivery 1");

    @Override
    public void runOpMode() throws InterruptedException {
        Robot.create(this);
        Robot.autoInit();

        pm.init();

        waitForStart();

        Robot.fwdTicks(550,0);
        Robot.getPosition();
        Robot.miscLateInit();
        Robot.collect1();
        Robot.deliver1();
        pm.playMacro();

        //L>350 = C
        //L in 350-150 = L

        //FWD 1500
        //L = -280, C = 200, R = 650
    }
}
