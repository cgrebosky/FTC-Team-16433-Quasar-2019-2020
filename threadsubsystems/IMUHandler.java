package quasar.threadsubsystems;

import com.qualcomm.hardware.bosch.BNO055IMU;

import quasar.lib.ThreadSubSystem;

public final class IMUHandler extends ThreadSubSystem {

    private double heading = 0;
    private double startHeading = 0;

    BNO055IMU imu;

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

        startHeading = imu.getAngularOrientation().firstAngle + 180;
    }

    @Override
    protected void _loop() {
        heading = imu.getAngularOrientation().firstAngle + 180; //The IMU gives us vaues in [-180, 180], so this converts
                                                                //it to [0, 360] so it's slightly easier to work with.
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
    }
    //endregion
    //region Getters
    public synchronized double getAbsoluteHeading() {
        return heading;
    }
    public synchronized double getRelativeHeading() {
        return heading - startHeading;
    }
    //endregion
}
