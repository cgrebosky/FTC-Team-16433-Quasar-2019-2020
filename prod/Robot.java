package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import quasar.lib.MoreMath;
import quasar.lib.macro.MacroState;
import quasar.lib.macro.MacroSystem;
import quasar.subsystems.*;
import quasar.subsystems.threaded.IMUHandler;
import quasar.subsystems.threaded.TFSkystoneDetector;
import quasar.subsystems.threaded.VuforiaPositionDetector;

import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.CM;

public final class Robot {
    static Side s = Side.RED;
    private static BlockPosition pb = BlockPosition.CENTER;

    public static ColorSensor color;
    public static DistanceSensor dist, distCol;

    private static Collector co = new Collector();
    private static Lift li = new Lift();
    private static Mecanum me = new Mecanum();
    private static PlatformMover pm = new PlatformMover();
    private static CapstoneDepositor cp = new CapstoneDepositor();
    private static AutoBlockMover ab = new AutoBlockMover();
    
    public static IMUHandler imu = new IMUHandler();
    private static TFSkystoneDetector tfs = new TFSkystoneDetector();

    //region Internal Stuff
    private static LinearOpMode lop = null;
    private static OpMode opm = null;

    public static void create(LinearOpMode lop) {
        Robot.lop = lop;
        create((OpMode) lop);
    }
    public static void create(OpMode opm) {
        Robot.opm = opm;

        co.create(opm);
        li.create(opm);
        me.create(opm);
        pm.create(opm);
        cp.create(opm);
        ab.create(opm);
    }

    public static void autoInit() {
        imu.create(lop, false);
        imu.start();
        say("IMU initialized");

        init();
    }
    public static void initTFOD() {
        tfs.create(lop, false);
        tfs.start();
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

        ab.init();
        say("Autonomous Block Mover initialized");
    }
    //You MUST set the side before calling this
    public static void initSensors() {
        dist = lop.hardwareMap.get(DistanceSensor.class, "distance");
        if(Robot.s == Side.RED) {
            color = lop.hardwareMap.colorSensor.get("colorRight");
            distCol = lop.hardwareMap.get(DistanceSensor.class, "colorRight");
        }
        else {
            color = lop.hardwareMap.colorSensor.get("colorLeft");
            distCol = lop.hardwareMap.get(DistanceSensor.class, "colorLeft");
        }
    }
    public static void loop() {
        co.loop();
        li.loop();
        me.loop();
        pm.loop();
        cp.loop();
        ab.loop();
        opm.telemetry.update();
    }
    public static void stop() {
        co.stop();
        li.stop();
        me.stop();
        pm.stop();
        cp.stop();
        ab.stop();

        imu.kill();
        imu.interrupt();
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
        ab.recordMacroState();
    }
    public static void playMacroState(MacroState m) {
        co.playMacroState(m);
        li.playMacroState(m);
        me.playMacroState(m);
        pm.playMacroState(m);
        ab.playMacroState(m);
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

    @SubSystem.Auto
    static void fwdTicks(int ticks, double targetHeading) {

        Mecanum.EncoderPosition startPos = me.new EncoderPosition();
        Mecanum.EncoderPosition current  = me.new EncoderPosition();
        Mecanum.EncoderPosition end      = startPos.fwd(ticks);

        while(lop.opModeIsActive() && !MoreMath.isClose( current.fwdTicks(), end.fwdTicks(), Mecanum.AUTO_ERR )) {
            current = me.new EncoderPosition();
            int diffTicks = end.subtract(current).fwdTicks();

            double turn = turnForStableAngle(targetHeading);
            double fwd  = MoreMath.clip(((double) diffTicks / 600), -Mecanum.AUTO_MAX_SPEED, Mecanum.AUTO_MAX_SPEED);
            if(fwd > 0 && fwd < Mecanum.AUTO_MIN_SPEED) fwd = Mecanum.AUTO_MIN_SPEED;
            if(fwd < 0 && fwd > -Mecanum.AUTO_MIN_SPEED) fwd = -Mecanum.AUTO_MIN_SPEED;

            me.setPowers(fwd,0, turn);

            lop.telemetry.addData("DiffTicks", diffTicks);
            lop.telemetry.addData("Heading", imu.getAbsoluteHeading());
            lop.telemetry.addData("Target Heading", targetHeading);
            lop.telemetry.update();
        }
    }
    //Positive ticks means going to the RIGHT, if we have collectors at front
    @SubSystem.Auto
    static void strafeTicks(int ticks, double targetHeading) {
        Mecanum.EncoderPosition startPos = me.new EncoderPosition();
        Mecanum.EncoderPosition current  = me.new EncoderPosition();
        Mecanum.EncoderPosition end      = startPos.strafe(ticks);

        while(lop.opModeIsActive() && !MoreMath.isClose( current.strafeTicks(), end.strafeTicks(), Mecanum.AUTO_ERR )) {
            current = me.new EncoderPosition();
            int diffTicks = end.subtract(current).strafeTicks();

            double turn   = turnForStableAngle(targetHeading);
            double strafe = MoreMath.clip(( (double) diffTicks / 600), -Mecanum.AUTO_MAX_SPEED, Mecanum.AUTO_MAX_SPEED);
            if(strafe > 0 && strafe < Mecanum.AUTO_MIN_SPEED) strafe = Mecanum.AUTO_MIN_SPEED;
            if(strafe < 0 && strafe > -Mecanum.AUTO_MIN_SPEED) strafe = -Mecanum.AUTO_MIN_SPEED;

            me.setPowers(0, strafe, turn);

            lop.telemetry.addData("DiffTicks", diffTicks);
            lop.telemetry.addData("Heading", imu.getAbsoluteHeading());
            lop.telemetry.addData("Target Heading", targetHeading);
            lop.telemetry.update();
        }
    }
    //Y represents fwd, X represents strafing
    static void goToXY(int x, int y, double targetHeading) {
        Mecanum.EncoderPosition current = me.new EncoderPosition();
        Mecanum.EncoderPosition endFwd  = current.fwd(y);
        Mecanum.EncoderPosition endStr  = current.strafe(x);

        while(
                lop.opModeIsActive() &&
                !MoreMath.isClose( current.fwdTicks(), endFwd.fwdTicks(), Mecanum.AUTO_ERR ) &&
                !MoreMath.isClose( current.fwdTicks(), endStr.strafeTicks(), Mecanum.AUTO_ERR )
        ) {
            current = me.new EncoderPosition();
            int diffFwd = endFwd.subtract(current).fwdTicks();
            int diffStrafe = endStr.subtract(current).strafeTicks();

            double turn      = turnForStableAngle(targetHeading);
            double fwdPwr    = limitMecanumPwr((float) diffFwd / 100);
            double strafePwr = limitMecanumPwr((float) diffStrafe / 100);

            me.setPowers(fwdPwr, strafePwr, turn);

            lop.telemetry.addData("Diff FWD", diffFwd);
            lop.telemetry.addData("Diff STRAFE", diffStrafe);
            lop.telemetry.addData("Heading", imu.getAbsoluteHeading());
            lop.telemetry.addData("Target Heading", targetHeading);
            lop.telemetry.update();
            lop.idle();
        }
    }
    @SubSystem.Auto
    public static void moveXYTicks(int strafe, int fwd, double targetHeading) {
        Mecanum.EncoderPosition startPos = me.new EncoderPosition();
        Mecanum.EncoderPosition current  = me.new EncoderPosition();
        Mecanum.EncoderPosition endFwd   = startPos.fwd(fwd);
        Mecanum.EncoderPosition endStrafe= startPos.strafe(strafe);

        long t = System.currentTimeMillis() + 5000;
        while(System.currentTimeMillis() < t &&
                lop.opModeIsActive() &&
                !MoreMath.isClose( current.fwdTicks(), endFwd.fwdTicks(), Mecanum.AUTO_ERR ) &&
                !MoreMath.isClose( current.fwdTicks(), endStrafe.strafeTicks(), Mecanum.AUTO_ERR ))
        {
            current = me.new EncoderPosition();
            int diffFwd = endFwd.subtract(current).fwdTicks();
            int diffStrafe = endStrafe.subtract(current).strafeTicks();

            double turn      = turnForStableAngle(targetHeading);
            double fwdPwr    = MoreMath.clip(diffFwd / 50, -Mecanum.AUTO_MAX_SPEED, Mecanum.AUTO_MAX_SPEED);
            double strafePwr = MoreMath.clip(diffStrafe / 50, -Mecanum.AUTO_MAX_SPEED, Mecanum.AUTO_MAX_SPEED);

            me.setPowers(fwdPwr, strafePwr, turn);

            lop.telemetry.addData("Diff FWD", diffFwd);
            lop.telemetry.addData("Diff STRAFE", diffStrafe);
            lop.telemetry.addData("Heading", imu.getAbsoluteHeading());
            lop.telemetry.addData("Target Heading", targetHeading);
            lop.telemetry.update();
            lop.idle();
        }
    }
    static void strafeUntilCloseToBlock() {
        ab.halfLower(s);
        double pwr = 0.3;
        if(s == Side.BLUE) pwr = -pwr;
        me.setPowers(0, pwr, 0);

        while( lop.opModeIsActive() && (distCol.getDistance(CM) > 40 || Double.isNaN(distCol.getDistance(CM))) ) {
            me.setPowers(0, pwr, turnForStableAngle(0));
            lop.idle();
        }
        me.setPowers(0,0,0);

    }
    static void collectSkystone() {
        while(color.red() > 45 && lop.opModeIsActive()) {
            me.setPowers(-0.3, strafeForStableDistance(20), turnForStableAngle(0));
        }
        me.setPowers(0,0,0);
        fwdTicks(25, 0);
        strafeTicks(35, 0);

        ab.lower(s);
        lop.sleep(500);
        ab.close(s);
        lop.sleep(200);
        ab.raise(s);
    }
    static void fwdUntilAtWall() {
        while((dist.getDistance(CM) > 15 || Double.isNaN(dist.getDistance(CM))) && lop.opModeIsActive()) {
            double fwd = MoreMath.clip(dist.getDistance(CM) / 200, 0.3, 0.4);
            me.setPowers(fwd, 0, turnForStableAngle(0));
            opm.telemetry.addData("Dist", dist.getDistance(CM));
            opm.telemetry.update();
        }
    }
    static void release() {
        ab.release(s);
    }

    private static double turnForStableAngle(double targetHeading) {
        double diff = targetHeading - imu.getAbsoluteHeading();
        final double P = 0.018;
        return MoreMath.clip( -diff * P, -.5, .5 );
    }
    private static double strafeForStableDistance(double targetDist) {
        double diff = distCol.getDistance(CM) - targetDist;
        final double P = 0.01;
        return P * diff;
    }
    private static double limitMecanumPwr(double pwr) {
        double p = MoreMath.clip(pwr, -Mecanum.AUTO_MAX_SPEED, Mecanum.AUTO_MAX_SPEED);

        if(p > 0 && p < Mecanum.AUTO_MIN_SPEED) p = Mecanum.AUTO_MIN_SPEED;
        if(p < 0 && p > -Mecanum.AUTO_MIN_SPEED) p = -Mecanum.AUTO_MIN_SPEED;

        return p;
    }

    public static void sayTFPosition() {
        lop.telemetry.addData("X,Y", tfs.getX() + ", " + tfs.getY());
    }
    static void goToBlock() {
        while(lop.opModeIsActive() && !tfs.isSkystoneIsVisible()) me.setPowers(0, -0.4, turnForStableAngle(0));


        me.setPowers(0,0,0);
    }
    //endregion
}
