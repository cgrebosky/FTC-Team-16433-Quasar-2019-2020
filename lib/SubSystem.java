package quasar.lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.external.samples.ConceptNullOp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

abstract public class SubSystem {
    //Injected properties from constructor called in main method.
    protected LinearOpMode lop = null;
    protected OpMode       opm;

    //These are merely niceties, they can easily be accessed through opm.
    protected Gamepad gamepad1, gamepad2;
    protected GamepadState prev1 = new GamepadState(), prev2 = new GamepadState();

    protected HardwareMap hardwareMap;

    //region Methods
    @Auto public void create(LinearOpMode lop) {
        create( (OpMode) lop );

        this.lop = lop;
    }
    @Tele public void create(OpMode opm) {
        this.opm = opm;

        gamepad1 =    opm.gamepad1;
        gamepad2 =    opm.gamepad2;
        hardwareMap = opm.hardwareMap;
    }

    public boolean isAutonomous() {
        return !( lop == null );
    }

    public abstract void init();
    @Tele public void teleInit() { init(); }
    @Auto public void autoInit() { init(); }
    @Tele public abstract void loop();
    /**
     * Call this at the end of your loop() method, this will add functionality such as telemetry,
     * advanced gamepad controls, &c.
     */
    @Tele public void postLoop() {
        updateGamepadStates();
        telemetry();
    }
    /**
     * This will update prevs, allowing for more advanced gamepad control.
     * This should be called at the end of loop() method
     */
    @Tele protected void updateGamepadStates() {
        prev1 = new GamepadState(gamepad1);
        prev2 = new GamepadState(gamepad2);
    }
    /**
     * Put all your telemetry code in here, it will be automatically called in postLoop() method.
     */
    @Tele protected void telemetry() {}

    public abstract void stop();

    //endregion
    //region Annotations
    //Decorative annotations to mark purely autonomous or teleop methods
    @Retention(value = RetentionPolicy.SOURCE)
    public @interface Tele {}
    @Retention(value = RetentionPolicy.SOURCE)
    public @interface Auto {}
    //endregion Annotations
}
