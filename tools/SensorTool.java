package quasar.tools;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.CM;

@TeleOp(name = "Sensor Tool", group = "Tools")
public final class SensorTool extends OpMode {

    private DistanceSensor dist, distL, distR;

    @Override
    public void init() {
        dist = hardwareMap.get(DistanceSensor.class, "distance");
        distL = hardwareMap.get(DistanceSensor.class, "distanceL");
    }

    @Override
    public void loop() {
        //Left = 30cm, Center = 50cm, Right = 70cm
        telemetry.addData("Front", dist.getDistance(CM));
        telemetry.addData("Left", distL.getDistance(CM));
        telemetry.update();
    }
}
