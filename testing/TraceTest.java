package quasar.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.lib.fileTools.Trace;

@TeleOp(name = "TraceTest", group = "UnitTests")
public class TraceTest extends OpMode {

    @Override
    public void init() {
        Trace.log("Hello World!");
    }

    @Override
    public void loop() {
    }
}
