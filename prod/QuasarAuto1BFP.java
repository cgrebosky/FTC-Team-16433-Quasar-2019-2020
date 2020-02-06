package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import quasar.subsystems.Collector;
import quasar.subsystems.Lift;
import quasar.subsystems.Mecanum;
import quasar.subsystems.PlatformMover;

@Autonomous(name = "1 Block & Foundation Move", group = "Prod")
public class QuasarAuto1BFP extends LinearOpMode {

    Mecanum m = new Mecanum();
    Collector c = new Collector();
    PlatformMover pf = new PlatformMover();
    Lift l = new Lift();

    ColorSensor colorLeft, colorRight;
    DistanceSensor distance;

    COLOR side = COLOR.BLUE;
    private int colorCoef = 1;
    private enum POSITION {LEFT, CENTER, RIGHT}
    POSITION pos = POSITION.CENTER;
    long delay = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        chooseOptions();

        waitForStart();

        sleep(delay);

        c.open();
        m.fwdBlocks(1, 0.5);

        m.strafeLeftBlock(-0.22 * colorCoef,0.35);

        observeColor();

        m.fwdBlocks(-1.8, 0.7);
        m.turnGlobalDegrees(90 * colorCoef);
        m.fwdBlocks(-4, 0.7);
        m.turnGlobalDegrees(180 * colorCoef);

        fwdToPlatform();

        deliverBlock();

        platformArc();

        m.setPowers(0.5,0,0);
        sleep(1700);
        m.zeroMotors();

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
        else colorCoef = -1;

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
        colorRight = hardwareMap.get(ColorSensor.class, "colorRight");
        distance = hardwareMap.get(DistanceSensor.class, "distance");
    }

    private void checkIfCanceled() {
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            telemetry.addLine("Autonomous Canceled");
            Thread.currentThread().interrupt();
        }
    }

    private void moveCloseToMinerals() {
        m.encoderMode();

        long timeOut = System.currentTimeMillis() + 3000;
        while(distance.getDistance(DistanceUnit.CM) > 6 && System.currentTimeMillis() < timeOut && opModeIsActive()) {
            m.setPowers(0.15, 0,0);
        }
        m.setPowers(0,0,0);

        m.runToPositionMode();
    }
    private void observeColor() {
        // > 35 = yellow, < 30 = black

        moveCloseToMinerals();

        if(colorLeft.red() > 35 && colorRight.red() > 35) pos = POSITION.CENTER;
        if(colorLeft.red() < 30 && colorRight.red() > 35) pos = POSITION.LEFT;
        if(colorLeft.red() > 35 && colorRight.red() < 30) pos = POSITION.RIGHT;

        say(pos);

        if(pos == POSITION.LEFT)  m.strafeLeftBlock(0.3, 0.35);
        if(pos == POSITION.RIGHT) m.strafeLeftBlock(-0.3, 0.35);

        sleep(100);

        c.collect();
        m.fwdBlocks(1.5,0.3);
        if(pos == POSITION.LEFT) m.strafeLeftBlock(-0.3, 0.35);
        if(pos == POSITION.RIGHT) m.strafeLeftBlock(0.3, 0.35);

        c.stop();
        l.closeClaw();
    }

    private void fwdToPlatform() {
        m.setPowers(-0.3,0,0);
        sleep(500);
        pf.lowerHooks();
        sleep(500);
        m.zeroMotors();
    }
    private void deliverBlock() {
        l.liftArms();
        l.extendArm();
        sleep(500);
        l.retractArm();
        l.lowerArms();
    }

    private void platformArc() {
        m.fwdBlocks(1,0.5);
        sleep(500);

        m.setPowers(0.2, 0.2 * colorCoef,0.2 * colorCoef);
        sleep(2300);
        m.zeroMotors();

        m.setPowers(-0.5,0,0);
        sleep(1100);
        pf.liftHooks();

        m.zeroMotors();
    }

}
