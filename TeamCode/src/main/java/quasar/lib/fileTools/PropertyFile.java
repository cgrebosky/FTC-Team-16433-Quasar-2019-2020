package quasar.lib.fileTools;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystemLoopException;
import java.util.Hashtable;

public class PropertyFile extends FileTool {
    {
        FILE_NAME = "Quasar Property File.txt";
    }

    private static Hashtable<String, Object> table;

    private static BufferedReader reader;

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
