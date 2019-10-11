package c4.lib.macro

import c4.lib.macro.MacroState.Companion.filename
import c4.lib.macro.MacroState.Companion.path
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*

@TeleOp(name = "Macro Recorder")
class MacroRecorder: OpMode() {

    private enum class State {UNINITIALIZED, RUNNING, STOPPED}

    var recording = LinkedList<MacroState>()

    private var state = State.UNINITIALIZED;

    override fun init() {
        //INIT CODE HERE

        telemetry.addLine("Ready")
        telemetry.update()
    }
    override fun loop() {
        //LOOP CODE HERE

        if(state == State.UNINITIALIZED && gamepad1.a) state = State.RUNNING
        if(state == State.RUNNING && gamepad1.x) state = State.STOPPED

        if(state == State.RUNNING)
            recording.add( createCurrentState() )
        else if(state == State.STOPPED) {
            serializeData()
            stop()
        }

        printTelemetry()
    }
    fun serializeData() {
        //"reindex" the data so that time starts at 0.  This just makes it easier to deal with.
        val t0 = recording[0].time
        for(i in 0 until (recording.size - 1)) {
            recording[i].time -= t0
        }

        val f = File("$path/$filename")
        val fos = FileOutputStream(f)
        val oos = ObjectOutputStream(fos)

        oos.writeObject(recording)
    }
    fun printTelemetry() {
        telemetry.addLine("Press [A] to start, [X] to stop")
        telemetry.addData("Status", state)

        telemetry.update()
    }
    fun createCurrentState(): MacroState {
        val m = MacroState(System.currentTimeMillis())
        //RECORD STATE HERE

        return m
    }
}