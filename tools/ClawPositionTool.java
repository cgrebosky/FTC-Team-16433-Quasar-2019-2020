package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Claw Position Tool", group = "Tools")
@Disabled
public class ClawPositionTool extends OpMode {

    double clawLeftPos, clawRightPos, anglePos;
    Servo clawLeft, clawRight, angle;

    @Override
    public void init() {
        clawLeft = hardwareMap.servo.get("clawLeft");
        clawRight = hardwareMap.servo.get("clawRight");
        angle = hardwareMap.servo.get("angle");
    }

    @Override
    public void loop() {
        if(gamepad1.a)         clawLeftPos += 0.1;
        if(gamepad1.b)         clawLeftPos -= 0.1;

        if(gamepad1.x)         clawRightPos += 0.1;
        if(gamepad1.y)         clawRightPos -= 0.1;

        if(gamepad1.dpad_up)   anglePos += 0.1;
        if(gamepad1.dpad_down) anglePos -= 0.1;

        telemetry.addData("Left Claw Position", clawLeftPos);
        telemetry.addData("Right Claw Position", clawRightPos);
        telemetry.addData("Angle", anglePos);
        telemetry.update();

    }
}
