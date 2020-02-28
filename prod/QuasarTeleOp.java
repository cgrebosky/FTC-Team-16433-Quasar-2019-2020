package quasar.prod;

import android.graphics.Paint;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.subsystems.CapstoneDepositor;
import quasar.subsystems.Collector;
import quasar.subsystems.Lift;
import quasar.subsystems.Mecanum;
import quasar.subsystems.PlatformMover;

@TeleOp(name = "Quasar TeleOp", group = "Prod")
public class QuasarTeleOp extends OpMode {

    @Override
    public void init() {
        Robot.create(this);
        Robot.init();
    }

    @Override
    public void loop() {
        Robot.loop();
    }

    @Override
    public void stop() {
        Robot.stop();
    }
}
