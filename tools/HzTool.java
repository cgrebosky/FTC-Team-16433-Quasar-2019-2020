package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Hz Tool", group = "Testing")
@Disabled
public final class HzTool extends OpMode {

    private long prevTime = 0, cycleTime = 0, Hz = 0;

    @Override
    public void init() {

    }

    @Override
    public void loop() {
        cycleTime = System.currentTimeMillis() - prevTime;
        prevTime = System.currentTimeMillis();

        telemetry.addData("Cycle time", cycleTime);
        telemetry.addData("Hz", 1000 / (double) cycleTime);
        telemetry.update();
    }
}
