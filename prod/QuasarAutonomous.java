package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import quasar.subsystems.Collector;
import quasar.subsystems.Mecanum;

@Autonomous(name = "Quasar Autonomous", group = "Prod")
public class QuasarAutonomous extends LinearOpMode {

    Mecanum m = new Mecanum();
    Collector c = new Collector();

    ColorSensor colorLeft, colorRight;
    DistanceSensor distanceLeft, distanceRight;

    private enum COLOR { RED, BLUE }
    COLOR side = COLOR.BLUE;

    @Override
    public void runOpMode() throws InterruptedException {
        //region Initialization
        m.create(this);
        m.init();
        m.useCompBotConfig();

        c.create(this);
        c.init();

        colorLeft = hardwareMap.get(ColorSensor.class, "colorLeft");
        distanceLeft = hardwareMap.get(DistanceSensor.class, "colorLeft");
        colorRight = hardwareMap.get(ColorSensor.class, "colorRight");
        distanceRight = hardwareMap.get(DistanceSensor.class, "colorRight");
        //endregion
        chooseOptions();
        waitForStart();
        while(opModeIsActive()) {

        }
    }
    private void say(Object m) {
        telemetry.addLine(""+m);
        telemetry.update();
    }
    private void chooseOptions() {
        boolean prevX = true;
        while(!gamepad1.a) {
            checkIfCanceled();
            if(gamepad1.x && !prevX) side = (side == COLOR.RED)?COLOR.BLUE:COLOR.RED;
            prevX = gamepad1.x;

            telemetry.addLine("Press A to select, and X to change sides");
            telemetry.addData("Current option", side);
            telemetry.update();
        }
        while(gamepad1.a) {
            checkIfCanceled();
        } //Make sure we stop pressing a before continuing

        long delay = 0;
        boolean prevUp = true, prevDown = true;
        while(!gamepad1.a) {
            checkIfCanceled();
            if(gamepad1.dpad_up && !prevUp) delay += 500;
            if(gamepad1.dpad_down && !prevDown) delay -= 500;
            if(delay < 0) delay = 0;
            prevDown = gamepad1.dpad_down;
            prevUp = gamepad1.dpad_up;

            telemetry.addLine("Press A to select, and DPAD to change");
            telemetry.addLine("Current option: " + ( (double) delay) / 1000.0 + " seconds");
            telemetry.update();
        }

        telemetry.addData("Side", side);
        telemetry.addData("Delay", ( (double) delay) / 1000.0 + " seconds");
        telemetry.update();
    }

    private void checkIfCanceled() {
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            telemetry.addLine("Autonomous Canceled");
            Thread.currentThread().interrupt();
        }
    }
}
