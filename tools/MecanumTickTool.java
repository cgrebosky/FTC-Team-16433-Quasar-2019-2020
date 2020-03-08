package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.subsystems.Mecanum;

@TeleOp(name = "Mecanum Tick Tool", group = "Tools")
public final class MecanumTickTool extends OpMode {

    private Mecanum m = new Mecanum();

    @Override
    public void init() {
        m.create(this);
        m.init();

        telemetry.addLine("READY");
        telemetry.update();
    }

    @Override
    public void loop() {

        double fwd    = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn   = gamepad1.right_stick_x;

        fwd = Math.abs(fwd) > 0.15 ? fwd : 0;
        strafe = Math.abs(strafe) > 0.15 ? strafe : 0;
        turn = Math.abs(turn) > 0.15 ? turn : 0;

        m.setPowers(fwd, strafe, turn);

        telemetry.addData("(fl, fr, bl, br)", "("+m.fl.getCurrentPosition() + ", " +
                m.fr.getCurrentPosition() + ", " + m.bl.getCurrentPosition() + ", " + m.br.getCurrentPosition() + ")");
        telemetry.addData("Fwd", m.getFwdPos());
        telemetry.addData("Str", m.getStrPos());
        telemetry.update();
    }

    @Override
    public void stop() {
        m.stop();
    }
}
