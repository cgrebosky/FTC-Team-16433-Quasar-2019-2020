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

    @Override
    public void runOpMode() throws InterruptedException {
        m.create(this);
        m.init();
        m.useCompBotConfig();

        c.create(this);
        c.init();

        colorLeft = hardwareMap.get(ColorSensor.class, "colorLeft");
        distanceLeft = hardwareMap.get(DistanceSensor.class, "colorLeft");
        colorRight = hardwareMap.get(ColorSensor.class, "colorRight");
        distanceRight = hardwareMap.get(DistanceSensor.class, "colorRight");

        waitForStart();

        m.moveVectorTime(0.3, 0, 680); //Adjust position so we can start at an easily rememberable place

        moveForwardUntilClose();

        adjustPositionInFrontOfBlock();

        c.collect();
        m.moveVectorTime(0, 0.4, 1500);
        sleep(500);
        c.zeroMotors();
        m.moveVectorTime(0, -0.4, 1900);
        m.turnDegrees(-90);

        m.moveVectorTime(0, 1, 700);
        m.stop();
        c.push();
        sleep(1000);
        c.zeroMotors();
        m.turnDegrees(180);

        m.moveVectorTime(0, 0.8, 1250);
        moveForwardUntilClose();
        adjustAngleToStraight();

        m.moveVectorTime(0, -0.3, 400);

        m.turnDegrees(-90);
        moveForwardUntilClose();

        adjustPositionInFrontOfBlock();
        c.collect();
        m.moveVectorTime(0, 0.4, 1500);
        sleep(500);
        c.zeroMotors();
        m.moveVectorTime(0, -0.4, 1700);
        m.turnDegrees(-90);
        m.moveVectorTime(0, 0.8, 1300);
        c.push();


    }
    private boolean leftIsBlack() {
        return colorLeft.red() < 30;
    }
    private boolean rightIsBlack() {
        return colorRight.red() < 30;
    }
    private void moveForwardUntilClose() {
        while((Double.isNaN(distanceLeft.getDistance(DistanceUnit.CM))|| distanceLeft.getDistance(DistanceUnit.CM) > 15) && opModeIsActive()) {
            m.moveVector(0, 0.2);
            say(distanceLeft.getDistance(DistanceUnit.CM));
        }
        m.zeroMotors();
    }
    private void moveForwardFastUntilClose() {
        while((Double.isNaN(distanceLeft.getDistance(DistanceUnit.CM))|| distanceLeft.getDistance(DistanceUnit.CM) > 60) && opModeIsActive()) {
            m.moveVector(0, 0.4);
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

        long endTime = System.currentTimeMillis() + 2000;
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

    private void say(Object m) {
        telemetry.addLine(""+m);
        telemetry.update();
    }
}
