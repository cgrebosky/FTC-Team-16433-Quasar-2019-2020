package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.CM;

@TeleOp(name = "Color Sensor Tool", group = "Tools")
@Disabled
public class ColorSensorTool extends OpMode {

    ColorSensor left, right;
    DistanceSensor distance;

    @Override
    public void init() {
        left = hardwareMap.colorSensor.get("colorLeft");
        right = hardwareMap.colorSensor.get("colorRight");

        distance = hardwareMap.get(DistanceSensor.class, "distance");
    }

    @Override
    public void loop() {
        telemetry.addData("Left RGB", left.red() + ", " + left.green() + ", " + left.blue());
        telemetry.addData("Right RGB", right.red() + ", " + right.green() + ", " + right.blue());
        telemetry.addData("Distance (CM)", distance.getDistance(CM));
        telemetry.update();
    }
}
