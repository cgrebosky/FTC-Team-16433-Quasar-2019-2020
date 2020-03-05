package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import quasar.lib.macro.PartialMacroPlayer;

@Autonomous(name = "Collector Autonomous", group = "Prod")
public final class AutonomousCollector extends LinearOpMode {

    PartialMacroPlayer pm = new PartialMacroPlayer(this, "AUTO Block Delivery 1");

    @Override
    public void runOpMode() throws InterruptedException {
        Robot.create(this);
        Robot.autoInit();

        pm.init();

        waitForStart();


        Robot.getPosition();
        Robot.miscLateInit();
        Robot.collect1();
        Robot.deliver1stBlock();
        sleep(3000); //Represents our macro :/
        Robot.goBackFor2ndBlock();
        Robot.collectSecond();
        Robot.deliver2ndBlock();

        Robot.imu.interrupt();
        Robot.tfs.interrupt();

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
