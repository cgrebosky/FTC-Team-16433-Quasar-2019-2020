package quasar.subsystems;

import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.macro.MacroState;
import quasar.lib.macro.MacroSystem;

public final class AutoBlockMover extends SubSystem implements MacroSystem {

    private Servo leftArm, leftClaw, rightArm, rightClaw;

    private double LEFT_ARM_UP = 0, LEFT_ARM_DOWN = 0, RIGHT_ARM_UP = 0, RIGHT_ARM_DOWN = 0;
    private double LEFT_CLAW_CLOSED = 0, LEFT_CLAW_OPEN = 0, RIGHT_CLAW_CLOSED = 0, RIGHT_CLAW_OPEN = 0;

    @Override
    public void init() {
        leftArm   = hmap.servo.get("BMLeftArm");
        rightArm  = hmap.servo.get("BMRightArm");
        leftClaw  = hmap.servo.get("BMLeftClaw");
        rightClaw = hmap.servo.get("BMRightClaw");
    }

    @Override
    public void loop() {
        leftClaw.setPosition(LEFT_CLAW_CLOSED);
        rightClaw.setPosition(RIGHT_CLAW_CLOSED);
        leftArm.setPosition(LEFT_ARM_UP);
        rightArm.setPosition(RIGHT_ARM_UP);
    }

    @Override
    protected void telemetry() {
        //There's nothing happening in teleop, so there's nothing important here :'(
    }

    @Override
    public void stop() {
        //Again, nothing important here.  I've found that doing servo stuff at stop() causes more pain than gain
    }

    //region Macro
    @Override
    public void recordMacroState() {

    }

    @Override
    public void playMacroState(MacroState m) {

    }
    //endregion
}
