package quasar.prod;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;

import quasar.subsystems.*;
import quasar.subsystems.threaded.IMUHandler;
import quasar.subsystems.threaded.VuforiaPositionDetector;

public final class Robot {
    public static Robot INSTANCE = new Robot();
    
    public Collector co;
    public Lift li;
    public Mecanum me;
    public PlatformMover pm;
    
    public IMUHandler imu;
    public VuforiaPositionDetector vpd;

    private LinearOpMode lop;
    private OpMode opm;
    private OpModeManagerImpl manager;


    public void create() {

    }
    public void init() {

        co.create(manager.getActiveOpMode());
        co.init();
    }
    public void loop() {
        
    }
}
