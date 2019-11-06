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
    var flPow = 0.0
    var frPow = 0.0
    var blPow = 0.0
    var brPow = 0.0
    //PlatformMover
    var pfLeftPos = 0.0
    var pfRightPos = 0.0
    //endregion

    companion object {
        val potentialFileNames = arrayOf("MacroRecording.txt",
                "PlatformMoverAuto.txt",
                "BlockMoveAuto.txt"
        )
        var filename = "MacroRecording.txt"
        val path = "/${Environment.getExternalStorageDirectory().path}/FIRST/"
    }
}