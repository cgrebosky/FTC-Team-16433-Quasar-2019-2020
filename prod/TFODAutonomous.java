package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "TFOD Autonomous", group = "Prod")
public class TFODAutonomous extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Robot.create(this);
        Robot.autoInit();
        Robot.initTFOD();

        waitForStart();

        //Robot.leftToBlock();

        while(opModeIsActive()) {
            Robot.sayTFPosition();
            telemetry.update();
        }
    }
}
