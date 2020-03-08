package quasar.subsystems.sensory;

import com.qualcomm.hardware.bosch.BNO055IMU;

import quasar.subsystems.SubSystem;

public final class IMUHandler extends SubSystem {

    private double heading = 0;
    private double startHeading = 0;

    private BNO055IMU imu;

    //region SubSystem
    @Override
    public void init() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;

        imu = hmap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);

        startHeading = imu.getAngularOrientation().firstAngle;
    }

    @Override
    public void loop() {
        heading = imu.getAngularOrientation().firstAngle;

        postLoop();
    }

    @Override
    public void stop() {
        imu.close();
    }

    @Override
    protected void telemetry() {
        telemetry.addLine("IMU");
        telemetry.addData("    Heading", heading);
        telemetry.addData("    Initial Heading", startHeading);
        telemetry.addLine();
    }
    //endregion

    public  double getAbsoluteHeading() {
        heading = imu.getAngularOrientation().firstAngle;
        return heading;
    }
}
