package quasar.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.lib.SubSystem;

@TeleOp(name = "HzTesting", group = "Testing")
public final class HzTest extends OpMode {

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
