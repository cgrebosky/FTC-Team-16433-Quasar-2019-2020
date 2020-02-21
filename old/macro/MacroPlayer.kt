package quasar.old.macro

import quasar.old.macro.MacroState.Companion.filename
import quasar.old.macro.MacroState.Companion.path
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import quasar.lib.GamepadState
import quasar.lib.MoreMath
import quasar.old.subsystems.Collector
import quasar.old.subsystems.Lift
import quasar.old.subsystems.Mecanum
import quasar.old.subsystems.PlatformMover
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

            while (System.currentTimeMillis() - t0 < recording[i].time) sleep(1)

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
        me.useCompBotConfig()

        pf.create(this)
        pf.init()

        co.create(this)
        co.init()

        li.create(this)
        li.init()
    }

    fun actState(m: MacroState) {
        //region Put all active subsystem actStates here
        me.bl.power = m.blPow
        me.br.power = m.brPow
        me.fl.power = m.flPow
        me.fr.power = m.frPow

        pf.left.position  = m.pfLeftPos
        pf.right.position = m.pfRightPos

        co.left.power = m.colLeftPow
        co.right.power = m.colRightPow
        co.limiterLeft.position = m.leftLim
        co.limiterRight.position = m.rightLim

        li.liftLeft.power = m.liftPow
        li.liftRight.power = m.liftPow
        li.extenderLeft.power = m.extenderPow
        li.extenderRight.power = m.extenderPow
        //li.grabber.power= m.grabberPos
        //endregion
    }
}