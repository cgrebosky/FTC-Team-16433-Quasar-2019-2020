package quasar.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.GamepadState;
import quasar.lib.SubSystem;

public class Lift extends SubSystem {

    final double limitLeftOut = 0.9, limitLeftIn = 0.5, limitRightOut = 0, limitRightIn = 0.4;
    final double clawLeftClosed = 0.67, clawLeftOpen = 0.44, clawRightClosed = 0.3, clawRightOpen = 0.14, angleIn = 0, angleOut = 1;

    Servo limitLeft, limitRight;
    public DcMotor liftLeft, liftRight;
    public CRServo extenderLeft, extenderRight;
    public Servo clawLeft, clawRight, clawAngle;

    public DigitalChannel liftLimitLeft, liftLimitRight;

    @Override
    public void init() {
        liftLeft = hardwareMap.dcMotor.get("liftLeft");
        liftRight = hardwareMap.dcMotor.get("liftRight");
        liftLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        liftRight.setDirection(DcMotorSimple.Direction.FORWARD);
        liftLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        extenderLeft = hardwareMap.crservo.get("extenderLeft");
        extenderRight = hardwareMap.crservo.get("extenderRight");
        extenderRight.setDirection(DcMotorSimple.Direction.REVERSE);
        extenderLeft.setDirection(DcMotorSimple.Direction.FORWARD);

        clawLeft = hardwareMap.servo.get("clawLeft");
        clawRight = hardwareMap.servo.get("clawRight");
        clawAngle = hardwareMap.servo.get("clawAngle");

        limitLeft = hardwareMap.servo.get("limitLeft");
        limitRight = hardwareMap.servo.get("limitRight");
        limitLeft.setPosition(limitLeftIn);
        limitRight.setPosition(limitRightIn);

        liftLimitLeft = hardwareMap.get(DigitalChannel.class, "liftLimitLeft");
        liftLimitRight = hardwareMap.get(DigitalChannel.class, "liftLimitRight");

        liftLimitLeft.setMode(DigitalChannel.Mode.INPUT);
        liftLimitRight.setMode(DigitalChannel.Mode.INPUT);
    }

    @Override
    public void loop() {
        controlLift();
        controlArms();
        controlClaw();
        limitLeft.setPosition(limitLeftOut);
        limitRight.setPosition(limitRightOut);

        opm.telemetry.addLine("LIFT");
        opm.telemetry.addData("    Extender Power", gamepad2.left_stick_y);
        opm.telemetry.addData("    Left Trigger", liftLimitLeft.getState());
        opm.telemetry.addData("    Right Trigger", liftLimitRight.getState());
    }

    @Override
    public void stop() {
        liftLeft.setPower(0);
        liftRight.setPower(0);
        extenderLeft.setPower(0);
        extenderRight.setPower(0);
    }

    private void controlLift() {
        double liftPwr = gamepad2.left_trigger - gamepad2.right_trigger;
        if(Math.abs(liftPwr) < 0.1) liftPwr = 0;

        double leftPwr = (liftPwr > 0 && !liftLimitLeft.getState())?0: liftPwr;
        double rightPwr = (liftPwr > 0 && !liftLimitRight.getState())?0: liftPwr;

        opm.telemetry.addData("Left Lift Power", leftPwr);
        opm.telemetry.addData("Right Lift Power", rightPwr);

        liftLeft.setPower(leftPwr);
        liftRight.setPower(rightPwr);
    }
    private void controlArms() {
        double pwr = -gamepad2.left_stick_y;
        extenderLeft.setPower(pwr);
        extenderRight.setPower(pwr);
    }

    boolean prevRightBumper = true;
    boolean clawIsOut = false;

    boolean prevLeftBumper = true;
    boolean clawIsClosed = false;
    private void controlClaw() {
        clawIsOut = GamepadState.toggle(gamepad2.right_bumper, prevRightBumper, clawIsOut);
        prevRightBumper = gamepad2.right_bumper;
        if(clawIsOut) clawAngle.setPosition(angleOut);
        else clawAngle.setPosition(angleIn);

        clawIsClosed = GamepadState.toggle(gamepad2.left_bumper, prevLeftBumper, clawIsClosed);
        prevLeftBumper = gamepad2.left_bumper;
        if(clawIsClosed) {
            clawLeft.setPosition(clawLeftClosed);
            clawRight.setPosition(clawRightClosed);
        } else {
            clawLeft.setPosition(clawLeftOpen);
            clawRight.setPosition(clawRightOpen);
        }
    }
}
