package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import quasar.lib.macro.MacroState;
import quasar.lib.macro.MacroSystem;
import quasar.subsystems.*;
import quasar.subsystems.threaded.IMUHandler;
import quasar.subsystems.threaded.VuforiaPositionDetector;

public final class Robot implements MacroSystem {
    private static Collector co = new Collector();
    private static Lift li = new Lift();
    private static Mecanum me = new Mecanum();
    private static PlatformMover pm = new PlatformMover();
    private static CapstoneDepositor cp = new CapstoneDepositor();
    
    private static IMUHandler imu = new IMUHandler();
    private static VuforiaPositionDetector vpd = new VuforiaPositionDetector();

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
    }

    public static void autoInit() {
        imu.create(lop, false);
        say("IMU initialized");

        vpd.create(lop, false);
        say("Vuforia Positioner initialized");

        init();
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
    }
    public static void loop() {
        co.loop();
        li.loop();
        me.loop();
        pm.loop();
        cp.loop();
        opm.telemetry.update();
    }
    public static void stop() {
        co.stop();
        li.stop();
        me.stop();
        pm.stop();
        cp.stop();
    }

    private static void say(Object o) {
        opm.telemetry.addLine(""+o);
        opm.telemetry.update();
    }

    @Override
    public void recordMacroState() {
        co.recordMacroState();
        li.recordMacroState();
        me.recordMacroState();
        pm.recordMacroState();
    }
    @Override
    public void playMacroState(MacroState m) {
        co.recordMacroState();
        li.recordMacroState();
        me.recordMacroState();
        pm.recordMacroState();
    }
}
