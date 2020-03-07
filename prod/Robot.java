package quasar.prod;

import android.provider.Telephony;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import quasar.lib.MoreMath;
import quasar.lib.macro.MacroState;
import quasar.lib.macro.PartialMacroPlayer;
import quasar.subsystems.*;
import quasar.subsystems.threaded.IMUHandler;
import quasar.subsystems.threaded.TFSkystoneDetector;

import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.CM;

public final class Robot {
    static Side s = Side.RED;
    static BlockPosition pb = BlockPosition.CENTER;

    static PartialMacroPlayer deliver, platRed, platBlue; //We have to lateinit these because lop isn't set

    private static DistanceSensor dist;

    private static Collector co = new Collector();
    private static Lift li = new Lift();
    private static Mecanum me = new Mecanum();
    private static PlatformMover pm = new PlatformMover();
    private static CapstoneDepositor cp = new CapstoneDepositor();
    //private static AutoBlockMover ab = new AutoBlockMover();
    
    private static IMUHandler imu = new IMUHandler();
    private static TFSkystoneDetector tfs = new TFSkystoneDetector();

    private static int angleL = 7, angleC = -13, angleR = -25, angle = angleC;
    private static double DISTL = 26, DISTC = 46, DISTR = 66, blockDist = DISTC;
    private static int B_DISTL = 640, B_DISTC = 270, B_DISTR = -100, B_DIST = B_DISTC;

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
        imu.create(lop, false);
        imu.start();
        say("IMU initialized");

        tfs.create(lop, false);
        tfs.start();
        say("Tensorflow initialized");

        co.autoInit();
        say("Collector initialized");

        li.autoInit();
        say("Lift initialized");

        me.autoInit();
        say("Mecanum initialized");

        pm.autoInit();
        say("Platform Mover initialized");

        cp.autoInit();
        say("Capstone Depositor initialized");

        deliver  = new PartialMacroPlayer(lop, "AUTO Deliver Block");
        deliver.init();
        platRed = new PartialMacroPlayer(lop, "AUTO Platform RED");
        platRed.init();
        platBlue = new PartialMacroPlayer(lop, "AUTO Platform BLUE");
        platBlue.init();
        say("Macros initialized");

        dist = lop.hardwareMap.get(DistanceSensor.class, "distance");
        say("Sensors initialized");

        say("Robot initialized :D");

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
        //ab.stop();

        imu.kill();
        imu.interrupt();

        tfs.kill();
        tfs.interrupt();
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
    /*@SubSystem.Auto
    void goToPositionVuforia(double endX, double endY) {
        double startAngle = imu.getAbsoluteHeading();
        while(lop.opModeIsActive() && vpd.imageIsVisible()) {
            double angle = imu.getAbsoluteHeading();
            double diff = startAngle - angle;
            double turn = MoreMath.clip(-diff / 45, -0.6, 0.6);

            double x = vpd.getX();
            double y = vpd.getY();
            double dx = endX - x;
            double dy = endY - y;

            double dmax = Math.max( Math.abs(dy), Math.abs(dx) );
            double xPow = MoreMath.clip( dx / dmax, -0.3, 0.3);
            double yPow = MoreMath.clip( dy / dmax, -0.3, 0.3);

            me.setPowers(-xPow, yPow, turn);
        }
    }*/

    static void fwdTicks(int ticks, double targetHeading, double pwr) {
        int start = me.fl.getCurrentPosition();
        int end = start + ticks;
        final int NEAR_EN = 500;
        final int END_DIFF = 50;
        if(ticks > 0)      while(lop.opModeIsActive()) {
            int curr = me.fl.getCurrentPosition();
            if(curr < end - NEAR_EN) me.setPowers(pwr, 0, turnForStableAngle(targetHeading));
            else if(curr < end && curr < end - END_DIFF) me.setPowers(0.5, 0, turnForStableAngle(targetHeading));
            else break;
        }
        else if(ticks < 0) while(lop.opModeIsActive()) {
            me.setPowers(-pwr, 0, turnForStableAngle(targetHeading));

            int curr = me.fl.getCurrentPosition();
            if(curr > end + NEAR_EN) me.setPowers(-pwr, 0, turnForStableAngle(targetHeading));
            else if(curr > end && curr > end + END_DIFF) me.setPowers(-0.5, 0, turnForStableAngle(targetHeading));
            else break;
        }

        me.setPowers(0,0,0);
    }
    static void fwdTicks(int ticks, double targetHeading) {
        fwdTicks(ticks, targetHeading, 0.8);
    }
    static void strafeTicks(int ticks, double targetHeading, double pwr) {
        int start = me.fl.getCurrentPosition();
        int end = start + ticks;
        final int END_DIFF = 50;
        if(ticks > 0)      while(lop.opModeIsActive() && me.fl.getCurrentPosition() < end - END_DIFF) {
            me.setPowers(0, pwr, turnForStableAngle(targetHeading));
        }
        else if(ticks < 0) while(lop.opModeIsActive() && me.fl.getCurrentPosition() > end + END_DIFF) {
            me.setPowers(0, -pwr, turnForStableAngle(targetHeading));
        }

        me.setPowers(0,0,0);
    }
    static void strafeTicks(int ticks, double targetHeading) {
        strafeTicks(ticks, targetHeading, 0.7);
    }

    static void turnDegAbsolute(double target) {
        me.setPowers(0,0,-Math.signum(target) * 0.5 / 3);

        double start = imu.getAbsoluteHeading();
        double curr = start;
        double diff = target - curr;
        while(Math.abs(diff) > 2 && lop.opModeIsActive()) {
            curr = imu.getAbsoluteHeading();
            diff = target - curr;

            if(Math.abs(diff) > 30) me.setPowers(0,0,-Math.signum(diff) * 0.5);
            else if(Math.abs(diff) > 15) me.setPowers(0,0,-Math.signum(diff) * 0.3);
            else if(Math.abs(diff) > 5) me.setPowers(0,0,-Math.signum(diff) * 0.25);

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
        long et = System.currentTimeMillis() + 15000;
        int l = 0, c = 0, r = 0;
        while(lop.opModeIsActive() && System.currentTimeMillis() < et) {
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
            angle = angleL;
            blockDist = DISTL;
            B_DIST = B_DISTL;
        } else if (pb == BlockPosition.CENTER) {
            angle = angleC;
            blockDist = DISTC;
            B_DIST = B_DISTC;
        } else {
            angle = angleR;
            blockDist = DISTR;
            B_DIST = B_DISTR;
        }
    }
    static void collect1_() {
        co.collect();
        co.open();
        orientForBPos();
        fwdTicks(1400, angle, 0.4);
        co.half();
        fwdTicks(-1000, angle);
        co.zero();
    }
    static void deliver1_() {
        turnDegAbsolute(-90);
        fwdTicks(3500,-90);
        me.turnDegrees(-90);
    }

    static void collect1stBlock() {
        li.openClaw();
        strafeTicks(B_DIST, 0);
        co.collect();
        co.open();
        fwdTicks(2000, 0, 0.6);
        fwdTicks(-1100, 0);
        co.stop();
        co.half();
        li.closeClaw();
    }
    static void deliver1stBlock() {
        turnDegAbsolute(90);
        fwdTicks(-2460 + B_DIST,90);
        turnDegAbsolute(135);
        deliver.playMacro();
        turnDegAbsolute(90);
    }
    static void collect2ndBlock() {
        li.openClaw();
        fwdTicks(3200 - B_DIST, 90);
        co.open();
        co.collect();
        strafeTicks(1050, 90);
        fwdTicks(300,90,0.7);
        strafeTicks(-1000,90);
        co.half();
        co.zero();
        li.closeClaw();
    }
    static void deliver2ndBlock() {
        fwdTicks(-4500 + B_DIST, 90);
        turnDegAbsolute(180);
        platRed.playMacro();
    }

    private static void orientForBPos() {
        if(pb == BlockPosition.LEFT) angle = angleL;
        else if(pb == BlockPosition.CENTER) angle = angleC;
        else if (pb == BlockPosition.RIGHT) angle = angleR;

        me.turnDegrees(angle);
    }

    private static double turnForStableAngle(double targetHeading) {
        double diff = targetHeading - imu.getAbsoluteHeading();
        final double P = 0.018;
        return MoreMath.clip( -diff * P, -.5, .5 );
    }
    private static double limitMecanumPwr(double pwr) {
        double p = MoreMath.clip(pwr, -0.8, 0.8);

        if(p > 0 && p < 0.4) p = 0.4;
        if(p < 0 && p > -0.4) p = -0.4;

        return p;
    }

    public static void disableLimits() {
        li.limitsActive = false;
    }
    //endregion
}
