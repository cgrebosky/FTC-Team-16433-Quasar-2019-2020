package quasar.threadsubsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.ThreadSubSystem;
import quasar.lib.macro.MacroSystem;

public final class Collector extends ThreadSubSystem implements MacroSystem {
    private DcMotor collectorLeft, collectorRight;
    private Servo limiterLeft, limiterRight;

    private final double LEFT_IN = 0, LEFT_OPEN = 1, LEFT_HALF = 0.77, RIGHT_IN = 1, RIGHT_OPEN = 0, RIGHT_HALF = 0.4;

    //region SubSystem
    @Override
    public void _init() {
        collectorLeft = hmap.dcMotor.get("collectorLeft");
        collectorRight = hmap.dcMotor.get("collectorRight");

        collectorLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        collectorRight.setDirection(DcMotorSimple.Direction.REVERSE);

        //This gives them a bit more flexibility on speed, which allows for better collection
        collectorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        collectorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        limiterLeft = hmap.servo.get("limiterLeft");
        limiterRight = hmap.servo.get("limiterRight");
    }

    @Override
    public void _loop() {
        double pwr = gamepad1.left_trigger - gamepad1.right_trigger;
        collectorLeft.setPower(pwr);
        collectorRight.setPower(pwr);

        if(gamepad1.left_bumper) open();
        else half();
    }

    @Override
    public void _stop() {
        collectorLeft.setPower(0);
        collectorRight.setPower(0);
    }

    @Override
    public void _telemetry() {
        telemetry.addLine("COLLECTOR");

        String pos = "?";
        if(limiterLeft.getPosition() == LEFT_IN) pos = "IN";
        if(limiterLeft.getPosition() == LEFT_HALF) pos = "HALF";
        if(limiterLeft.getPosition() == LEFT_OPEN) pos = "OPEN";
        telemetry.addData("    Current Position", pos);
        telemetry.addData("    Power", gamepad1.left_trigger - gamepad1.right_trigger);
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
    public void setMacroState() {

    }
    @Override
    public void getMacroState() {

    }
    //endregion

}
