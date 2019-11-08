package quasar.testing.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import quasar.subsystems.VuforiaImageRecognizer;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

@Autonomous(name = "VF Image Recognition Test", group = "Testing")
public class VuforiaImageRecognizerTest extends LinearOpMode {

    VuforiaImageRecognizer vf = new VuforiaImageRecognizer();

    @Override
    public void runOpMode() throws InterruptedException {
        vf.create(this);
        vf.init();

        waitForStart();

        while (opModeIsActive()) {
            vf.detectLoop();



            telemetry.update();
        }
    }
}
