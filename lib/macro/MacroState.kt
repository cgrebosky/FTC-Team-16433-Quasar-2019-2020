package quasar.lib.macro

import android.os.Environment
import java.io.Serializable

/**
 * A class to store a single instant in time for all the motor powers or positions on the robot.
 * When you add a new feature to this class, you must also edit MacroRecorder#createCurrentState
 * and MacroPlayer#actState, as well as any new initializations or looping code.
 */
data class MacroState(var time: Long): Serializable {

    //region PUT MACRO STATE DATA HERE
    //Mecanum
    var fl = 0.0
    var fr = 0.0
    var bl = 0.0
    var br = 0.0
    //PlatformMover
    var pfLeftPos = 0.0
    var pfRightPos = 0.0
    //Collector
    var colLeftPow = 0.0
    var colRightPow = 0.0
    var colLimiterL = 0.0
    var colLimiterR = 0.0
    //Lift
    var liftPow = 0.0
    var extenderPow = 0.0
    var grabberPos = 0.0
    var anglePos = 0.0
    //endregion

    fun clone(t: Long): MacroState {
        val m = MacroState(t)
        m.fl = this.fl
        m.fr = this.fr
        m.bl = this.bl
        m.br = this.br

        m.pfLeftPos = this.pfLeftPos
        m.pfRightPos = this.pfRightPos

        m.colLeftPow = this.colLeftPow
        m.colRightPow = this.colRightPow
        m.colLimiterL = this.colLimiterL
        m.colLimiterR = this.colLimiterR

        m.liftPow = this.liftPow
        m.extenderPow = this.extenderPow
        m.grabberPos = this.grabberPos
        m.anglePos = this.anglePos

        return m
    }

    companion object {
        val currentMacroState = MacroState(System.currentTimeMillis())

        val potentialFileNames = arrayOf("MacroRecording (DEBUG - DO NOT USE)",
                "RED Platform Mover",
                "BLUE Platform Mover",
                "RED Block Mover",
                "BLUE Block Mover",
                "AUTO Platform Mover"

        )
        var filename = "MacroRecording"
        val path = "/${Environment.getExternalStorageDirectory().path}/FIRST/macro/"
    }
}