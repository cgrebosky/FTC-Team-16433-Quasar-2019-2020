package quasar.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import quasar.lib.MoreMath;
import quasar.lib.SubSystem;

public class Mecanum extends SubSystem {

    DcMotor fl, fr, bl, br;

    private double THRESHOLD = 0.1;

    //These are the coefficients for the motorpowers in order of fl, fr, bl, br (in order as defined above)
    private final double[] FWD       = {1,-1,-1,1};
    private final double[] LEFT      = {-1,-1,-1,-1};
    private final double[] CLOCKWISE = {-1,1,-1,1};
    private       double[] powers    = {0, 0, 0,0};

    @Override
    public void init() {
        fl = hardwareMap.dcMotor.get("fl");
        fr = hardwareMap.dcMotor.get("fr");
        bl = hardwareMap.dcMotor.get("bl");
        br = hardwareMap.dcMotor.get("br");

        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }
    @Override
    public void loop() {
        calculatePowers();
        normalizeMotorPowers();
        zeroPowers();
        setMotorPowers();

        opm.telemetry.addLine(powers[0] + ", " + powers[1] + ", " + powers[2] + ", " + powers[3]);
        opm.telemetry.addLine("" + gamepad1.left_stick_y);
    }

    private void calculatePowers() {
        //Why tf doesn't java have list stuff automatically???  Why do I have to make this stuff manually?? >:(
        powers = MoreMath.listMultiply(gamepad1.left_stick_y, FWD);
        powers = MoreMath.listAdd(powers, MoreMath.listMultiply(gamepad1.left_stick_x, LEFT));
        powers = MoreMath.listAdd(powers, MoreMath.listMultiply(gamepad1.right_stick_x, CLOCKWISE));
    }
    private void zeroPowers() {
        for (int i = 0; i < 4; i++) {
            if(Math.abs(powers[i]) < THRESHOLD) powers[i] = 0;
        }
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
            if(Math.abs(i) > max) max = i;
        }

        powers = MoreMath.listMultiply(1/max, powers);

    }
}
