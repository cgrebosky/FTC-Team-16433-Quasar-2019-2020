package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.subsystems.Mecanum;

@TeleOp(name = "Mecanum Tick Tool", group = "Tools")
public final class MecanumTickTool extends OpMode {

    Mecanum m = new Mecanum();

    Mecanum.EncoderPosition startPos;
    Mecanum.EncoderPosition currentPos;

    @Override
    public void init() {
        m.create(this);
        m.init();

        startPos   = m.new EncoderPosition();
        currentPos = m.new EncoderPosition();

        telemetry.addLine("READY");
        telemetry.update();
    }

    @Override
    public void loop() {
        if(gamepad1.a) startPos = m.new EncoderPosition(); //This is some spicy notation, I didn't even know you could do this :D
        currentPos              = m.new EncoderPosition();

        double fwd    = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn   = gamepad1.right_stick_x;
        //This makes it so we can ONLY go forward or sideways at one time, we can't go diagonally
        if(Math.abs(fwd) > Math.abs(strafe)) strafe = 0;
        else fwd = 0;

        m.setPowers(fwd, strafe, turn);

        telemetry.addData("Start Position", startPos);
        telemetry.addData("Current Position", currentPos);
        telemetry.addData("FWD", currentPos.subtract(startPos).fwdTicks());
        telemetry.addData("STRAFE", currentPos.subtract(startPos).strafeTicks());
        telemetry.update();
    }

    @Override
    public void stop() {
        m.stop();
    }
}
