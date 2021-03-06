package com.tracker.covid_19tracker.files;

import android.app.Activity;
import android.util.Log;
import com.tracker.covid_19tracker.MainActivity;
import org.json.JSONObject;

import java.io.*;

public abstract class AbstractFile {

    protected String name;
    protected File dir;
    protected File file;
    protected FileInputStream inputStream;
    protected FileOutputStream outputStream;
    protected JSONObject data;

    protected MainActivity mainActivity;
    protected FileManager fileManager;

    protected AbstractFile(String name, File dir, FileManager fileManager) {
        this.name = name;
        this.dir = dir;
        this.file = new File(dir, name);
        this.mainActivity = fileManager.getMainActivity();
        this.fileManager = fileManager;
        this.data = new JSONObject();
        fileManager.registerFile(this);

        if (file.getParentFile() != null && !file.getParentFile().exists()){
            if (!file.getParentFile().mkdirs()){
                Log.e("File IO", String.format("Failed to create directory for file: %s. Closing...", name));
                mainActivity.exit();
                return;
            }
        }

        if (!file.exists()){
            try {
                onCreate(file.createNewFile());
            } catch (IOException e){
                e.printStackTrace();
                Log.e("File IO", String.format("Failed to create directory for file: %s. Closing...", name));
                mainActivity.exit();
                return;
            }
        }

        try {
            this.inputStream = mainActivity.getApplicationContext().openFileInput(name);
            this.outputStream = mainActivity.getApplicationContext().openFileOutput(name, Activity.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onCreate(boolean successful){}

    public abstract boolean load();

    public boolean save() {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.append(data.toString());
            Log.d("Debugging", "!!!" + data.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }
}
