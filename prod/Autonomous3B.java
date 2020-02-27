package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

import quasar.lib.AutoTransitioner;
import quasar.lib.macro.PartialMacroPlayer;
import quasar.subsystems.*;
import quasar.subsystems.threaded.*;

@Autonomous(name = "3 Block Autonomous", group = "prod")
public final class Autonomous3B extends LinearOpMode {

    private Collector c     = new Collector();
    private Mecanum m       = new Mecanum();
    private Lift l          = new Lift();
    private PlatformMover p = new PlatformMover();

    private IMUHandler i              = new IMUHandler();
    private VuforiaPositionDetector v = new VuforiaPositionDetector();

    private PartialMacroPlayer pm;

    private ColorSensor color;

    private Side side = Side.RED;
    private long delayMS = 0;

    @Override
    public void runOpMode() {

        initSubSystems();
        chooseOptions();
        initMiscellaneous();

        telemetry.addLine("All Subsystems initialized successfully :D");
        telemetry.addData("Side", side == Side.RED ? "RED" : "BLUE");
        telemetry.addData("Delay (ms)", delayMS);
        telemetry.update();

        waitForStart();
        say("Started");

        m.moveXYTicks(-1400,800, 0, i);
        sleep(1000);
        m.strafeUntilCloseToBlock(color, i, side);
        pm.playMacro();
    }


    private void chooseOptions() {

        boolean prevX = true;
        while(!isStopRequested() && (!gamepad1.a || gamepad1.start)) {
            if(gamepad1.x && !prevX) side = side.swap();
            prevX = gamepad1.x;

            telemetry.addLine("Which side are you on?");
            telemetry.addData("Current Side", side);
            telemetry.addLine("Press [X] to switch, [A] to select");
            telemetry.update();
        }

        while(gamepad1.a && !isStopRequested()) idle(); //This ensures that we the second loop runs / you press A twice

        while(!isStopRequested() && !gamepad1.a) {
            double delta = -gamepad1.left_stick_y;
            delta = Math.abs(delta) > 0.1 ? delta : 0;
            delayMS += delta;

            telemetry.addLine("How much delay do you want initially?");
            telemetry.addData("Delay (ms)", delayMS);
            telemetry.addLine("Use [LEFT JOYSTICK] to add/subtract delay.  Press [A] to select");
            telemetry.update();
        }
    }
    private void initSubSystems() {
        //If we put the multithreaded systems first, that gives it a bit more time to start up
        i.create(this, false);
        i.start();
        say("IMU Ready");
        v.create(this, false);
        v.start();
        say("Vuforia Ready");

        c.create(this);
        c.init();
        say("Collector Ready");
        m.create(this);
        m.init();
        say("Mecanum Ready");
        l.create(this);
        l.init();
        say("Lift Ready");
        p.create(this);
        p.init();
        say("Platform Mover Ready");
    }
    private void initMiscellaneous() {
        pm = new PartialMacroPlayer(
                this,
                "AUTO Platform Mover",
                m, c, l, p
        );
        pm.init();
        say("Macro Ready");

        if(side == Side.RED) color = hardwareMap.colorSensor.get("colorLeft");
        else color = hardwareMap.colorSensor.get("colorRight");
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
