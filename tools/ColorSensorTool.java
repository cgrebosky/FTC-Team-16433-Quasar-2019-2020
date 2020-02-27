package quasar.tools;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.CM;

@TeleOp(name = "Color Sensor Tool", group = "Tools")
@Disabled
public final class ColorSensorTool extends OpMode {

    private ColorSensor left, right;
    private DistanceSensor distance, leftDist, rightDist;

    @Override
    public void init() {
        left = hardwareMap.colorSensor.get("colorLeft");
        right = hardwareMap.colorSensor.get("colorRight");

        leftDist = hardwareMap.get(DistanceSensor.class, "colorLeft");
        rightDist = hardwareMap.get(DistanceSensor.class, "colorRight");

        distance = hardwareMap.get(DistanceSensor.class, "distance");
    }

    @Override
    public void loop() {
        //40- = black, 40-50 = nothing, 50+ = yellow
        telemetry.addData("Left RGB", left.red() + ", " + left.green() + ", " + left.blue());
        telemetry.addData("Left Distance", leftDist.getDistance(CM));
        telemetry.addData("Right RGB", right.red() + ", " + right.green() + ", " + right.blue());
        telemetry.addData("Right Distance", rightDist.getDistance(CM));
        telemetry.addData("Distance (CM)", distance.getDistance(CM));
        telemetry.update();
    }
}
