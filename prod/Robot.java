package quasar.prod;

import android.sax.StartElementListener;

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
import static quasar.subsystems.Mecanum.STRAFE_COEF;

public final class Robot {
    static Side s = Side.RED;
    static BlockPosition pb = BlockPosition.CENTER;

    static PartialMacroPlayer deliver, platRed, platBlue; //We have to lateinit these because lop isn't set

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

    private static int B_DISTL = -190, B_DISTC = 240, B_DISTR = 500, B_DIST;
    private static double W_DISTL = 37, W_DISTC = 55, W_DISTR = 75, W_DIST;

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
        platRed = new PartialMacroPlayer(lop, "AUTO Platform RED");
        platRed.init();
        platBlue = new PartialMacroPlayer(lop, "AUTO Platform BLUE");
        platBlue.init();

        say("Sensors initializing");
        distF = lop.hardwareMap.get(DistanceSensor.class, "distance");
        distL = lop.hardwareMap.get(DistanceSensor.class, "distanceL");

        say(
                "Robot initialized :D\n"
                + "(Remember: is a wire blocking a distance sensor?)"
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
    static void fwdTicks(int ticks, double targetHeading, double pwr) {
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
    static void strafeTicks(int ticks, double targetHeading, double pwr) {
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
    static void strafeTicks(int ticks, double targetHeading) {
        strafeTicks(ticks, targetHeading, 0.7);
    }

    private static void turnDegAbsolute(double target) {
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
        long et = System.currentTimeMillis() + 800;
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

        if(pb == BlockPosition.LEFT) {
            B_DIST = B_DISTL;
            W_DIST = W_DISTL;
        } else if (pb == BlockPosition.CENTER) {
            B_DIST = B_DISTC;
            W_DIST = W_DISTC;
        } else {
            B_DIST = B_DISTR;
            W_DIST = W_DISTR;
        }

    }

    static void collect1stBlock() {
        co.open();
        li.openClaw();
        strafeTicks((int) (B_DIST * STRAFE_COEF), 0);
        co.collect();
        fwdTicks(1700, 0, 0.5);
        fwdTicks(-1000, 0,0.7);
        co.stop();
        co.half();
        li.closeClaw();
    }
    static void deliver1stBlock() {
        turnDegAbsolute(90);
        goToCorrectWallDistance();
        fwdTicks(-2750 + B_DIST,90);
        me.turnDegrees(90);
        deliver.playMacro();
        turnDegAbsolute(90);
    }
    static void collect2ndBlock() {
        li.openClaw();
        fwdTicks(3320 - B_DIST, 90);
        fwdToWall();
        co.open();
        co.collect();
        strafeTicks(900, 90);
        fwdTicks(300,90,0.7);
        strafeTicks(-900,90);
        goToCorrectWallDistance();
        co.half();
        co.zero();
        li.closeClaw();
    }
    static void deliver2ndBlock() {
        fwdTicks(-4200 + B_DIST, 90);
        me.turnDegrees(90);
        platRed.playMacro();
    }
    static void movePlatform() {
        pm.raiseHooks();
        me.setPowers(-0.4,0,0);
        lop.sleep(500);
        pm.lowerHooks();
        lop.sleep(500);

        me.setPowers(0.6,0,0);

        final double target = 5;
        final double P = -0.01;
        double diff;

        do {
            diff = target - distF.getDistance(CM);

            double pow = P * diff;
            pow = Math.signum(pow) * MoreMath.clip(Math.abs(pow), 0.2, 0.5);

            me.setPowers(pow, 0,0); //Our IMU turn functions kinda break down at 180±5...
        } while (Math.abs(diff) > 10);
    }
    static void movePlatformMacro() {
        pm.raiseHooks();
        me.setPowers(-0.4,0,0);
        lop.sleep(500);
        pm.lowerHooks();
        lop.sleep(500);

        platRed.playMacro();
    }

    static void fwdToWall() {
        final double targetF = W_DIST, targetS = 50;
        final double Pf = -0.005, Ps = 0.01;

        double diffF, diffS;
        do {
            diffF = targetF - distF.getDistance(CM);
            diffS = targetS - distL.getDistance(CM);

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
    static void goToCorrectWallDistance() {
        final double target = 55;
        final double P = 0.01;
        double diff;
        do {
            diff = target - distL.getDistance(CM);

            double pow = P * diff;
            pow = Math.signum(pow) * MoreMath.clip(Math.abs(pow), 0.2, 0.8);

            me.setPowers(0, pow, turnForStableAngle(90));
        } while(lop.opModeIsActive() && Math.abs(diff) > 4);

        me.setPowers(0,0,0);
    }

    private static double turnForStableAngle(double targetHeading) {
        double diff = targetHeading - imu.getAbsoluteHeading();
        final double P = 0.018;
        return MoreMath.clip( -diff * P, -.5, .5 );
    }

    public static void disableLimits() {
        li.limitsActive = false;
    }
    //endregion
}
