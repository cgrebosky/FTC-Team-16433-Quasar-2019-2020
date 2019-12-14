package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import quasar.lib.macro.MacroPlayer;
import quasar.subsystems.Collector;
import quasar.subsystems.Mecanum;

@Autonomous(name = "1 Block & Platform", group = "Autonomous")
public class QuasarAutonomous1BlockPlatform extends LinearOpMode {

    Mecanum m = new Mecanum();
    Collector c = new Collector();

    //This is a *really* shitty way to do things, but fuck it tbh
    class PlatformMoverRecording extends MacroPlayer {
        public void run() {
            initRobot();
            telePrint("RUNNING");

            int i = 0;
            long t0 = System.currentTimeMillis();
            while(opModeIsActive() && i < recording.size() - 3) {
                actState(recording.get(i));

                while (System.currentTimeMillis() - t0 < recording.get(i).getTime()) sleep(1);

                i++;
            }
        }
    }
    PlatformMoverRecording pfm = new PlatformMoverRecording();

    @Override
    public void runOpMode() {
        pfm.readData();
        pfm.hardwareMap = this.hardwareMap;
        pfm.telemetry = this.telemetry;

        m.create(this);
        m.init();
        m.useCompBotConfig();

        c.create(this);
        c.init();

        waitForStart();

        m.moveVectorTime(0,0.2,1000);
        m.zeroMotors();

        pfm.run();


    }
}
