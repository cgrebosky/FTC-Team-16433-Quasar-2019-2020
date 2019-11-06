package quasar.lib.macro

import quasar.lib.macro.MacroState.Companion.filename
import quasar.lib.macro.MacroState.Companion.path
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import quasar.subsystems.Mecanum
import quasar.subsystems.PlatformMover
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.util.*

@Autonomous(name = "Macro Player")
class MacroPlayer: LinearOpMode() {

    lateinit var recording: LinkedList<MacroState>

    //region variables
    val me = Mecanum()
    val pf = PlatformMover()
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
        telePrint("Initializing robot")

        //region INITIALIZE ROBOT
        me.create(this)
        me.init()
        me.useCompBotConfig()

        pf.create(this)
        pf.init()
        //endregion

        telePrint("Loading data")
        readData()

        telePrint("Ready")
    }

    fun actState(m: MacroState) {
        //region Put all active subsystem actStates here
        me.bl.power = m.blPow
        me.br.power = m.brPow
        me.fl.power = m.flPow
        me.fr.power = m.frPow

        pf.left.position  = m.pfLeftPos
        pf.right.position = m.pfRightPos
        //endregion
    }
}