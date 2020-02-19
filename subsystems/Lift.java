package quasar.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import quasar.lib.GamepadState;
import quasar.lib.SubSystem;
import quasar.prod.QuasarAuto1BFP;

import static java.lang.Thread.sleep;

public class Lift extends SubSystem {

    final double clawLeftClosed = 0.67, clawLeftOpen = 0.44, clawRightClosed = 0.3, clawRightOpen = 0.1, angleIn = 0.23, angleOut = 1;

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
        clawLeft.setPosition(clawLeftOpen);
        clawRight.setPosition(clawRightOpen);
        clawAngle.setPosition(angleIn);

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
        pwr = Math.min(0.9, pwr);
        pwr = Math.max(-0.9, pwr); //clip to [-0.9,0.9]

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
            closeClaw();
        } else {
            openClaw();
        }
    }
    public void closeClaw() {
        clawLeft.setPosition(clawLeftClosed);
        clawRight.setPosition(clawRightClosed);
    }
    public void openClaw() {
        clawLeft.setPosition(clawLeftOpen);
        clawRight.setPosition(clawRightOpen);
    }
    @Auto public void extendArm() {
        extenderLeft.setPower(0.9);
        extenderRight.setPower(0.9);
        try {
            sleep(1600);
        } catch (InterruptedException e) {
            extenderLeft.setPower(0);
            extenderRight.setPower(0);
        }

        extenderLeft.setPower(0);
        extenderRight.setPower(0);
        clawAngle.setPosition(angleOut);

    }
    @Auto public void retractArm() {
        clawAngle.setPosition(angleIn);
        try {
            sleep(300);
        } catch (InterruptedException e) {

        }

        extenderLeft.setPower(-0.9);
        extenderRight.setPower(-0.9);

        try {
            sleep(1700);
        } catch (InterruptedException e) {
            extenderLeft.setPower(0);
            extenderRight.setPower(0);
        }

        extenderLeft.setPower(0);
        extenderRight.setPower(0);
    }
    @Auto public void liftArms() {
        liftLeft.setPower(-0.5);
        liftRight.setPower(-0.5);
        try {
            sleep(400);
        } catch (InterruptedException e) {
            liftLeft.setPower(0);
            liftRight.setPower(0);
        }

        liftLeft.setPower(0);
        liftRight.setPower(0);
    }
    @Auto public void lowerArms() {
        liftLeft.setPower(0.5);
        liftRight.setPower(0.5);
        try {
            sleep(800);
        } catch (InterruptedException e) {
            liftLeft.setPower(0);
            liftRight.setPower(0);
        }

        liftLeft.setPower(0);
        liftRight.setPower(0);
    }
}
