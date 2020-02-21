package quasar.threadsubsystems;

import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.GamepadState;
import quasar.lib.ThreadSubSystem;
import quasar.lib.macro.MacroSystem;

public final class PlatformMover extends ThreadSubSystem implements MacroSystem {

    private Servo platformLeft, platformRight;

    private final double LEFT_DOWN = 0.0, LEFT_UP = 0.72, RIGHT_DOWN = 0.82, RIGHT_UP = 0.13;
    private boolean isDown = false;

    //region SubSystem
    @Override
    protected void _init() {
        platformLeft  = hmap.servo.get("platformLeft");
        platformRight = hmap.servo.get("platformRight");

        platformLeft.setPosition(LEFT_UP);
        platformRight.setPosition(RIGHT_UP);
    }

    @Override
    protected void _loop() {
        isDown = GamepadState.toggle(gamepad1.dpad_right, prev1.dpad_right, isDown);
        updateHooks();
    }

    @Override
    protected void _stop() {
        //We don't really care what happens at this point, it's hard to break this system
    }

    @Override
    protected void _telemetry() {
        String state = "?";
        if(platformLeft.getPosition() == LEFT_DOWN) state = "DOWN";
        if(platformLeft.getPosition() == LEFT_UP)   state = "UP";
        telemetry.addLine("PLATFORM MOVER");
        telemetry.addData("    Current State", state);
    }
    //endregion

    public synchronized void lowerHooks() {
        isDown = true;
        platformLeft.setPosition(LEFT_DOWN);
        platformRight.setPosition(RIGHT_DOWN);
    }
    public synchronized void raiseHooks() {
        isDown = false;
        platformLeft.setPosition(LEFT_UP);
        platformRight.setPosition(RIGHT_UP);
    }
    private void updateHooks() {
        if (isDown) lowerHooks();
        else raiseHooks();
    }

    //region Macro
    @Override
    public void setMacroState() {

    }

    @Override
    public void getMacroState() {

    }
    //endregion
}
