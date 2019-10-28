package quasar.subsystems;

import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.GamepadState;
import quasar.lib.SubSystem;

public class PlatformMover extends SubSystem {

    private final double LEFT_DOWN = 0.8, LEFT_UP = 0.05, RIGHT_DOWN = 0.25, RIGHT_UP = 0.95;

    private Servo left, right;

    private boolean armsDown = false;

    @Override
    public void init() {
        left = hardwareMap.servo.get("platformLeft");
        right = hardwareMap.servo.get("platformRight");
    }
    @Override
    public void loop() {
        armsDown = GamepadState.toggle(gamepad1.dpad_left, prev1.dpad_left, armsDown);
        updateArms();

        postLoop();
    }
    @Override
    public void stop() {
        liftArms();
    }
    @Override
    protected void telemetry() {
        opm.telemetry.addLine("Platform hooks are currently " + (armsDown?"DOWN":"UP") );
    }

    private void updateArms() {
        if(armsDown) lowerArms();
        else liftArms();
    }
    private void liftArms() {
        left.setPosition(LEFT_UP);
        right.setPosition(RIGHT_UP);
    }
    private void lowerArms() {
        left.setPosition(LEFT_DOWN);
        right.setPosition(RIGHT_DOWN);
    }
}
