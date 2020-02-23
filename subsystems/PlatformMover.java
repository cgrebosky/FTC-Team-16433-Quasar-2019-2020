package quasar.subsystems;

import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.GamepadState;
import quasar.lib.macro.MacroState;
import quasar.lib.macro.MacroSystem;

public final class PlatformMover extends SubSystem implements MacroSystem {

    private Servo platformLeft, platformRight;

    private final double LEFT_DOWN = 0.0, LEFT_UP = 0.72, RIGHT_DOWN = 0.82, RIGHT_UP = 0.13;
    private boolean isDown = false;

    //region SubSystem
    @Override
    public void init() {
        platformLeft  = hmap.servo.get("platformLeft");
        platformRight = hmap.servo.get("platformRight");

        platformLeft.setPosition(LEFT_UP);
        platformRight.setPosition(RIGHT_UP);
    }

    @Override
    public void loop() {
        isDown = GamepadState.toggle(gamepad1.dpad_right, prev1.dpad_right, isDown);
        updateHooks();

        postLoop();
    }

    @Override
    public void stop() {
        //We don't really care what happens at this point, it's hard to break this system
    }

    @Override
    public void telemetry() {
        String state = "?";
        if(platformLeft.getPosition() == LEFT_DOWN) state = "DOWN";
        if(platformLeft.getPosition() == LEFT_UP)   state = "UP";
        telemetry.addLine("PLATFORM MOVER");
        telemetry.addData("    Current State", state);
        telemetry.addLine();
    }
    //endregion

    private void lowerHooks() {
        isDown = true;
        platformLeft.setPosition(LEFT_DOWN);
        platformRight.setPosition(RIGHT_DOWN);
    }
    private void raiseHooks() {
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
    public void recordMacroState() {
        MacroState.Companion.getCurrentMacroState().setPfLeftPos(platformLeft.getPosition());
        MacroState.Companion.getCurrentMacroState().setPfRightPos(platformRight.getPosition());
    }

    @Override
    public void playMacroState(MacroState m) {
        platformLeft.setPosition(m.getPfLeftPos());
        platformRight.setPosition(m.getPfRightPos());
    }
    //endregion
}
