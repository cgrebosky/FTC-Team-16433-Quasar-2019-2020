package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import quasar.lib.AutoTransitioner;
import quasar.lib.macro.PartialMacroPlayer;

import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.CM;


@Autonomous(name = "3 Block Autonomous", group = "prod")
public final class Autonomous3B extends LinearOpMode {

    private PartialMacroPlayer pm;

    private ColorSensor color;
    private DistanceSensor distanceCol, distance;

    private long delayMS = 0;

    @Override
    public void runOpMode() {

        chooseOptions();
        initMiscellaneous();

        telemetry.addLine("All Subsystems initialized successfully :D");
        telemetry.addData("Side", Robot.s == Side.RED ? "RED" : "BLUE");
        telemetry.addData("Delay (ms)", delayMS);
        telemetry.update();

        waitForStart();
        say("Started");

        Robot.strafeTicks(1250,0);
        Robot.strafeUntilCloseToBlock();
        Robot.collectSkystone();
        Robot.strafeTicks(-300,0);
        Robot.fwdTicks(-3000,0);

        while(opModeIsActive()) {
            telemetry.addData("RED", Robot.color.red());
            telemetry.addData("DIS", Robot.distCol.getDistance(CM));
            telemetry.update();
        }
        /*
        Robot.strafeTicks(1350, 0);
        Robot.findBlock(color, distanceCol, side);
        Robot.fwdTicks(-3400, 0);
        Robot.strafeTicks(300,0);
        Robot.releaseBlock(side);
        Robot.strafeTicks(-400,0);
        Robot.fwdUntilAtWall(distance);
        Robot.fwdTicks(100, 0);
        Robot.strafeUntilCloseToBlock(distanceCol, side);
        Robot.findBlock(color, distanceCol, side);
        Robot.strafeTicks(-100, 0);
        Robot.fwdTicks(-5000, 0);
        Robot.strafeTicks(300,0);
        Robot.releaseBlock(side);
        Robot.strafeTicks(-300, 0);
        pm.playMacro();
         */
    }

    private void chooseOptions() {

        boolean prevX = true;
        while(!isStopRequested() && (!gamepad1.a || gamepad1.start)) {
            if(gamepad1.x && !prevX) Robot.s = Robot.s.swap();
            prevX = gamepad1.x;

            telemetry.addLine("Which side are you on?");
            telemetry.addData("Current Side", Robot.s);
            telemetry.addLine("Press [X] to switch, [A] to select");
            telemetry.update();
        }

        while(gamepad1.a && !isStopRequested()) idle(); //This ensures that we the second loop runs / you press A twice

        boolean prevDPadPressed = true;
        while(!isStopRequested() && !gamepad1.a) {
            if(gamepad1.dpad_up && !prevDPadPressed) delayMS += 250;
            if(gamepad1.dpad_down && !prevDPadPressed) delayMS -= 250;
            prevDPadPressed = gamepad1.dpad_up || gamepad1.dpad_down;

            telemetry.addLine("How much delay do you want initially?");
            telemetry.addData("Delay (ms)", delayMS);
            telemetry.addLine("Use [DPAD] to add/subtract delay.  Press [A] to select");
            telemetry.update();
        }
    }
    private void initMiscellaneous() {
        Robot.create(this);
        Robot.autoInit();
        say("Robot initialized");

        pm = new PartialMacroPlayer(
                this,
                "AUTO Platform Mover"
        );
        pm.init();
        say("Macro Ready");

        Robot.initSensors();
        say("Sensors Ready");

        AutoTransitioner.transitionOnStop(this, "Quasar TeleOp");
        say("Transitioner Ready");
    }

    private void say(Object o) {
        telemetry.addLine(o.toString());
        telemetry.update();
        sleep(50); //lol I just like being able to see all the telemetry in succession.  It's so pretty!
    }
}
