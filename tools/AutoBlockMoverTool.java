package quasar.tools;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import quasar.subsystems.AutoBlockMover;

@TeleOp(name = "AutoBlockMover Tool", group = "Tools")
public final class AutoBlockMoverTool extends OpMode {
    AutoBlockMover ab = new AutoBlockMover();

    @Override
    public void init() {
        ab.create(this);
        ab.init();
    }

    @Override
    public void loop() {
        if(gamepad1.x) ab.lowerLeft(); else ab.raiseLeft();
        if(gamepad1.y) ab.closeLeft(); else ab.openLeft();
        if(gamepad1.a) ab.lowerRight(); else ab.raiseRight();
        if(gamepad1.b) ab.closeRight(); else ab.openRight();
    }
}
