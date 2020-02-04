package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import quasar.subsystems.Collector;
import quasar.subsystems.Lift;
import quasar.subsystems.Mecanum;
import quasar.subsystems.PlatformMover;

@Autonomous(name = "Quasar Autonomous", group = "Prod")
public class QuasarAutonomous extends LinearOpMode {

    Mecanum m = new Mecanum();
    Collector c = new Collector();
    PlatformMover pf = new PlatformMover();
    Lift l = new Lift();

    ColorSensor colorLeft, colorRight;
    DistanceSensor distance;

    private enum COLOR { RED, BLUE }
    COLOR side = COLOR.BLUE;
    int colorCoef = 1;
    long delay = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        chooseOptions();

        waitForStart();

        sleep(delay);

        m.fwdBlocks(1, 0.5);

        sleep(100);

        m.strafeLeftBlock(-1,0.3);



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

            telemetry.addLine("Press A to select the COLOR, and X to change sides");
            telemetry.addData("Current option", side);
            telemetry.update();
        }
        while(gamepad1.a) {
            checkIfCanceled();
        } //Make sure we stop pressing a before continuing

        boolean prevUp = true, prevDown = true;
        while(!gamepad1.a) {
            checkIfCanceled();
            if(gamepad1.dpad_up && !prevUp) delay += 500;
            if(gamepad1.dpad_down && !prevDown) delay -= 500;
            if(delay < 0) delay = 0;
            prevDown = gamepad1.dpad_down;
            prevUp = gamepad1.dpad_up;

            telemetry.addLine("Press A to select the DELAY, and DPAD to change");
            telemetry.addLine("Current option: " + ( (double) delay) / 1000.0 + " seconds");
            telemetry.update();
        }

        if(side == COLOR.RED) colorCoef = 1;
        else colorCoef = 0;

        telemetry.addData("Side", side);
        telemetry.addData("Delay", ( (double) delay) / 1000.0 + " seconds");
        telemetry.update();
    }
    private void initialize() {
        m.create(this);
        m.autoInit();
        m.useCompBotConfig();

        c.create(this);
        c.init();

        pf.create(this);
        pf.init();

        l.create(this);
        l.init();

        colorLeft = hardwareMap.get(ColorSensor.class, "colorLeft");
        distance = hardwareMap.get(DistanceSensor.class, "colorLeft");
        colorRight = hardwareMap.get(ColorSensor.class, "colorRight");
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
