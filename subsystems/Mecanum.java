package quasar.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import quasar.lib.MoreMath;
import quasar.lib.SubSystem;

public class Mecanum extends SubSystem {

    private DcMotor fl, fr, bl, br;

    private final double THRESHOLD = 0.1;

    private double left, fwd, rot;

    //These are the coefficients for the motorpowers in order of fl, fr, bl, br (in order as defined above)
    private final double[] FWD    = {1,1,1,1};
    private final double[] STRAFE = {1,-1,-1,1};
    private final double[] TURN   = {1,-1,1,-1};

    private       double[] powers = {0, 0, 0,0};

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
    @Override @Tele public void loop() {

        zeroControls();
        calculatePowers(fwd, left, rot);
        normalizeMotorPowers();
        setMotorPowers();

        opm.telemetry.addData("FL", powers[0]);
        opm.telemetry.addData("FR", powers[1]);
        opm.telemetry.addData("BL", powers[2]);
        opm.telemetry.addData("BR", powers[3]);
        opm.telemetry.addLine();
        opm.telemetry.addData("Forward", fwd);
        opm.telemetry.addData("Left", left);
        opm.telemetry.addData("Rotation", rot);
        opm.telemetry.addLine();
    }
    @Override public void stop() {
        zeroMotors();
    }

    public void useTestBotConfig() {
        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        fr.setDirection(DcMotorSimple.Direction.FORWARD);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);
        br.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    public void useCompBotConfig() {
        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        fr.setDirection(DcMotorSimple.Direction.FORWARD);
        bl.setDirection(DcMotorSimple.Direction.FORWARD);
        br.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Tele private void calculatePowers(double forward, double strafe, double rotation) {
        //Why tf doesn't java have list stuff automatically???  Why do I have to make this stuff manually?? >:(
        powers = MoreMath.listMultiply(forward, FWD);
        powers = MoreMath.listAdd(powers, MoreMath.listMultiply(strafe, STRAFE));
        powers = MoreMath.listAdd(powers, MoreMath.listMultiply(rotation, TURN));
    }
    @Tele private void zeroControls() {
        left = gamepad1.left_stick_x;
        fwd = -gamepad1.left_stick_y;
        rot = gamepad1.right_stick_x;

        left = (Math.abs(left)<THRESHOLD)?0:left;
        fwd = (Math.abs(fwd)<THRESHOLD)?0:fwd;
        rot = (Math.abs(rot)<THRESHOLD)?0:rot;
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
    @Auto public void moveAngle(double deg) {
        double theta = Math.toDegrees(deg + (Math.PI / 2) );
        double x = Math.cos(theta);
        double y = Math.sin(theta);

        moveVector(x, y);
    }
    @Auto public void turnDegrees(double deg) {
        calculatePowers(0,0,Math.signum(deg));
        setMotorPowers();

        long endTime = System.currentTimeMillis() + 5000;
        while(System.currentTimeMillis() < endTime);

        calculatePowers(0d,0d,0d);
        setMotorPowers();

    }

    @Auto public void zeroMotors() {
        powers = new double[] {0d,0d,0d,0d};
        setMotorPowers();
    }
}
