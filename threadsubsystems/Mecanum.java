package quasar.threadsubsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import quasar.lib.GamepadState;
import quasar.lib.MoreMath;
import quasar.old.SubSystem.Auto;
import quasar.lib.ThreadSubSystem;
import quasar.lib.macro.MacroSystem;

public final class Mecanum extends ThreadSubSystem implements MacroSystem {

    private class EncoderPosition {
        int fl, fr, bl, br;

        public EncoderPosition(int fl, int fr, int bl, int br) {
            this.fl = fl;
            this.fr = fr;
            this.bl = bl;
            this.br = br;
        }
        public EncoderPosition() {
            this.fl = Mecanum.this.fl.getCurrentPosition();
            this.fr = Mecanum.this.fr.getCurrentPosition();
            this.bl = Mecanum.this.bl.getCurrentPosition();
            this.br = Mecanum.this.br.getCurrentPosition();
        }

        public int getFwdTicks() {
            return (fl + fr + bl + br) / 4;
        }
        public int getStrafeTicks() {
            return (fl - fr - bl + br) / 4;
        }

        public EncoderPosition subtract(EncoderPosition b) {
            return new EncoderPosition(this.fl - b.fl, this.fr - b.fr, this.bl - b.bl, this.br - b.br);
        }
    }

    private DcMotor fl, fr, bl, br;
    private double powFL, powFR, powBL, powBR;
    private double fwd, strafe, turn;

    private boolean slowMode = false;

    private final double PWR_THRESHOLD = 0.08;

    //region SubSystem
    @Override
    protected void _init() {
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

        telemetry.addLine("Mecanum Ready");
    }

    @Override
    protected void _loop() {
        control();
        setNumericalPowers();
        setMotorPowers();
    }

    @Override
    protected void _stop() {
        fl.setPower(0);
        fr.setPower(0);
        bl.setPower(0);
        br.setPower(0);
    }

    @Override
    protected void _telemetry() {
        telemetry.addLine("MECANUM");
        telemetry.addData("    Slow Mode", slowMode);
        telemetry.addData("    Forward", fwd);
        telemetry.addData("    Strafe", strafe);
        telemetry.addData("    Rotation", turn);
    }
    //endregion

    //region TeleOp
    private void control() {
        fwd    = -gamepad1.left_stick_y;
        strafe =  gamepad1.left_stick_x;
        turn   =  gamepad1.right_stick_x;

        slowMode = GamepadState.toggle(gamepad1.left_stick_button || gamepad1.right_stick_button, prev1.left_stick_button || prev1.right_stick_button, slowMode);
        if(slowMode) {
            fwd    *= 0.4;
            strafe *= 0.4;
            turn   *= 0.4;
        }
    }
    private void setNumericalPowers() {
        powFL = fwd + strafe + turn;
        powFR = fwd - strafe - turn;
        powBL = fwd - strafe + turn;
        powBR = fwd + strafe - turn;

        normalizePowers();
        thresholdPowers();
    }
    private void setMotorPowers() {
        fl.setPower(powFL);
        fr.setPower(powFR);
        bl.setPower(powBL);
        br.setPower(powBR);
    }
    private void normalizePowers() {
        //Having max compared to 1 allows us to have fine control, i.e., we're not always going at full speed
        double max = Math.max(1, powFL);
        max = Math.max(max, powFR);
        max = Math.max(max, powBL);
        max = Math.max(max, powBR);

        powFL /= max;
        powFR /= max;
        powBL /= max;
        powBR /= max;
    }
    private void thresholdPowers() {
        powBL = Math.abs(powBL) > PWR_THRESHOLD ? powBL : 0;
        powBR = Math.abs(powBR) > PWR_THRESHOLD ? powBR : 0;
        powFL = Math.abs(powFL) > PWR_THRESHOLD ? powFL : 0;
        powFR = Math.abs(powFR) > PWR_THRESHOLD ? powFR : 0;
    }
    //endregion

    public synchronized void setPowers(double fwd, double strafe, double turn) {
        this.fwd = fwd;
        this.strafe = strafe;
        this.turn = turn;

        setNumericalPowers();
        setMotorPowers();
    }

    @Auto public void goToPositionVuforia(double endX, double endY, IMUHandler i, PositionDetector pd) {
        double startAngle = i.getAbsoluteHeading();

        while(lop.opModeIsActive() && pd.imageIsVisible()) {
            double angle = i.getAbsoluteHeading();
            double diff = startAngle - angle;
            double turn = MoreMath.clip(-diff / 45, -0.6, 0.6);

            double x = pd.getX();
            double y = pd.getY();
            double dx = endX - x;
            double dy = endY - y;

            double dmax = Math.max( Math.abs(dy), Math.abs(dx) );
            double xPow = MoreMath.clip( dx / dmax, -0.3, 0.3);
            double yPow = MoreMath.clip( dy / dmax, -0.3, 0.3);

            setPowers(-xPow, yPow, turn);
        }

        setPowers(0,0,0);

    }
    @Auto public void gyroStableMoveTicks(double fwd, double strafe, double absoluteAngle, IMUHandler i) {
        EncoderPosition startPos   = new EncoderPosition();
        EncoderPosition currentPos = new EncoderPosition();
        setPowers(fwd, strafe, 0);


    }
    @Auto public void turnToGlobalDegrees() {

    }


    //region Macro
    @Override
    public void setMacroState() {

    }

    @Override
    public void getMacroState() {

    }
    //endregion
}
