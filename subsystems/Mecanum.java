package quasar.subsystems;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import quasar.lib.MoreMath;
import quasar.lib.macro.MacroState;
import quasar.lib.macro.MacroSystem;
import quasar.prod.Side;
import quasar.subsystems.threaded.IMUHandler;
import quasar.subsystems.threaded.VuforiaPositionDetector;

//TODO: Make Gyro-stabilized tele-op options?
public final class Mecanum extends SubSystem implements MacroSystem {

    public DcMotor fl, fr, bl, br;
    private double powFL, powFR, powBL, powBR;
    private double fwd, strafe, turn;

    private boolean slowMode = false;

    public static final double CTRL_THRESHOLD = 0.1;
    public static final double STRAFE_COEF = 1.27;

    //region SubSystem
    @Override
    public void init() {
        fl = hmap.dcMotor.get("fl");
        fr = hmap.dcMotor.get("fr");
        bl = hmap.dcMotor.get("bl");
        br = hmap.dcMotor.get("br");

        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        fr.setDirection(DcMotorSimple.Direction.FORWARD);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);
        br.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void loop() {
        control();
        setNumericalPowers();
        setMotorPowers();

        postLoop();
    }

    @Override
    public void stop() {
        fl.setPower(0);
        fr.setPower(0);
        bl.setPower(0);
        br.setPower(0);
    }

    @Override
    public void telemetry() {
        telemetry.addLine("MECANUM");
        telemetry.addData("    Slow Mode", slowMode);
        telemetry.addData("    Forward", fwd);
        telemetry.addData("    Strafe", strafe);
        telemetry.addData("    Rotation", turn);
        telemetry.addLine();
    }
    //endregion

    //region TeleOp
    private void control() {
        fwd    = -gamepad1.left_stick_y;
        strafe =  gamepad1.left_stick_x;
        turn   =  gamepad1.right_stick_x;

        thresholdControls();

        slowMode = gamepad1.left_stick_button || gamepad1.right_stick_button;
        if(slowMode) {
            fwd    *= 0.3;
            strafe *= 0.3;
            turn   *= 0.3;
        }
    }
    private void setNumericalPowers() {
        powFL = fwd + strafe + turn;
        powFR = fwd - strafe - turn;
        powBL = fwd - strafe + turn;
        powBR = fwd + strafe - turn;

        normalizePowers();
    }
    private void setMotorPowers() {
        fl.setPower(powFL);
        fr.setPower(powFR);
        bl.setPower(powBL);
        br.setPower(powBR);
    }
    private void normalizePowers() {
        //Having max compared to 1 allows us to have fine control, i.e., we're not always going at full speed
        double max = Math.max(1, Math.abs(powFL));
        max = Math.max(max, Math.abs(powFR));
        max = Math.max(max, Math.abs(powBL));
        max = Math.max(max, Math.abs(powBR));

        powFL /= max;
        powFR /= max;
        powBL /= max;
        powBR /= max;
    }
    private void thresholdControls() {
        fwd    = Math.abs(fwd)    > CTRL_THRESHOLD ? fwd : 0;
        strafe = Math.abs(strafe) > CTRL_THRESHOLD ? strafe : 0;
        turn   = Math.abs(turn)   > CTRL_THRESHOLD ? turn : 0;
    }
    //endregion

    public void setPowers(double fwd, double strafe, double turn) {
        this.fwd = fwd;
        this.strafe = strafe;
        this.turn = turn;

        setNumericalPowers();
        setMotorPowers();
    }

    private void turnTicks(int t) {
        int endTicks = bl.getCurrentPosition() + t;
        while(!MoreMath.isClose(bl.getCurrentPosition(), endTicks, 20) && lop.opModeIsActive()) {
            double pwr = (endTicks - bl.getCurrentPosition()) * 0.002;
            if(pwr > 0) pwr = MoreMath.clip(pwr, 0.5, 0.7);
            if(pwr < 0) pwr = MoreMath.clip(pwr, -0.5, -0.7);

            bl.setPower(pwr);
            fl.setPower(pwr);
            fr.setPower(-pwr);
            br.setPower(-pwr);
        }
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        setPowers(0,0,0);
    }
    //d>0 goes to the left.  This makes it consistent with IMU readings :D
    public void turnDegrees(double deg) {
        turnTicks((int) (deg * -11.7));
    }

    public int getFwdPos() {
        return (fl.getCurrentPosition() + fr.getCurrentPosition() + bl.getCurrentPosition() + br.getCurrentPosition()) / 4;
    }
    public int getStrPos() {
        return (fl.getCurrentPosition() - fr.getCurrentPosition() - bl.getCurrentPosition() + br.getCurrentPosition()) / 4;
    }

    //region Macro
    @Override
    public void recordMacroState() {
        MacroState.Companion.getCurrentMacroState().setFl(fl.getPower());
        MacroState.Companion.getCurrentMacroState().setFr(fr.getPower());
        MacroState.Companion.getCurrentMacroState().setBl(bl.getPower());
        MacroState.Companion.getCurrentMacroState().setBr(br.getPower());
    }
    @Override
    public void playMacroState(MacroState m) {
        fl.setPower(m.getFl());
        fr.setPower(m.getFr());
        bl.setPower(m.getBl());
        br.setPower(m.getBr());
    }
    //endregion
}
