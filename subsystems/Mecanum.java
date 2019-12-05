package quasar.subsystems;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import quasar.lib.MoreMath;
import quasar.lib.SubSystem;

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
        opm.telemetry.addData("    X (Active)", imu.getAngularOrientation().firstAngle);
        opm.telemetry.addData("    Y", imu.getAngularOrientation().secondAngle);
        opm.telemetry.addData("    Z", imu.getAngularOrientation().thirdAngle);
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
        left = scaleControls(gamepad1.left_stick_x);
        fwd = -scaleControls(gamepad1.left_stick_y);
        rot = scaleControls(gamepad1.right_stick_x);

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

    @Auto public void moveVector(double x, double y) {
        calculatePowers(y, x, 0);
        normalizeMotorPowers();
        setMotorPowers();
    }
    @Auto public void moveVectorTime(double x, double y, long timeMS) {
        long endTime = System.currentTimeMillis() + timeMS;

        try {
            moveVector(x, y);
            sleep(timeMS - 300);
            moveVector(x / 2, y / 2);
            sleep(300);
        } catch (InterruptedException e) {
            zeroMotors();
        }
        zeroMotors();
    }
    @Auto public void moveAngle(double deg) {
        double theta = Math.toDegrees(deg + (Math.PI / 2) );
        double x = Math.cos(theta);
        double y = Math.sin(theta);

        moveVector(x, y);
    }
    @Auto public void turnDegrees(double deg) {
        calculatePowers(0,0,-Math.signum(deg) * 0.5);
        setMotorPowers();

        double start = imu.getAngularOrientation().firstAngle;
        double end = deg + start;
        double curr = start;
        double diff = end - curr;
        while(Math.abs(diff) > 5 && lop.opModeIsActive()) {
            curr = imu.getAngularOrientation().firstAngle;
            diff = end - curr;

            if(Math.abs(diff) > 30) calculatePowers(0,0,-Math.signum(deg));
            else if(Math.abs(diff) > 15) calculatePowers(0,0,-Math.signum(deg) * 0.5);
            else if(Math.abs(diff) > 5) calculatePowers(0,0,-Math.signum(deg) * 0.25);
            setMotorPowers();

            opm.telemetry.addData("start", start);
            opm.telemetry.addData("end", end);
            opm.telemetry.addData("curr", curr);
            opm.telemetry.addData("diff", diff);
            opm.telemetry.update();
        }

        calculatePowers(0d,0d,0d);
        setMotorPowers();

    }

    @Auto public void zeroMotors() {
        powers = new double[] {0d,0d,0d,0d};
        setMotorPowers();
    }
    //endregion Mecanum

}
