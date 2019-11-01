package quasar.subsystems;

import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.GamepadState;
import quasar.lib.SubSystem;

public class PlatformMover extends SubSystem {

    private final double LEFT_DOWN = 0.8, LEFT_UP = 0.05, RIGHT_DOWN = 0.25, RIGHT_UP = 0.95;

    public Servo left, right;

    private boolean armsDown = false;

    @Override
    public void init() {
        left = hardwareMap.servo.get("platformLeft");
        right = hardwareMap.servo.get("platformRight");
    }
    @Override
    public void loop() {
        toggleHooks();

        postLoop();
    }
    @Override
    public void stop() {
        liftHooks();
    }
    @Override
    protected void telemetry() {
        opm.telemetry.addLine("PLATFORM MOVER");
        opm.telemetry.addLine("    Platform hooks are currently " + (armsDown?"DOWN":"UP") );
    }

    private void toggleHooks() {
        armsDown = GamepadState.toggle(gamepad1.dpad_right, prev1.dpad_right, armsDown);

        if(armsDown) liftHooks();
        else lowerHooks();
    }
    private void liftHooks() {
        left.setPosition(LEFT_UP);
        right.setPosition(RIGHT_UP);
    }
    private void lowerHooks() {
        left.setPosition(LEFT_DOWN);
        right.setPosition(RIGHT_DOWN);
    }
}
