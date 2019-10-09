package quasar.lib.fileTools;

import android.os.Environment;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;

/**
 * Really basic Trace class to log data.
 */
public class Trace extends FileTool {
    private static Trace instance;

    public static boolean enableTrace = true;
    public static boolean robotLog    = true;

    public Trace() {
        start();

        instance = new Trace();
        FILE_NAME = "Quasar Trace File.txt";
    }

    public static void log(Object msg) {
        if(enableTrace) instance.pw.println(formatMessage(msg));
        if(robotLog)    instance.pw.println(formatMessage(msg));
    }

    @Contract(pure = true)
    private static String formatMessage(@NotNull Object msg) {
        return "Quasar: " + msg;
    }
}
