package quasar.testing.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.threadsubsystems.PositionDetector;

@TeleOp(name = "Position Getter", group = "Tests")
public class VuforiaRecognizerTest extends LinearOpMode {
    PositionDetector vf = new PositionDetector();

    @Override
    public void runOpMode() throws InterruptedException {

        vf.create(this, false);
        vf.enableTelemetry();
        vf.start();

        waitForStart();

        while(opModeIsActive());

    }
}
