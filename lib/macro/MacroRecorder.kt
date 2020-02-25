package quasar.lib.macro

import quasar.lib.macro.MacroState.Companion.filename
import quasar.lib.macro.MacroState.Companion.path
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import quasar.lib.GamepadState
import quasar.lib.MoreMath
import quasar.lib.macro.MacroState.Companion.currentMacroState
import quasar.subsystems.Collector
import quasar.subsystems.Lift
import quasar.subsystems.Mecanum
import quasar.subsystems.PlatformMover
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*

@TeleOp(name = "Macro Recorder")
class MacroRecorder: OpMode() {

    private enum class State {UNINITIALIZED, RUNNING, STOPPED}

    private var recording = LinkedList<MacroState>()

    private var state = State.UNINITIALIZED

    private val me = Mecanum()
    private val pf = PlatformMover()
    private val co = Collector()
    private val li = Lift()

    init {
        msStuckDetectInit = 50000
    }

    override fun init() {
        //region INIT CODE HERE
        me.create(this)
        me.init()

        pf.create(this)
        pf.init()

        co.create(this)
        co.init()

        li.create(this)
        li.init()
        //endregion

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

        telemetry.addLine("Ready")
        telemetry.update()
    }
    override fun loop() {
        //region LOOP CODE HERE
        me.loop()
        pf.loop()
        co.loop()
        li.loop()
        //endregion
        recordData()

        if(state == State.UNINITIALIZED && gamepad1.a) state = State.RUNNING
        if(state == State.RUNNING && gamepad1.x) state = State.STOPPED

        if(state == State.STOPPED) {
            serializeData()
            stop()
        }

        printTelemetry()
    }
    override fun stop() {
        me.stop()
        pf.stop()
        co.stop()
        li.stop()
    }
    private fun serializeData() {
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
    private fun printTelemetry() {
        telemetry.addLine("Press [A] to start, [X] to stop")
        telemetry.addData("Status", state)

        telemetry.update()
    }
    private fun recordData() {
        me.recordMacroState()
        pf.recordMacroState()
        co.recordMacroState()
        li.recordMacroState()

        if(state == State.RUNNING)
            recording.add(currentMacroState.clone(System.currentTimeMillis()))
    }
}