package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.subsystems.threaded.VuforiaPositionDetector;

@TeleOp(name = "Vuforia Position Tool", group = "Tests")
@Disabled
public final class VuforiaTool extends LinearOpMode {
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