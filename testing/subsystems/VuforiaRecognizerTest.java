package quasar.testing.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.threadsubsystems.VuforiaPositionDetector;

@TeleOp(name = "Position Getter", group = "Tests")
public class VuforiaRecognizerTest extends LinearOpMode {
    private VuforiaPositionDetector vf = new VuforiaPositionDetector();

    @Override
    public void runOpMode() {

        vf.create(this, false);
        vf.enableTelemetry();
        vf.start();

        waitForStart();

        while(opModeIsActive()) idle();

    }
}
