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
        //region Pre-Autonomous Choices
        boolean prevX = true;
        while(!gamepad1.a && !isStopRequested()) {
            if(gamepad1.x && !prevX) side = (side == COLOR.RED)?COLOR.BLUE:COLOR.RED;
            prevX = gamepad1.x;

            telemetry.addLine("Press A to select, and X to change sides");
            telemetry.addData("Current option", side);
            telemetry.update();
        }
        while(gamepad1.a && !isStopRequested()); //Make sure we stop pressing a before continuing

        long delay = 0;
        boolean prevUp = true, prevDown = true;
        while(!gamepad1.a && !isStopRequested()) {
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
        //endregion
        waitForStart();
        while(opModeIsActive()) {
            //region Autonomous Code
            sleep(delay);

            adjustStartPosition();

            m.moveVectorTime(0, 0.6, 400);

            moveForwardUntilClose();

            adjustPositionInFrontOfBlock();

            c.collect();
            m.moveVectorTime(0, 0.4, 1200);
            c.zeroMotors();
            m.moveVectorTime(0, -0.4, 1700);
            turnDegrees(-90);

            m.moveVectorTime(0, 0.8, 1000);
            m.stop();
            c.push();
            sleep(1000);
            c.zeroMotors();
            turnDegrees(180);

            m.moveVectorTime(0, 0.8, 115);
            moveForwardUntilClose();
            adjustAngleToStraight();

            m.moveVectorTime(0, -0.3, 400);

            turnDegrees(-90);
            moveForwardUntilClose();

            adjustPositionInFrontOfBlock();
            c.collect();
            m.moveVectorTime(0, 0.4, 1200);
            c.zeroMotors();
            m.moveVectorTime(0, -0.4, 1700);
            turnDegrees(-90);
            m.moveVectorTime(0, 0.8, 1300);
            c.push();
            sleep(10000);
            //endregion
        }
    }
    private boolean leftIsBlack() {
        return colorLeft.red() < 30;
    }
    private boolean rightIsBlack() {
        return colorRight.red() < 30;
    }
    private void moveForwardUntilClose() {
        while((Double.isNaN(distanceLeft.getDistance(DistanceUnit.CM)) || distanceLeft.getDistance(DistanceUnit.CM) > 25
                || Double.isNaN(distanceRight.getDistance(DistanceUnit.CM)) || distanceRight.getDistance(DistanceUnit.CM) > 25) && opModeIsActive()) {
            m.moveVector(0, 0.2);
            say(distanceLeft.getDistance(DistanceUnit.CM));
        }
        m.zeroMotors();
    }

    private void adjustPositionInFrontOfBlock() {
        if(leftIsBlack()) {
            m.moveVectorTime(-0.5, 0, 650); //LEFT
        }
        else if(rightIsBlack()) {
            m.moveVectorTime(0.5, 0, 650); //RIGHT
        }
        m.stop();
    }
    private void adjustAngleToStraight() {

        long endTime = System.currentTimeMillis() + 1000;
        while(System.currentTimeMillis() < endTime) {
            double left  = distanceLeft.getDistance(DistanceUnit.CM);
            double right = distanceRight.getDistance(DistanceUnit.CM);
            left = Double.isNaN(left)?100:left;
            right = Double.isNaN(right)?100:right;

            double diff = left - right;

            double pwr = Math.min(0.1, diff / 50);
            m.turnPwr(pwr);
        }

        m.zeroMotors();
    }

    private void turnDegrees(double deg) {
        if(side == COLOR.RED) m.turnDegrees(deg);
        else if(side == COLOR.BLUE) m.turnDegrees(-deg);
    }
    private void adjustStartPosition() {
        double pow = side==COLOR.RED?0.3:-0.3;
        m.moveVectorTime(pow, 0, 680); //Adjust position so we can start at an easily rememberable place
    }

    private void say(Object m) {
        telemetry.addLine(""+m);
        telemetry.update();
    }
}
