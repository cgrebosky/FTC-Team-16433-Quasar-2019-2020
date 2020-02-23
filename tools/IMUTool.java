package quasar.tools;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

@TeleOp(name = "IMU Tool", group = "Tests")
public final class IMUTool extends OpMode {

    private BNO055IMU imu;
    private double startDeg = 0;

    private double cycleTime = 0, prevTime = 0;

    private double x = 0, y = 0;
    private double dx = 0, dy = 0;

    @Override
    public void init() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

        startDeg = imu.getAngularOrientation().firstAngle;
    }

    @Override
    public void loop() {
        cycleTime = System.currentTimeMillis() - prevTime;
        prevTime = System.currentTimeMillis();

        Orientation o  = imu.getAngularOrientation();
        Acceleration a = imu.getAcceleration();
        Velocity v     = imu.getVelocity();
        Position p     = imu.getPosition();

        telemetry.addData("Orientation  (X,Y,Z)", o.firstAngle + ", " + o.secondAngle + ", " + o.thirdAngle);
        telemetry.addData("Acceleration (X,Y,Z)", a.xAccel + ", " + a.yAccel + ", " + a.zAccel);
        telemetry.addData("Position (X,Y,Z)", p.x + ", " + p.y + ", " + p.z);
        telemetry.addData("Cycle Time", cycleTime);
        telemetry.update();
    }
}
