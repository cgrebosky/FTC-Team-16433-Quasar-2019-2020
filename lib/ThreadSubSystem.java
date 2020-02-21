package quasar.lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;


public abstract class ThreadSubSystem extends Thread {
    //region Variables
    protected LinearOpMode lop; //We only need to use LinearOpModes since this class takes care of looping...

    protected Gamepad gamepad1, gamepad2;
    protected GamepadState prev1 = new GamepadState(), prev2 = new GamepadState();
    protected HardwareMap hmap;
    protected Telemetry telemetry;

    public enum PRGM_STATE {NOT_STARTED, INITIALIZING, INITIALIZED, RUNNING, STOPPED}
    private PRGM_STATE state = PRGM_STATE.NOT_STARTED;

    private boolean telemetryIsActive = false;
    private boolean isAutonomous = false;

    private long prevTime = 0, cycleTime = 0; //This tells how long the current loop cycle is
    //endregion

    //region Constructors / Creators
    public ThreadSubSystem() {}

    public synchronized void create(LinearOpMode lop, boolean isAutonomous) {
        this.lop = lop;
        this.isAutonomous = isAutonomous;

        hmap      = lop.hardwareMap;
        telemetry = lop.telemetry;
        gamepad1  = lop.gamepad1;
        gamepad2  = lop.gamepad2;
    }
    //endregion

    //region SubSystem
    //These are analogues of the OpMode methods init, loop, and stop.  The _ prefix indicates that it's not Thread methods
    protected abstract void _init();
    protected void _teleInit() { _init(); }
    protected void _autoInit() { _init(); }
    protected abstract void _loop();
    protected abstract void _stop();
    protected abstract void _telemetry();

    private void updateGamepadStates() {
        prev1 = new GamepadState(gamepad1);
        prev2 = new GamepadState(gamepad2);
    }
    private void postLoop() {
        cycleTime = System.currentTimeMillis() - prevTime;
        prevTime = System.currentTimeMillis();

        updateGamepadStates();

        if(telemetryIsActive) _telemetry();
    }
    //endregion

    //region Thread
    @Override public void run() {
        internalInit();
        while(!lop.isStarted()); //Just wait until it starts.  lop.waitForStart() is too thread-magicky, so I'm just doing this
        internalLoop();

        state = PRGM_STATE.STOPPED;
        _stop();
    }
    private void internalInit() {
        state = PRGM_STATE.INITIALIZING;

        if(isAutonomous) _autoInit();
        else             _teleInit();

        state = PRGM_STATE.INITIALIZED;
    }
    private void internalLoop() {
        state = PRGM_STATE.RUNNING;
        while (lop.opModeIsActive() && state == PRGM_STATE.RUNNING) {
            if(isAutonomous) {
                postLoop();
            } else {
                _loop();
                postLoop();
            }
        }
    }
    //endregion

    //region Getters/Setters
    /**
     * This tells us if the subsystem is currently in autonomous or teleop
     * @return If this is in autonomous mode
     */
    public synchronized boolean isAutonomous() {
        return isAutonomous;
    }
    public synchronized boolean isStarted() {
        return lop.isStarted();
    }
    public synchronized boolean isStopRequested() {
        return lop.isStopRequested();
    }
    public synchronized PRGM_STATE getProgramState() {
        return state;
    }

    public synchronized void enableTelemetry()  {
        telemetryIsActive = true;
    }
    public synchronized void disableTelemetry() {
        telemetryIsActive = false;
    }

    public synchronized double getCycleTime() {
        return cycleTime;
    }

    //endregion
}
