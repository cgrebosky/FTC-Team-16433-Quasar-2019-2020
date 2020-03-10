package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import quasar.lib.MoreMath;
import quasar.lib.macro.MacroState;
import quasar.lib.macro.PartialMacroPlayer;
import quasar.subsystems.*;
import quasar.subsystems.sensory.IMUHandler;
import quasar.subsystems.sensory.TFSkystoneDetector;

import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.CM;
import static quasar.prod.Side.RED;
import static quasar.subsystems.Mecanum.STRAFE_COEF;

public final class Robot {
    private static Side s = RED;
    private static BlockPosition pb = BlockPosition.CENTER;

    private static PartialMacroPlayer deliver, platform; //We have to lateinit these because lop isn't set yet

    private static DistanceSensor distF, distL, distR;

    //region SubSystem Variables
    private static Collector co = new Collector();
    private static Lift li = new Lift();
    private static Mecanum me = new Mecanum();
    private static PlatformMover pm = new PlatformMover();
    private static CapstoneDepositor cp = new CapstoneDepositor();
    
    private static IMUHandler imu = new IMUHandler();
    private static TFSkystoneDetector tfs = new TFSkystoneDetector();
    //endregion

    private static int B_DIST;
    private static double W_DIST;
    private static double SIDE_WALL_DIST = 63;

    //region Internal Stuff
    private static LinearOpMode lop = null;
    private static OpMode opm = null;

    public static void create(LinearOpMode lop) {
        Robot.lop = lop;
        Robot.opm = lop;
        co.create(lop);
        li.create(lop);
        me.create(lop);
        pm.create(lop);
        cp.create(lop);

        imu.create(lop);
        tfs.create(lop);
    }
    public static void create(OpMode opm) {
        Robot.opm = opm;

        co.create(opm);
        li.create(opm);
        me.create(opm);
        pm.create(opm);
        cp.create(opm);
        //ab.create(opm);
    }

    static void autoInit() {
        chooseSide();

        say("IMU Initializing");
        imu.autoInit();

        say("Tensorflow Initializing");
        tfs.autoInit();

        say("Collector Initializing");
        co.autoInit();

        say("Lift Initializing");
        li.autoInit();

        say("Mecanum Initializing");
        me.autoInit();

        say("Platform Mover Initializing");
        pm.autoInit();

        say("Capstone Depositor Initializing");
        cp.autoInit();

        say("Macro Initializing");
        deliver  = new PartialMacroPlayer(lop, "AUTO Deliver Block");
        deliver.init();
        platform = new PartialMacroPlayer(lop, "AUTO Move Platform");
        platform.init();

        say("Sensors initializing");
        distF = lop.hardwareMap.get(DistanceSensor.class, "distance");
        distL = lop.hardwareMap.get(DistanceSensor.class, "distanceL");
        distR = lop.hardwareMap.get(DistanceSensor.class, "distanceR");

        say(
                "Robot initialized :D\n"
                + "(Remember: is a wire blocking a distance sensor?)\n"
                + "Side: " + s
        );
    }
    public static void init() {
        co.init();
        say("Collector initialized");
        
        li.init();
        say("Lift initialized");
        
        me.init();
        say("Mecanum initialized");
        
        pm.init();
        say("Platform Mover initialized");
        
        cp.init();
        say("Capstone Depositor initialized");

        //ab.init();
        //say("Autonomous Block Mover initialized");
    }
    public static void loop() {
        co.loop();
        li.loop();
        me.loop();
        pm.loop();
        cp.loop();
        //ab.loop();
        opm.telemetry.update();
    }
    public static void stop() {
        co.stop();
        li.stop();
        me.stop();
        pm.stop();
        cp.stop();
    }

    private static void chooseSide() {
        boolean prevX = true;
        while(!lop.isStopRequested() && (!lop.gamepad1.a || lop.gamepad1.start)) {
            if(lop.gamepad1.x && !prevX) s = s.swap();
            prevX = lop.gamepad1.x;

            lop.telemetry.addLine("Choose side.  Press [A] to finalize choice, and [X] to switch");
            lop.telemetry.addData("Current Side", s);
            lop.telemetry.update();
        }
    }
    //endregion
    private static void say(Object o) {
        opm.telemetry.addLine(""+o);
        opm.telemetry.update();
    }
    //region Macro
    //Unfortunately, we don't inherit MacroSystem here, because these are static methods.  I'm not
    //sure how to get around this from an OOP perspective
    public static void recordMacroState() {
        co.recordMacroState();
        li.recordMacroState();
        me.recordMacroState();
        pm.recordMacroState();
        //ab.recordMacroState();
    }
    public static void playMacroState(MacroState m) {
        co.playMacroState(m);
        li.playMacroState(m);
        me.playMacroState(m);
        pm.playMacroState(m);
        //ab.playMacroState(m);
    }
    //endregion

    //region Autonomous
    private static void fwdTicks(int ticks, double targetHeading, double pwr) {
        int start = me.getFwdPos();
        int end = start + ticks;
        final int NEAR_EN = 400;
        final int END_DIFF = 50;
        if(ticks > 0)      while(lop.opModeIsActive()) {
            int curr = me.getFwdPos();
            if(curr < end - NEAR_EN) me.setPowers(pwr, 0, turnForStableAngle(targetHeading));
            else if(curr < end && curr < end - END_DIFF) me.setPowers(0.35, 0, turnForStableAngle(targetHeading));
            else break;
        }
        else if(ticks < 0) while(lop.opModeIsActive()) {
            me.setPowers(-pwr, 0, turnForStableAngle(targetHeading));

            int curr = me.getFwdPos();
            if(curr > end + NEAR_EN) me.setPowers(-pwr, 0, turnForStableAngle(targetHeading));
            else if(curr > end && curr > end + END_DIFF) me.setPowers(-0.35, 0, turnForStableAngle(targetHeading));
            else break;
        }

        me.setPowers(0,0,0);
    }
    static void fwdTicks(int ticks, double targetHeading) {
        fwdTicks(ticks, targetHeading, 0.8);
    }
    private static void strafeTicks(int ticks, double targetHeading, double pwr) {
        int start = me.getStrPos();
        int end = start + ticks;
        final int END_DIFF = 100;
        if(ticks > 0)      while(lop.opModeIsActive() && me.getStrPos() < end - END_DIFF) {
            me.setPowers(0, pwr, turnForStableAngle(targetHeading));
        }
        else if(ticks < 0) while(lop.opModeIsActive() && me.getStrPos() > end + END_DIFF) {
            me.setPowers(0, -pwr, turnForStableAngle(targetHeading));
        }

        me.setPowers(0,0,0);
    }
    private static void strafeTicks(int ticks, double targetHeading) {
        strafeTicks(ticks, targetHeading, 0.7);
    }

    private static void turnDegAbsolute(double target) {
        target *= s.c();
        me.setPowers(0,0,-Math.signum(target) * 0.5 / 3);

        double start = imu.getAbsoluteHeading();
        double curr = start;
        double diff = target - curr;
        while(Math.abs(diff) > 6 && lop.opModeIsActive()) {
            curr = imu.getAbsoluteHeading();
            diff = target - curr;
            if(Math.abs(diff) > 35) me.setPowers(0,0,-Math.signum(diff) * 0.7);
            else me.setPowers(0,0,-Math.signum(diff) * 0.35);

            opm.telemetry.addData("start", start);
            opm.telemetry.addData("end", target);
            opm.telemetry.addData("curr", curr);
            opm.telemetry.addData("diff", diff);
            opm.telemetry.update();
        }
        me.setPowers(0,0,0);
    }

    static void miscLateInit() {
        cp.deactivate();
        li.openClaw();
    }
    static void getPosition() {
        if(s == RED) getPositionRed();
        else getPositionBlue();

        if(pb == BlockPosition.LEFT) {
            B_DIST = s == RED? -190 : 250;
            W_DIST = 35;
        } else if (pb == BlockPosition.CENTER) {
            B_DIST = s == RED? 240: -200;
            W_DIST = 55;
        } else {
            B_DIST = s == RED? 500: -500;
            W_DIST = 75;
        }
    }
    private static void getPositionRed() {
        long et = System.currentTimeMillis() + 1000;
        int l = 0, c = 0, r = 0;
        while(lop.opModeIsActive() && System.currentTimeMillis() < et) {
            tfs.loop();

            double pos = tfs.getX();
            if (pos > 100 && tfs.isSkystoneIsVisible()) l ++;
            else if (pos <= 80 && tfs.isSkystoneIsVisible()) r ++;
            else c ++;

            lop.telemetry.addData("L", l);
            lop.telemetry.addData("C", c);
            lop.telemetry.addData("R", r);
            lop.telemetry.addData("pos", pos);
            lop.telemetry.addData("Visible", tfs.isSkystoneIsVisible());
            lop.telemetry.update();
        }
        if(l > c && l > r) pb = BlockPosition.LEFT;
        else if(c > l && c > r) pb = BlockPosition.CENTER;
        else pb = BlockPosition.RIGHT;
    }
    private static void getPositionBlue() {
        long et = System.currentTimeMillis() + 1000;
        int l = 0, c = 0, r = 0;
        while(lop.opModeIsActive() && System.currentTimeMillis() < et) {
            tfs.loop();

            double pos = tfs.getX();
            if (pos < 150 && tfs.isSkystoneIsVisible()) r ++;
            else if(tfs.isSkystoneIsVisible()) c ++;
            else l ++;

            lop.telemetry.addData("L", l);
            lop.telemetry.addData("C", c);
            lop.telemetry.addData("R", r);
            lop.telemetry.addData("pos", pos);
            lop.telemetry.addData("Visible", tfs.isSkystoneIsVisible());
            lop.telemetry.update();
        }
        if(l > c && l > r) pb = BlockPosition.LEFT;
        else if(c > l && c > r) pb = BlockPosition.CENTER;
        else pb = BlockPosition.RIGHT;
    }

    static void collect1stBlock() {
        co.open();
        li.openClaw();
        strafeTicks((int) (B_DIST * STRAFE_COEF), 0);
        co.collect();
        fwdTicks(1700, 0, 0.5);
        fwdTicks(-900, 0,0.7);
        co.stop();
        co.half();
        li.closeClaw();
    }
    static void deliver1stBlock() {
        turnDegAbsolute(90);
        goToCorrectWallDistance();
        fwdTicks(-2750 + B_DIST,90);
        turnDegAbsolute(150);
        deliver.playMacro();
        turnDegAbsolute(90);
    }
    static void collect2ndBlock() {
        li.openClaw();
        fwdTicks(3320 - B_DIST, 90);
        fwdToWall();
        co.open();
        co.collect();
        strafeTicks(900 * s.c(), 90);
        fwdTicks(300,90,0.7);
        strafeTicks(-900 * s.c(),90);
        goToCorrectWallDistance();
        co.half();
        co.zero();
        li.closeClaw();
    }
    static void deliver2ndBlock() {
        fwdTicks(-4200 + B_DIST, 90);
        if(s == RED) me.turnDegrees(90);
        else         me.turnDegrees(-90);
    }
    static void movePlatform() {
        platform.playMacro();
    }

    private static void fwdToWall() {
        final double targetF = W_DIST, targetS = SIDE_WALL_DIST;
        final double Pf = -0.005, Ps = 0.01;

        double diffF, diffS;
        do {
            diffF = targetF - distF.getDistance(CM);
            if(s == RED) diffS = targetS - distL.getDistance(CM);
            else         diffS = -(targetS - distR.getDistance(CM));

            double powF = Pf * diffF;
            double powS = Ps * diffS;

            powF = Math.signum(powF) * MoreMath.clip(Math.abs(powF), 0.17, 0.8);
            powS = Math.signum(powS) * MoreMath.clip(Math.abs(powS), 0.17, 0.8);

            me.setPowers(powF, powS, turnForStableAngle(90));

            opm.telemetry.addData("diffF", diffF);
            opm.telemetry.addData("diffS", diffS);
            opm.telemetry.update();

        } while(lop.opModeIsActive() && (Math.abs(diffS) > 5 || Math.abs(diffF) > 5) );

        me.setPowers(0,0,0);
    }
    private static void goToCorrectWallDistance() {
        final double target = SIDE_WALL_DIST;
        final double P = 0.01;
        double diff;
        do {
            if(s == RED) diff = target - distL.getDistance(CM);
            else         diff = -(target - distR.getDistance(CM));

            double pow = P * diff;
            pow = Math.signum(pow) * MoreMath.clip(Math.abs(pow), 0.2, 0.8);

            me.setPowers(0, pow, turnForStableAngle(90));
        } while(lop.opModeIsActive() && Math.abs(diff) > 4);

        me.setPowers(0,0,0);
    }

    private static double turnForStableAngle(double targetHeading) {
        double diff = targetHeading * s.c() - imu.getAbsoluteHeading();
        final double P = 0.018;
        return MoreMath.clip( -diff * P, -.5, .5 );
    }

    public static void disableLimits() {
        li.limitsActive = false;
    }
    //endregion
}
