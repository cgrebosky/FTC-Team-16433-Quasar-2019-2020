package quasar.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.SubSystem;
import quasar.lib.macro.MacroState;
import quasar.lib.macro.MacroSystem;

public final class Collector extends SubSystem implements MacroSystem {
    private DcMotor collectorLeft, collectorRight;
    private Servo limiterLeft, limiterRight;

    private final double LEFT_IN = 0, LEFT_OPEN = 1, LEFT_HALF = 0.77, RIGHT_IN = 1, RIGHT_OPEN = 0, RIGHT_HALF = 0.4;

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

        close();
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
        if(limiterLeft.getPosition() == LEFT_IN) pos = "IN";
        if(limiterLeft.getPosition() == LEFT_HALF) pos = "HALF";
        if(limiterLeft.getPosition() == LEFT_OPEN) pos = "OPEN";
        telemetry.addData("    Current Position", pos);
        telemetry.addData("    Power", gamepad1.left_trigger - gamepad1.right_trigger);
        telemetry.addLine();
    }
    //endregion

    private void open() {
        limiterLeft.setPosition(LEFT_OPEN);
        limiterRight.setPosition(RIGHT_OPEN);
    }
    private void close() {
        limiterLeft.setPosition(LEFT_IN);
        limiterRight.setPosition(RIGHT_IN);
    }
    private void half() {
        limiterLeft.setPosition(LEFT_HALF);
        limiterRight.setPosition(RIGHT_HALF);
    }

    //region Macro
    @Override
    public void recordMacroState() {
        MacroState.Companion.getCurrentMacroState().setColLeftPow(collectorLeft.getPower());
        MacroState.Companion.getCurrentMacroState().setColRightPow(collectorRight.getPower());

        MacroState.Companion.getCurrentMacroState().setLeftLim(limiterLeft.getPosition());
        MacroState.Companion.getCurrentMacroState().setRightLim(limiterRight.getPosition());
    }
    @Override
    public void playMacroState(MacroState m) {
        collectorLeft.setPower(m.getColLeftPow());
        collectorRight.setPower(m.getColRightPow());

        limiterLeft.setPosition(m.getLeftLim());
        limiterRight.setPosition(m.getRightLim());
    }
    //endregion

}
