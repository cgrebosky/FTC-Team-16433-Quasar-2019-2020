package quasar.lib.macro

import quasar.lib.macro.MacroState.Companion.filename
import quasar.lib.macro.MacroState.Companion.path
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import quasar.lib.GamepadState
import quasar.lib.MoreMath
import quasar.subsystems.Collector
import quasar.subsystems.Lift
import quasar.subsystems.Mecanum
import quasar.subsystems.PlatformMover
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.util.*

@Autonomous(name = "Macro Player")
open class MacroPlayer: LinearOpMode() {

    lateinit var recording: LinkedList<MacroState>

    //region variables
    val me = Mecanum()
    val pf = PlatformMover()
    val co = Collector()
    val li = Lift()
    //endregion

    override fun runOpMode() {
        initialize()

        waitForStart()
        telePrint("RUNNING")

        var i = 0
        val t0 = System.currentTimeMillis()
        while(opModeIsActive() && i < recording.size - 3) {
            actState(recording[i])

            while (System.currentTimeMillis() - t0 < recording[i].time) idle()

            i++
        }
    }
    fun readData() {
        val f = File("$path/$filename")
        val fis = FileInputStream(f)
        val ois = ObjectInputStream(fis)

        @SuppressWarnings("unchecked")
        recording = ois.readObject() as LinkedList<MacroState>
    }
    fun telePrint(msg: String) {
        telemetry.addLine(msg)
        telemetry.update()
    }
    fun initialize() {

        var prevUp = gamepad1.dpad_up
        var prevDown = gamepad1.dpad_down
        val len = MacroState.potentialFileNames.size
        var index = 0
        var currentOption = MacroState.potentialFileNames[index]
        while(!gamepad1.a) {

            if(GamepadState.press(gamepad1.dpad_up, prevUp)) index = MoreMath.tapeInc(0, len-1, index)
            if(GamepadState.press(gamepad1.dpad_down, prevDown)) index = MoreMath.tapeDec(0,len-1,index)

            prevUp = gamepad1.dpad_up
            prevDown = gamepad1.dpad_down

            currentOption = MacroState.potentialFileNames[index]
            telemetry.addLine("Current file: $currentOption")
            telemetry.addLine("Press DPAD_UP or DPAD_DOWN to change file")
            telemetry.addLine("Index: $index")
            telemetry.update()
        }
        filename = currentOption

        telePrint("Initializing robot")

        initRobot()

        telePrint("Loading data")
        readData()

        telePrint("Ready")
    }
    fun initRobot() {
        me.create(this)
        me.init()

        pf.create(this)
        pf.init()

        co.create(this)
        co.init()

        li.create(this)
        li.init()
    }

    fun actState(m: MacroState) {
        me.playMacroState(m)
        pf.playMacroState(m)
        co.playMacroState(m)
        li.playMacroState(m)
    }
}