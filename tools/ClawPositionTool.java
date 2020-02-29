package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Claw Position Tool", group = "Tools")
@Disabled
public final class ClawPositionTool extends OpMode {

    private double clawPos, anglePos;
    private Servo claw, angle;

    @Override
    public void init() {
        claw = hardwareMap.servo.get("claw");
        angle = hardwareMap.servo.get("angle");

        clawPos = claw.getPosition();
        anglePos = angle.getPosition();
    }

    @Override
    public void loop() {
        clawPos = (-gamepad1.left_stick_y + 1) / 2;
        anglePos = (-gamepad1.right_stick_y + 1) / 2;
        claw.setPosition(clawPos);
        angle.setPosition(anglePos);

        telemetry.addData("Claw Position", clawPos);
        telemetry.addData("Angle", anglePos);
        telemetry.update();

    }
}
