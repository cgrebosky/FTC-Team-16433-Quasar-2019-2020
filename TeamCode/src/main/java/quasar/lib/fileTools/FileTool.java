package quasar.lib.fileTools;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

abstract public class FileTool {
    public String FILE_NAME;
    public static String PATH = Environment.getExternalStorageDirectory().getPath() + "/FIRST/";
    public File FILE = new File(PATH + FILE_NAME);

    public PrintWriter pw;
    public BufferedReader reader;

    public void createFile() {
        try {
            FILE.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void eraseFile() {
        try {
            new FileOutputStream(PATH + FILE_NAME).close();
        } catch (IOException e) {
            Trace.log(FILE_NAME + " erasure failed :(");

        }
    }

    public void start() {
        try {
            FileOutputStream fOut = new FileOutputStream(FILE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            pw = new PrintWriter(osw);

            osw.close();
            fOut.close();

            reader = new BufferedReader(new FileReader(FILE));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stop() {
        try {
            pw.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
