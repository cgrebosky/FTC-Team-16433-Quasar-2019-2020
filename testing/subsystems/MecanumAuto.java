package quasar.testing.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import quasar.lib.GamepadState;
import quasar.lib.SubSystem;
import quasar.subsystems.Mecanum;


@Autonomous(name = "Mecanum Auto Test")
public class MecanumAuto extends LinearOpMode {

    //Mecanum m = new Mecanum();

    @Override
    public void runOpMode() {
        //m.create(this);
        //m.init();
        //m.useTestBotConfig();

        boolean value = false;
        boolean prevX = true;
        while(!isStarted() && !gamepad1.a) {
            try {
                value = GamepadState.toggle(gamepad1.x, prevX, value);
                prevX = gamepad1.x;
                Thread.sleep(11);
                telemetry.addData("state", value);
                telemetry.update();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        telemetry.addLine("READY");
        telemetry.update();

        waitForStart();
        telemetry.addLine("STARTED");
        telemetry.update();

        sleep(1000);


    }
}
