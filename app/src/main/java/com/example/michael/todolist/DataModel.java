package com.example.michael.todolist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Michael Purcell on 12/1/2017.
 */

public class DataModel {

    private static final String TAG = "DataModel";
    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String SHORT_DESCRIPTION = "SHORT_DESCRIPTION";
    public static final String PRIORITY = "PRIORITY";

    private Context context;

    private final String IDS_FILE_NAME = "objectIDs";
    private final String DATA_FILE_NAME = "listInfoFile";
    private FileOutputStream fileOutputStream;
    private FileInputStream fileInputStream;
    private JSONObject listData;
    private ArrayList<String> idData;

    public DataModel(Context context){
        this.context = context;
        init();
    }


    private void init(){

        String listDataString = getFileData(DATA_FILE_NAME,"");
        String idDataString = getFileData(IDS_FILE_NAME,",");

        if(!listDataString.equals("")){
            try {
                listData = new JSONObject(listDataString);
            } catch (JSONException e) {
                Log.d(TAG,e.toString());
            }
        } else {
            listData = new JSONObject();
            Log.d(TAG,"No data for "+ DATA_FILE_NAME + " has been added yet.");
        }

        if(!idDataString.equals("")){
            idData = new ArrayList<>(Arrays.asList(idDataString.split(",")));
        } else {
            idData = new ArrayList<>();
            Log.d(TAG,"No data for "+ IDS_FILE_NAME + " has been added yet.");
        }
    }

    private String getFileData(String fileName, String seperator){
        try {

            fileInputStream = context.openFileInput(fileName);

            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
                stringBuilder.append(seperator);
            }

            return stringBuilder.toString();

        } catch (FileNotFoundException e) {
            Log.d(TAG,"File not found, creating file: " + fileName);
            File listInfoFile = new File(context.getFilesDir(),fileName);
            try {
                if(listInfoFile.createNewFile()){
                    return getFileData(fileName,seperator);
                }
            } catch (IOException e1) {
                Log.d(TAG,e1.toString());
            }
        } catch (IOException e) {
            Log.d(TAG, "IOException: "+e.toString());
        }

        return "";
    }

    public void addID(String id){

        if(!idData.contains(id)){
            idData.add(id);
            saveData();
        } else {
            //TODO tell controller to not allow duplicate title names.
        }

    }

    public void deleteID(String id){
        idData.remove(id);
        saveData();
    }

    public void addNewListItemData(String listTitle, JSONObject data){
        try {

            listData.put(listTitle,data);

            saveData();
        } catch (JSONException e) {
            Log.d(TAG,e.toString());
        }
    }

    public ArrayList<String> getIdData(){
        return idData;
    }

    public void updateListItemData(String itemTitle, JSONObject data){

        try {
            if(listData.has(itemTitle)) {
                listData.put(itemTitle, data);
            }
            else{
                Log.d(TAG,itemTitle + " does not exist");
            }


            saveData();
        } catch (JSONException e) {
            Log.d(TAG,e.toString());
        }

    }

    /*
     * very inefficient to save all of the data like this
     * however, the lists in this application shouldn't be too long.
     * Could also split all the data up into 3 different files for
     * each priority level.
     */
    private void saveData(){

        try {

            fileOutputStream = context.openFileOutput(DATA_FILE_NAME,Context.MODE_PRIVATE);
            fileOutputStream.write(listData.toString().getBytes());
            fileOutputStream.close();

            fileOutputStream = context.openFileOutput(IDS_FILE_NAME,Context.MODE_PRIVATE);

            for(String s : idData){
                String ss = s + "\n";
                fileOutputStream.write(ss.getBytes());
            }

            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            Log.d(TAG,e.toString());
        } catch (IOException e) {
            Log.d(TAG,e.toString());
        }
    }

    public void deleteListItemData(String itemTitle){

        listData.remove(itemTitle);

    }



//    public String getTitle(String itemTitle){
//        JSONObject temp = getListInfo(listIndex);
//        try {
//            String title = temp.getString(TITLE);
//            if(title != null){
//                return title;
//            }else {
//                return "";
//            }
//        } catch (JSONException e) {
//            Log.d(TAG,e.toString());
//            return "";
//        }
//    }

    public String getDescription(String itemTitle){
        JSONObject temp = getListInfo(itemTitle);
        try {
            String title = temp.getString(DESCRIPTION);
            if(title != null){
                return title;
            }else {
                return "";
            }
        } catch (JSONException e) {
            Log.d(TAG,e.toString());
            return "";
        }
    }

    public String getShortDescription(String itemTitle){
        JSONObject temp = getListInfo(itemTitle);
        try {
            String title = temp.getString(SHORT_DESCRIPTION);
            if(title != null){
                return title;
            }else {
                return "";
            }
        } catch (JSONException e) {
            Log.d(TAG,e.toString());
            return "";
        }
    }

    public String getPriority(String itemTitle){
        JSONObject temp = getListInfo(itemTitle);
        try {
            String title = temp.getString(PRIORITY);
            if(title != null){
                return title;
            }else {
                return "";
            }
        } catch (JSONException e) {
            Log.d(TAG,e.toString());
            return "";
        }
    }

    public JSONObject getListInfo(String itemTitle){

        try {
            return listData.getJSONObject(itemTitle);
        } catch (JSONException e) {
            Log.d(TAG, e.toString());
            return null;
        }

    }

}
