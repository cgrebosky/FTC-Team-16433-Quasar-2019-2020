package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Collector Position Tool", group = "Tools")
@Disabled
public final class ServoCollectorPositionTool extends OpMode {

    Servo limitLeft, limitRight;

    @Override
    public void init() {
        limitLeft = hardwareMap.servo.get("limitLeft");
        limitRight = hardwareMap.servo.get("limitRight");
    }

    double leftPos = 0;
    double rightPos = 0;

    @Override
    public void loop() {

        if(gamepad1.a) leftPos += 0.01;
        else if(gamepad1.b) leftPos -= 0.01;

        if(gamepad1.x) rightPos += 0.01;
        else if(gamepad1.y) rightPos -= 0.01;

        limitLeft.setPosition(leftPos);
        limitRight.setPosition(rightPos);

        telemetry.addData("Left Position", leftPos);
        telemetry.addData("Right Position", rightPos);
        telemetry.update();
    }
}
