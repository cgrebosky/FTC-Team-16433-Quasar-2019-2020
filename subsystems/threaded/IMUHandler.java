package quasar.subsystems.threaded;

import com.qualcomm.hardware.bosch.BNO055IMU;

import quasar.subsystems.ThreadSubSystem;

public final class IMUHandler extends ThreadSubSystem {

    private double heading = 0;
    private double startHeading = 0;

    private BNO055IMU imu;

    //region SubSystem
    @Override
    protected void _init() {
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
    protected void _loop() {
        heading = imu.getAngularOrientation().firstAngle;
    }

    @Override
    protected void _stop() {
        imu.close();
    }

    @Override
    protected void _telemetry() {
        telemetry.addLine("IMU");
        telemetry.addData("    Heading", heading);
        telemetry.addData("    Initial Heading", startHeading);
        telemetry.addLine();
    }
    //endregion
    //region Getters
    public synchronized double getAbsoluteHeading() {
        return heading;
    }
    public synchronized double getRelativeHeading() {
        return heading - startHeading;
    }
    public synchronized double getStartHeading() {
        return startHeading;
    }
    //endregion
}
