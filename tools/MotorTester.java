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


@TeleOp(name = "Motor Tester", group = "Testing")
public class MotorTester extends OpMode {

    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/FIRST/";
    private static final String FILE_NAME = "driveWheelers.xml";
    private static final File FILE = new File(PATH + FILE_NAME);

    private int index;
    private DcMotor currentMotor;
    private ArrayList<String> motorNames;
    private ArrayList<DcMotor> motorList;
    private Document doc;


    @Override
    public void init() {



        loadDocument();

        motorNames = getMotorNames(doc);
        motorList = getMotorList(motorNames);
    }


    GamepadState prev = new GamepadState();

    @Override
    public void loop() {

        if(gamepad1.a && !prev.a) index = cycleUp(index, motorNames.size());
        if(gamepad1.b && !prev.b) index = cycleDown(index, motorNames.size());

        currentMotor = motorList.get(index);



        telemetry.addData("Current Motor", motorNames.get(index));
        telemetry.addData("Index", index);
        telemetry.update();
        prev = new GamepadState(gamepad1);
    }

    private int cycleUp(int index, int size) {
        if(index >= size) return 0;
        return index + 1;
    }
    private int cycleDown(int index, int size) {
        if(index == 0) return size-1;
        return index -1;
    }

    private ArrayList<String> getMotorNames(@NotNull Document doc) {
        doc.getDocumentElement().normalize();

        NodeList elemList           = doc.getElementsByTagName("*");
        ArrayList<String> motorNames = new ArrayList<>();

        //This is unfortunate that I have to use a fori loop, but it's the only way I can think of.
        for (int i = 0; i < elemList.getLength(); i++) {
            Node elem = elemList.item(i);
            if(elem.getNodeName().toLowerCase().contains("motor")) {
                motorNames.add(elem.getAttributes().getNamedItem("name").getNodeValue());
            }
        }

        return motorNames;
    }
    private ArrayList<DcMotor> getMotorList(@NotNull ArrayList<String> names) {
        ArrayList<DcMotor> motors = new ArrayList<>();
        for (String i : names) {
            DcMotor motor = hardwareMap.dcMotor.get(i);
            motors.add(motor);
        }
        return motors;
    }

    private void loadDocument() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(FILE);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
