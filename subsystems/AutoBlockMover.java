package quasar.subsystems;

import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.macro.MacroState;
import quasar.lib.macro.MacroSystem;
import quasar.prod.Side;

public final class AutoBlockMover extends SubSystem implements MacroSystem {

    public Servo leftArm, leftClaw, rightArm, rightClaw;

    private double LEFT_UP  = 0.3, LEFT_HALF  = 0.6, LEFT_DOWN  = 0.75;
    private double RIGHT_UP = 0.63, RIGHT_HALF = 0.3, RIGHT_DOWN = 0.1;
    private double LEFT_CLOSED  = 0.35, LEFT_OPEN  = 0.11;
    private double RIGHT_CLOSED = 0.05, RIGHT_OPEN = 0.3;

    @Override
    public void init() {
        leftArm   = hmap.servo.get("BMLeftArm");
        rightArm  = hmap.servo.get("BMRightArm");
        leftClaw  = hmap.servo.get("BMLeftClaw");
        rightClaw = hmap.servo.get("BMRightClaw");

        leftArm.setPosition(LEFT_UP);
        rightArm.setPosition(RIGHT_UP);
        leftClaw.setPosition(LEFT_CLOSED);
        rightClaw.setPosition(RIGHT_CLOSED);
    }

    @Override
    public void loop() {
        leftClaw.setPosition(LEFT_CLOSED);
        rightClaw.setPosition(RIGHT_CLOSED);
        leftArm.setPosition(LEFT_UP);
        rightArm.setPosition(RIGHT_UP);
    }

    @Override
    protected void telemetry() {
        //There's nothing happening in teleop, so there's nothing important here :'(
    }

    @Override
    public void stop() {
        //Again, nothing important here.  I've found that doing servo stuff at stop() causes more pain than gain
    }

    public void halfLower(Side s) {
        if(s == Side.RED) {
            rightArm.setPosition(RIGHT_HALF);
            rightClaw.setPosition(RIGHT_OPEN);
        } else {
            leftArm.setPosition(LEFT_HALF);
            leftClaw.setPosition(LEFT_OPEN);
        }
    }
    public void lower(Side s) {
        if(s == Side.RED) rightArm.setPosition(RIGHT_DOWN);
        else leftArm.setPosition(LEFT_DOWN);
    }
    public void close(Side s) {
        if(s == Side.RED) rightClaw.setPosition(RIGHT_CLOSED);
        else leftClaw.setPosition(LEFT_CLOSED);
    }
    public void raise(Side s) {
        if(s == Side.RED) rightArm.setPosition(RIGHT_UP);
        else leftArm.setPosition(LEFT_UP);
    }
    public void release(Side s) {
        if(s == Side.RED) rightClaw.setPosition(RIGHT_OPEN);
        else leftClaw.setPosition(LEFT_OPEN);
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
