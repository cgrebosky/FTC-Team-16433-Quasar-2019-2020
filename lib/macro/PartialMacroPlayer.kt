package quasar.lib.macro

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import quasar.subsystems.Collector
import quasar.subsystems.Lift
import quasar.subsystems.Mecanum
import quasar.subsystems.PlatformMover
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.util.*

class PartialMacroPlayer(
        val lop: LinearOpMode,
        private val fileName: String,
        private val me: Mecanum,
        private val co: Collector,
        private val li: Lift,
        private val pf: PlatformMover) {

    private var recording = LinkedList<MacroState>()

    fun init() {
        deserializeData()
    }
    fun playMacro() {
        var i = 0
        val t0 = System.currentTimeMillis()
        while(lop.opModeIsActive() && i < recording.size - 3) {
            actState(recording[i])

            while (System.currentTimeMillis() - t0 < recording[i].time) lop.idle()

            i++
        }
    }
    private fun deserializeData() {
        val f = File("${MacroState.path}/$fileName")
        val fis = FileInputStream(f)
        val ois = ObjectInputStream(fis)

        @SuppressWarnings("unchecked")
        recording = ois.readObject() as LinkedList<MacroState>
    }
    private fun actState(m: MacroState) {
        me.playMacroState(m)
        pf.playMacroState(m)
        co.playMacroState(m)
        li.playMacroState(m)
    }
}