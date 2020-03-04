package quasar.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.macro.MacroState;
import quasar.lib.macro.MacroSystem;

public final class Collector extends SubSystem implements MacroSystem {
    private DcMotor collectorLeft, collectorRight;
    private Servo limiterLeft, limiterRight;

    private final double L_IN = 0.99, L_HALF = 0.48, L_OPEN = 0.29;
    private final double R_IN = 0, R_HALF = 0, R_OPEN = 0;

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

        limiterLeft = hmap.servo.get("limiterLeft");
        limiterRight = hmap.servo.get("limiterRight");

        limiterLeft.setPosition(L_IN);
        limiterRight.setPosition(R_IN);
    }

    @Override
    public void loop() {
        double pwr = gamepad1.left_trigger - gamepad1.right_trigger;
        collectorLeft.setPower(pwr);
        collectorRight.setPower(pwr);

        if(gamepad1.left_bumper) open();
        else half();

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
        if(limiterLeft.getPosition() == L_IN) pos = "IN";
        if(limiterLeft.getPosition() == L_HALF) pos = "HALF";
        if(limiterLeft.getPosition() == L_OPEN) pos = "OPEN";
        telemetry.addData("    Current Position", pos);
        telemetry.addData("    Power", gamepad1.left_trigger - gamepad1.right_trigger);
        telemetry.addLine();
    }
    //endregion

    public void open() {
        limiterLeft.setPosition(L_OPEN);
        limiterRight.setPosition(R_OPEN);
    }
    public void half() {
        limiterLeft.setPosition(L_HALF);
        limiterRight.setPosition(R_HALF);
    }
    public void close() {
        limiterLeft.setPosition(L_IN);
        limiterRight.setPosition(R_IN);
    }

    //region Macro
    @Override
    public void recordMacroState() {
        MacroState.Companion.getCurrentMacroState().setColLeftPow(collectorLeft.getPower());
        MacroState.Companion.getCurrentMacroState().setColRightPow(collectorRight.getPower());

        MacroState.Companion.getCurrentMacroState().setColLimiterL(limiterLeft.getPosition());
        MacroState.Companion.getCurrentMacroState().setColLimiterR(limiterRight.getPosition());
    }
    @Override
    public void playMacroState(MacroState m) {
        collectorLeft.setPower(m.getColLeftPow());
        collectorRight.setPower(m.getColRightPow());

        limiterLeft.setPosition(m.getColLimiterL());
        limiterRight.setPosition(m.getColLimiterR());
    }
    //endregion

}
