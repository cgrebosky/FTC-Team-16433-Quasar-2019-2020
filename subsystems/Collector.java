package quasar.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.macro.MacroState;
import quasar.lib.macro.MacroSystem;

public final class Collector extends SubSystem implements MacroSystem {
    private DcMotor collectorLeft, collectorRight;
    private Servo limiter;

    private final double IN = 0, HALF = 0, OPEN = 0;

    //region SubSystem
    @Override
    public void init() {
        collectorLeft = hmap.dcMotor.get("collectorLeft");
        collectorRight = hmap.dcMotor.get("collectorRight");

        collectorLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        collectorRight.setDirection(DcMotorSimple.Direction.REVERSE);

        //This gives them a bit more flexibility on speed, which allows for better collection
        collectorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        collectorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        limiter = hmap.servo.get("limiter");

        limiter.setPosition(IN);
    }

    @Override
    public void loop() {
        double pwr = gamepad1.left_trigger - gamepad1.right_trigger;
        collectorLeft.setPower(pwr);
        collectorRight.setPower(pwr);

        if(gamepad1.left_bumper) limiter.setPosition(OPEN);
        else limiter.setPosition(HALF);

        postLoop();
    }

    @Override
    public void stop() {
        collectorLeft.setPower(0);
        collectorRight.setPower(0);
    }

    @Override
    protected void telemetry() {
        telemetry.addLine("COLLECTOR");

        String pos = "?";
        if(limiter.getPosition() == IN) pos = "IN";
        if(limiter.getPosition() == HALF) pos = "HALF";
        if(limiter.getPosition() == OPEN) pos = "OPEN";
        telemetry.addData("    Current Position", pos);
        telemetry.addData("    Power", gamepad1.left_trigger - gamepad1.right_trigger);
        telemetry.addLine();
    }
    //endregion

    //region Macro
    @Override
    public void recordMacroState() {
        MacroState.Companion.getCurrentMacroState().setColLeftPow(collectorLeft.getPower());
        MacroState.Companion.getCurrentMacroState().setColRightPow(collectorRight.getPower());

        MacroState.Companion.getCurrentMacroState().setColLimiter(limiter.getPosition());
    }
    @Override
    public void playMacroState(MacroState m) {
        collectorLeft.setPower(m.getColLeftPow());
        collectorRight.setPower(m.getColRightPow());

        limiter.setPosition(m.getColLimiter());
    }
    //endregion

}
