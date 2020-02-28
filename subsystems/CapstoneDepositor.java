package quasar.subsystems;

import com.qualcomm.robotcore.hardware.Servo;

public final class CapstoneDepositor extends SubSystem {

    private Servo capstoneServo;
    private final double ON_TOP = 0.85, OUT_OF_WAY = 0.35;
    private boolean isActive = false;

    @Override
    public void init() {
        capstoneServo = hmap.servo.get("capstone");
    }

    private boolean prevA = true;
    @Override
    public void loop() {
        if(gamepad2.a && !prevA) isActive = !isActive;
        if(isActive) capstoneServo.setPosition(ON_TOP);
        else capstoneServo.setPosition(OUT_OF_WAY);

        prevA = gamepad2.a;

        postLoop();
    }

    @Override
    protected void telemetry() {
        telemetry.addLine("CAPSTONE");
        telemetry.addData("    Position", isActive ? "ACTIVE":"INACTIVE");
        telemetry.addLine();
    }

    @Override
    public void stop() {

    }
}
