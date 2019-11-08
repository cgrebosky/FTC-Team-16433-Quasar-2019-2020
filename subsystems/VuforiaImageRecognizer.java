package quasar.subsystems;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

import quasar.lib.SubSystem;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;

public class VuforiaImageRecognizer extends SubSystem {

    float x = 0, y = 0, z = 0;

    //region Vuforia Variables
    private static final float mmPerInch        = 25.4f;

    // Constant for Stone Target
    private static final float stoneZ = 2.00f * mmPerInch;
    
    private static final String VUFORIA_KEY =
            "AYBNsh3/////AAABmUprTCAGrUL7h8odXetdmu1r1oSQv23+Msoyvu1ArLHWA3Sm2bnZ+0sICt5iRYmEcpqRaMLrN0L1h1oQ25TuZZprUiFU2qcf3lqvwaZWDpocwbtc5Kry55NqesfKgDCa/Sjcd5dkwYbwT858hsg9FnV1wZ73KNyJsek9LdqhT7GI8EUmZsGdjgysyN2z57IpvSS/0JydDjY3u+X7oRgWlIR2qfkZJbOf1jqv35hP2R9YqLCIyDvFriMLn+EIy/Ho/JqQuBsfZEJ9U6z14IIniAwfHQ7ZffhfPDx2k1MquqHzZVU0jX5ry6sN5RoKRUrsFfoumfwQI7XX3oG/o9UtIiUpjBzBOxjqFhPFnfttXvcu";

    private OpenGLMatrix lastLocation = null;
    private VuforiaLocalizer vuforia  = null;
    private boolean targetVisible     = false;
    
    private float phoneXRotate        = 90; //PORTRAIT
    private float phoneYRotate        = 90; //FRONT CAMERA
    private float phoneZRotate        = 0;
    final float CAMERA_FORWARD_DISPLACEMENT  = 0 * mmPerInch;   // eg: Camera is 4 Inches in front of robot center
    final float CAMERA_VERTICAL_DISPLACEMENT = 0 * mmPerInch;   // eg: Camera is 8 Inches above ground
    final float CAMERA_LEFT_DISPLACEMENT     = 0 * mmPerInch;

    VuforiaTrackable stoneTarget = null;
    //endregion

    @Override public void init() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection   = FRONT;
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        VuforiaTrackables targets = this.vuforia.loadTrackablesFromAsset("Skystone");

        stoneTarget = targets.get(0);
        stoneTarget.setName("Stone Target");
        
        stoneTarget.setLocation(OpenGLMatrix
                .translation(0, 0, stoneZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        OpenGLMatrix robotFromCamera = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));
        

        ((VuforiaTrackableDefaultListener) stoneTarget.getListener()).setPhoneInformation(robotFromCamera, parameters.cameraDirection);

        targets.activate();
    }
    @Override public void teleInit() {

    }
    @Override public void loop() {

    }
    @Override public void stop() {
    }
    @Override protected void telemetry() {
        opm.telemetry.addData("X Position", x);
        opm.telemetry.addData("Y Position", y);
        opm.telemetry.addData("Z Position", z);
    }

    public void detectLoop() {
        targetVisible = false;
        if (((VuforiaTrackableDefaultListener)stoneTarget.getListener()).isVisible()) {
            targetVisible = true;

            OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)stoneTarget.getListener()).getUpdatedRobotLocation();
            if (robotLocationTransform != null) {
                lastLocation = robotLocationTransform;
            }
        }

        VectorF pos = new VectorF(0,0,0);
        if(lastLocation != null) pos = lastLocation.getTranslation();

        opm.telemetry.addData("X Position", pos.get(0));
        opm.telemetry.addData("Y Position", pos.get(1));
        opm.telemetry.addData("Z Position", pos.get(2));

        opm.telemetry.update();
    }
}
