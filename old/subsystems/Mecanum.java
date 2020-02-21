package quasar.old.subsystems;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import quasar.lib.MoreMath;
import quasar.old.SubSystem;

import static java.lang.Thread.sleep;

public class Mecanum extends SubSystem {

    //region Tele Variables
    public DcMotor fl, fr, bl, br;

    private final double THRESHOLD = 0.1;

    private double left, fwd, rot;

    //These are the coefficients for the motorpowers in order of fl, fr, bl, br (in order as defined above)
    private final double[] FWD    = {1,1,1,1};
    private final double[] STRAFE = {1,-1,-1,1};
    private final double[] TURN   = {1,-1,1,-1};

    private       double[] powers = {0, 0, 0,0};
    //endregion
    //region Auto Variables
    BNO055IMU imu;

    double startDegrees = 0;

    double blockToTick = 900;
    double degToTick = 9.8;
    double blockToTickStrafe = 1200;
    //endregion

    //region SubSystem
    @Override public void init() {
        fl = hardwareMap.dcMotor.get("fl");
        fr = hardwareMap.dcMotor.get("fr");
        bl = hardwareMap.dcMotor.get("bl");
        br = hardwareMap.dcMotor.get("br");

        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        opm.telemetry.addLine("Mecanum ready");
    }
    @Override public void autoInit() {
        fl = hardwareMap.dcMotor.get("fl");
        fr = hardwareMap.dcMotor.get("fr");
        bl = hardwareMap.dcMotor.get("bl");
        br = hardwareMap.dcMotor.get("br");

        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        fl.setTargetPosition(0);
        fr.setTargetPosition(0);
        bl.setTargetPosition(0);
        br.setTargetPosition(0);

        fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        br.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);

        startDegrees = imu.getAngularOrientation().firstAngle;

        opm.telemetry.addLine("Mecanum ready");
    }
    @Override @Tele public void loop() {

        zeroControls();
        calculatePowers(fwd, left, rot);
        normalizeMotorPowers();
        setMotorPowers();

        postLoop();
    }
    @Override public void stop() {
        zeroMotors();
    }
    @Override protected void telemetry() {
        opm.telemetry.addLine("MECANUM");
        opm.telemetry.addData("    FL", powers[0]);
        opm.telemetry.addData("    FR", powers[1]);
        opm.telemetry.addData("    BL", powers[2]);
        opm.telemetry.addData("    BR", powers[3]);
        opm.telemetry.addLine();
        opm.telemetry.addData("    Forward", fwd);
        opm.telemetry.addData("    Left", left);
        opm.telemetry.addData("    Rotation", rot);
        opm.telemetry.addLine();
    }
    //endregion SubSystem
    //region Mecanum
    public void useTestBotConfig() {
        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        fr.setDirection(DcMotorSimple.Direction.FORWARD);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);
        br.setDirection(DcMotorSimple.Direction.FORWARD);

        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    public void useCompBotConfig() {
        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        fr.setDirection(DcMotorSimple.Direction.FORWARD);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);
        br.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Tele private void calculatePowers(double forward, double strafe, double rotation) {
        //Why tf doesn't java have list stuff automatically???  Why do I have to make this stuff manually?? >:(
        powers = MoreMath.listMultiply(forward, FWD);
        powers = MoreMath.listAdd(powers, MoreMath.listMultiply(strafe, STRAFE));
        powers = MoreMath.listAdd(powers, MoreMath.listMultiply(rotation, TURN));
    }
    @Tele private void zeroControls() {
        if(gamepad1.right_bumper) {
            left = (gamepad1.left_stick_x) * 0.4;
            fwd = -(gamepad1.left_stick_y) * 0.4;
            rot = (gamepad1.right_stick_x) * 0.4;
        } else {
            left = (gamepad1.left_stick_x);
            fwd = -(gamepad1.left_stick_y);
            rot = (gamepad1.right_stick_x);
        }

        left = (Math.abs(left)<THRESHOLD)?0:left;
        fwd = (Math.abs(fwd)<THRESHOLD)?0:fwd;
        rot = (Math.abs(rot)<THRESHOLD)?0:rot;
    }
    @Tele private double scaleControls(double x) {
        return Math.signum(x) * Math.pow(x, 2);
    }

    private void setMotorPowers() {
        fl.setPower(powers[0]);
        fr.setPower(powers[1]);
        bl.setPower(powers[2]);
        br.setPower(powers[3]);
    }
    private void normalizeMotorPowers() {

        double max = 1;
        for (double i : powers) {
            if(Math.abs(i) > Math.abs(max)) max = Math.abs(i);
        }

        powers = MoreMath.listMultiply(1/max, powers);

    }

    @Auto private void setAutoSpeed(double pwr) {
        fl.setPower(pwr);
        fr.setPower(pwr);
        bl.setPower(pwr);
        br.setPower(pwr);
    }

    @Auto private boolean isBusy() {
        int threshold = 3;
        int value = 0;
        if(fl.isBusy()) value += 1;
        if(fr.isBusy()) value += 1;
        if(bl.isBusy()) value += 1;
        if(br.isBusy()) value += 1;

        return value >= threshold;
    }

    @Auto public void setPowers(double forward, double strafe, double rotation) {
        encoderMode();

        calculatePowers(forward, strafe, rotation);
        normalizeMotorPowers();
        setMotorPowers();
    }

    @Auto private void fwdTicks(int ticks, double pwr) {
        fl.setTargetPosition(fl.getCurrentPosition() + ticks);
        fr.setTargetPosition(fr.getCurrentPosition() + ticks);
        bl.setTargetPosition(bl.getCurrentPosition() + ticks);
        br.setTargetPosition(br.getCurrentPosition() + ticks);

        setAutoSpeed(pwr);

        while(isBusy() && lop.opModeIsActive()) lop.idle();
    }
    @Auto private void turnLeftTicks(int ticks, double pwr) {
        fl.setTargetPosition(fl.getCurrentPosition() - ticks);
        fr.setTargetPosition(fr.getCurrentPosition() + ticks);
        bl.setTargetPosition(bl.getCurrentPosition() - ticks);
        br.setTargetPosition(br.getCurrentPosition() + ticks);

        setAutoSpeed(pwr);

        while(isBusy() && lop.opModeIsActive()) lop.idle();
    }
    @Auto private void strafeLeftTicks(int ticks, double pwr) {
        fl.setTargetPosition(fl.getCurrentPosition() - ticks);
        fr.setTargetPosition(fr.getCurrentPosition() + ticks);
        bl.setTargetPosition(bl.getCurrentPosition() + ticks);
        br.setTargetPosition(br.getCurrentPosition() - ticks);

        setAutoSpeed(pwr);

        while(isBusy() && lop.opModeIsActive()) lop.idle();
    }

    @Auto public void fwdBlocks(double blocks, double pwr) {
        fwdTicks((int) (blockToTick * blocks), pwr);
    }
    @Auto public void turnLeftDegrees(double degrees, double pwr) {
        turnLeftTicks((int) (degToTick * degrees), pwr);
    }
    @Auto public void strafeLeftBlock(double blocks, double pwr) {
        strafeLeftTicks((int) (blockToTickStrafe * blocks), pwr);
    }

    @Auto public void fwdSecs(double secs, double pwr) {
        encoderMode();

        setAutoSpeed(pwr);

        long endTime = (long) (System.currentTimeMillis() + 1000 * secs);
        while(System.currentTimeMillis() < endTime && lop.opModeIsActive()) lop.idle();

        runToPositionMode();
    }

    /**
     * This resets our angle to whatever GLOBAL angle we want.  For example, if we want to go back to our
     * original angle exactly, we'd enter turnGlobalDegrees(0).  This is necessary because our dead-
     * reckoning angle system is faster, but we always slightly get it off.  By doing this every once
     * in a while, we can avoid propagating errors
     * @param deg
     */
    @Auto public void turnGlobalDegrees(double deg) {
        encoderMode();

        double start = imu.getAngularOrientation().firstAngle;
        double end = deg + startDegrees;

        double diff = end - imu.getAngularOrientation().firstAngle;
        double timeOut = System.currentTimeMillis() + 5000;
        while(Math.abs(diff) > 2 && System.currentTimeMillis() < timeOut && lop.opModeIsActive()) {
            diff = end - imu.getAngularOrientation().firstAngle;

            double rot = Math.abs(diff) * 0.01;
            rot = Math.max(rot, 0.1);
            rot = Math.min(rot, 0.7);
            rot *= -Math.signum(diff);

            calculatePowers(0,0,rot);
            setMotorPowers();

            opm.telemetry.addData("Power", rot);
            opm.telemetry.addData("Current Angle", imu.getAngularOrientation());
            opm.telemetry.update();
        }

        runToPositionMode();
    }

    @Auto public void zeroMotors() {
        powers = new double[] {0d,0d,0d,0d};
        setMotorPowers();
    }

    @Auto public void runToPositionMode() {
        fl.setTargetPosition(0);
        fr.setTargetPosition(0);
        bl.setTargetPosition(0);
        br.setTargetPosition(0);

        fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        br.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    @Auto public void encoderMode() {
        fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    //endregion Mecanum

}
