package c4.lib.macro

import c4.lib.macro.MacroState.Companion.filename
import c4.lib.macro.MacroState.Companion.path
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInput
import java.io.ObjectInputStream
import java.util.*

@Autonomous(name = "Macro Player")
class MacroPlayer: LinearOpMode() {

    lateinit var recording: LinkedList<MacroState>

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



        telePrint("Loading data")
        readData()

        telePrint("Ready")
    }

    fun actState(m: MacroState) {
        //Put all active subsystem actStates here
    }
}