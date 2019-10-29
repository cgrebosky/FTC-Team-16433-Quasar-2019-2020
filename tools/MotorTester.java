package quasar.tools;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import quasar.lib.GamepadState;


@TeleOp(name = "Motor Tester", group = "Tools")
public class MotorTester extends OpMode {

    DcMotor motor;

    @Override
    public void init() {
        motor = hardwareMap.dcMotor.get("motor");
    }
    @Override
    public void loop() {
        telemetry.addData("Position", motor.getCurrentPosition());
        telemetry.addData("Direction", motor.getDirection());
        telemetry.addData("Mode", motor.getMode());
        telemetry.addData("Name", motor.getDeviceName());
        telemetry.addData("Power",motor.getPower());
        telemetry.addData("Target Position",motor.getTargetPosition());
        telemetry.addData("Zero Power Behaviour", motor.getZeroPowerBehavior());
        telemetry.update();
    }
    @Override
    public void stop() {
        motor.setPower(0);
    }
}
