package quasar.testing.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import quasar.lib.GamepadState;
import quasar.lib.SubSystem;
import quasar.subsystems.Mecanum;


@Autonomous(name = "Mecanum Auto Test")
public class MecanumAuto extends LinearOpMode {

    Mecanum m = new Mecanum();

    @Override
    public void runOpMode() {
        m.create(this);
        m.autoInit();
        m.useCompBotConfig();

        waitForStart();

        m.turnGlobalDegrees(90);
        m.turnGlobalDegrees(0);

        m.turnGlobalDegrees(180);


    }
}
