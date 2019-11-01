package quasar.prod.macro

import android.os.Environment
import java.io.Serializable

/**
 * A class to store a single instant in time for all the motor powers or positions on the robot.
 * When you add a new feature to this class, you must also edit MacroRecorder#createCurrentState
 * and MacroPlayer#actState, as well as any new initializations or looping code.
 */
data class MacroState(var time: Long): Serializable {

    //PUT MACRO STATE DATA HERE
    public var fl = 0.0
    public var fr = 0.0
    public var bl = 0.0
    public var br = 0.0

    public var leftPlatformPos = 0.0
    public var rightPlatformPos = 0.0

    companion object {
        val filename = "MacroRecording.txt"
        val path = "/${Environment.getExternalStorageDirectory().path}/FIRST/"
    }
}