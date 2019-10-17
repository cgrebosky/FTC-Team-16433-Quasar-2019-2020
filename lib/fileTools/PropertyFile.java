package quasar.lib.fileTools;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.FileSystemLoopException;
import java.util.Hashtable;

public class PropertyFile {
    public String FILE_NAME = "Quasar Property File.txt";
    public static String PATH = Environment.getExternalStorageDirectory().getPath() + "/FIRST/";
    public File FILE = new File(PATH + FILE_NAME);

    public PrintWriter pw;
    public BufferedReader reader;

    private static Hashtable<String, Object> table;

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
            e.printStackTrace();
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

    private void getData() {
        try {
            FileReader fr = new FileReader(FILE);
            reader = new BufferedReader(fr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(line != null) {
            String label = line.split(":")[0];
            String data  = line.split(":")[1];

            //We assume double.  Otherwise it's string.  This works for nearly all cases
            try {
                table.put(label, Double.parseDouble(data));
            } catch (NumberFormatException e) {
                table.put(label, data);
            }

            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
