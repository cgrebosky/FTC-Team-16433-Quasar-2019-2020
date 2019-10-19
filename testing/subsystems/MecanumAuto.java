package quasar.testing.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import quasar.subsystems.Mecanum;

public class MecanumAuto extends LinearOpMode {

    Mecanum m = new Mecanum();

    @Override
    public void runOpMode() throws InterruptedException {
        m.create(this);
        m.init();
        m.useCompBotConfig();

        waitForStart();

        m.turnDegrees(45);

        m.moveAngle(-45);

    }
}
