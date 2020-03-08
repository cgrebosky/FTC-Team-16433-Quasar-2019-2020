package quasar.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public abstract class ThreadSubSystem extends Thread {
    //region Variables
    protected LinearOpMode lop;

    protected HardwareMap hmap;
    protected Telemetry telemetry;

    public enum PRGM_STATE {NOT_STARTED, INITIALIZING, INITIALIZED, RUNNING, STOPPED}
    private PRGM_STATE state = PRGM_STATE.NOT_STARTED;

    //since this is not synchronized with lop, having telemetry active will mess up all other telemetry;
    //If you really want telemetry for a threaded system, just turn it off everywhere elsewhere
    private boolean telemetryIsActive = false;
    //endregion

    //region Constructors / Creators
    public ThreadSubSystem() {}

    public synchronized void create(LinearOpMode lop) {
        this.lop = lop;

        hmap      = lop.hardwareMap;
        telemetry = lop.telemetry;
    }
    //endregion

    //region SubSystem
    //These are analogues of the OpMode methods init, loop, and stop.  The _ prefix indicates that it's not Thread methods
    protected abstract void _init();
    protected abstract void _loop();
    protected abstract void _stop();
    protected abstract void _telemetry();

    private void postLoop() {
        if(telemetryIsActive) {
            _telemetry();
            telemetry.update();
        }
    }
    //endregion

    @Override public void run() {
        state = PRGM_STATE.INITIALIZING;
        _init();
        state = PRGM_STATE.INITIALIZED;

        state = PRGM_STATE.RUNNING;
        while (isActive() && state == PRGM_STATE.RUNNING) {
            _loop();
            postLoop();
        }

        state = PRGM_STATE.STOPPED;
        _stop();
    }

    private boolean isActive() {
        synchronized (lop) {
            return !lop.isStarted() || lop.opModeIsActive();
        }
    }

    //region Getters/Setters
    public synchronized void kill() {
        state = PRGM_STATE.STOPPED;
    }

    public synchronized void enableTelemetry()  {
        telemetryIsActive = true;
    }
    public synchronized void disableTelemetry() {
        telemetryIsActive = false;
    }
    //endregion
}
