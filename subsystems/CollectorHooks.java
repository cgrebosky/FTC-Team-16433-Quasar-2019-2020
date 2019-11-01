package quasar.subsystems;

import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.GamepadState;
import quasar.lib.SubSystem;

public class CollectorHooks extends SubSystem {

    private Servo left, right;

    private final double LEFT_CLOSED = 0.17, LEFT_OUT = 0.5, RIGHT_CLOSED = 0.43, RIGHT_OUT = 0.2;

    private boolean hooksClosed = true;

    @Override
    public void init() {
        left = hardwareMap.servo.get("collectorLeft");
        right = hardwareMap.servo.get("collectorRight");
    }
    @Override
    public void loop() {
        toggleHooks();

        postLoop();
    }
    @Override
    public void stop() {
        left.setPosition(LEFT_OUT);
        right.setPosition(RIGHT_OUT);
    }

    @Override
    protected void telemetry() {
        opm.telemetry.addLine("Collector");
        opm.telemetry.addData("    Collector hooks are ", hooksClosed?"CLOSED":"OPEN");
    }

    private void toggleHooks() {
        hooksClosed = gamepad1.dpad_left;
        if(hooksClosed) {
            left.setPosition(LEFT_CLOSED);
            right.setPosition(RIGHT_CLOSED);
        } else {
            left.setPosition(LEFT_OUT);
            right.setPosition(RIGHT_OUT);
        }
    }
}
