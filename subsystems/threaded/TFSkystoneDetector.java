package quasar.subsystems.threaded;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

import quasar.subsystems.ThreadSubSystem;

public class TFSkystoneDetector extends ThreadSubSystem {

    private static final String TFOD_MODEL_ASSET = "Skystone.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Stone";
    private static final String LABEL_SECOND_ELEMENT = "Skystone";

    private static final String VUFORIA_KEY =
            "AYBNsh3/////AAABmUprTCAGrUL7h8odXetdmu1r1oSQv23+Msoyvu1ArLHWA3Sm2bnZ+0sICt5iRYmEcpqRaMLrN0L1h1oQ25TuZZprUiFU2qcf3lqvwaZWDpocwbtc5Kry55NqesfKgDCa/Sjcd5dkwYbwT858hsg9FnV1wZ73KNyJsek9LdqhT7GI8EUmZsGdjgysyN2z57IpvSS/0JydDjY3u+X7oRgWlIR2qfkZJbOf1jqv35hP2R9YqLCIyDvFriMLn+EIy/Ho/JqQuBsfZEJ9U6z14IIniAwfHQ7ZffhfPDx2k1MquqHzZVU0jX5ry6sN5RoKRUrsFfoumfwQI7XX3oG/o9UtIiUpjBzBOxjqFhPFnfttXvcu";

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    private double x = 0, y = 0;
    private boolean skystoneIsVisible = false;

    //region Subsystem
    @Override
    protected void _init() {
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) initTfod();
        else telemetry.addData("Sorry!", "This device is not compatible with TFOD");

        if (tfod != null) tfod.activate();
    }

    @Override
    protected void _loop() {
        if (tfod != null) {
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                skystoneIsVisible = false;
                for (Recognition r : updatedRecognitions) {
                    if(r.getLabel().equals("Skystone") && r.getTop() > 750) {
                        x = r.getLeft();
                        y = r.getTop();
                        skystoneIsVisible = true;
                    }
                }
            }
        }
    }

    @Override
    protected void _stop() {
        tfod.shutdown();
    }

    @Override
    protected void _telemetry() {
        telemetry.addData("(x ,y)","(" + x + ", " + y + ")");
    }
    //endregion

    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;

        vuforia = ClassFactory.getInstance().createVuforia(parameters);
        // Loading trackables is not necessary for the TensorFlow Object Detection engine :D
    }
    private void initTfod() {
        int tfodMonitorViewId = hmap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hmap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.5;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }

    public synchronized double getX() {
        return x;
    }
    public synchronized double getY() {
        return y;
    }
    public synchronized boolean isSkystoneIsVisible() {
        return skystoneIsVisible;
    }
}
