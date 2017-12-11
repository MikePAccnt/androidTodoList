package com.example.michael.todolist;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Michael Purcell on 12/1/2017.
 */

public class ControllerService extends Service {

    private static final String TAG = "ControllerService";

    public static final String ACTION_MODIFY_ITEM = "com.example.michael.todolist.ACTION_MODIFY_ITEM";
    public static final String ACTION_ADD_ITEM = "com.example.michael.todolist.ACTION_ADD_ITEM";
    public static final String ACTION_REFRESH = "com.example.michael.todolist.ACTION_REFRESH";
    public static final String ACTION_REMOVE_ITEM = "com.example.michael.todolist.ACTION_REMOVE_ITEM";

    public static final String ACTION_REQUEST_ALL_ITEMS = "com.example.michael.todolist.ACTION_REQUEST_ALL_ITEMS";
    public static final String ACTION_REQUEST_ITEM = "com.example.michael.todolist.ACTION_REQUEST_ITEM";
    public static final String ACTION_REQUEST_TITLE = "com.example.michael.todolist.ACTION_REQUEST_TITLE";
    public static final String ACTION_REQUEST_PRIORITY = "com.example.michael.todolist.ACTION_REQUEST_PRIORITY";
    public static final String ACTION_REQUEST_DESCRIPTION = "com.example.michael.todolist.ACTION_REQUEST_DESCRIPTION";
    public static final String ACTION_REQUEST_SHORT_DESCRIPTION = "com.example.michael.todolist.ACTION_REQUEST_SHORT_DESCRIPTION";

    public static final String ACTION_SENT_ITEM = "com.example.michael.todolist.ACTION_SENT_ITEM";
    public static final String ACTION_SENT_TITLE = "com.example.michael.todolist.ACTION_SENT_TITLE";
    public static final String ACTION_SENT_PRIORITY = "com.example.michael.todolist.ACTION_SENT_PRIORITY";
    public static final String ACTION_SENT_DESCRIPTION = "com.example.michael.todolist.ACTION_SENT_DESCRIPTION";
    public static final String ACTION_SENT_SHORT_DESCRIPTION = "com.example.michael.todolist.ACTION_SENT_SHORT_DESCRIPTION";
    public static final String ACTION_SENT_DELETE_ITEM = "com.example.michael.todolist.ACTION_SENT_DELETE_ITEM";
    public static final String ACTION_SENT_MODIFY_ITEM = "com.example.michael.todolist.ACTION_ACTION_SENT_MODIFY_ITEM";



    public static final String EXTRA_TITLE = "com.example.michael.todolist.EXTRA_TITLE";
    public static final String EXTRA_PRIORITY = "com.example.michael.todolist.EXTRA_PRIORITY";
    public static final String EXTRA_DESCRIPTION = "com.example.michael.todolist.EXTRA_DESCRIPTION";
    public static final String EXTRA_SHORT_DESCRIPTION = "com.example.michael.todolist.EXTRA_SHORT_DESCRIPTION";


    private DataModel dataModel;
    private ControllerServiceBroadcastReceiver controllerServiceBroadcastReceiver;
    private IntentFilter intentFilter;
    private boolean active = false;

    @Override
    public void onCreate() {
        super.onCreate();

        dataModel = new DataModel(getApplicationContext());

        intentFilter = new IntentFilter();

        intentFilter.addAction(ACTION_MODIFY_ITEM);
        intentFilter.addAction(ACTION_ADD_ITEM);
        intentFilter.addAction(ACTION_REFRESH);
        intentFilter.addAction(ACTION_REMOVE_ITEM);
        intentFilter.addAction(ACTION_REQUEST_ALL_ITEMS);
        intentFilter.addAction(ACTION_REQUEST_ITEM);
        intentFilter.addAction(ACTION_REQUEST_TITLE);
        intentFilter.addAction(ACTION_REQUEST_PRIORITY);
        intentFilter.addAction(ACTION_REQUEST_DESCRIPTION);
        intentFilter.addAction(ACTION_REQUEST_SHORT_DESCRIPTION);

        controllerServiceBroadcastReceiver = new ControllerServiceBroadcastReceiver();
        registerReceiver(controllerServiceBroadcastReceiver,intentFilter);

        active = true;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(controllerServiceBroadcastReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Intent modifyItem(Intent intent) {
        String title = intent.getStringExtra(EXTRA_TITLE);
        String short_desc = intent.getStringExtra(EXTRA_SHORT_DESCRIPTION);
        String desc = intent.getStringExtra(EXTRA_DESCRIPTION);
        String priority = intent.getStringExtra(EXTRA_PRIORITY);


        JSONObject temp = new JSONObject();

        try {
            temp.put(DataModel.SHORT_DESCRIPTION, short_desc);
            temp.put(DataModel.DESCRIPTION, desc);
            temp.put(DataModel.PRIORITY, priority);

            dataModel.updateListItemData(title,temp);

        } catch (JSONException e){
            Log.d(TAG, e.getMessage());
        }

        return   new Intent(ACTION_SENT_MODIFY_ITEM)
                .putExtra(EXTRA_TITLE,title)
                .putExtra(EXTRA_PRIORITY,priority)
                .putExtra(EXTRA_DESCRIPTION,desc)
                .putExtra(EXTRA_SHORT_DESCRIPTION,short_desc);

    }

    private Intent addItem(Intent intent){
        String title = intent.getStringExtra(EXTRA_TITLE);
        String short_desc = intent.getStringExtra(EXTRA_SHORT_DESCRIPTION);
        String desc = intent.getStringExtra(EXTRA_DESCRIPTION);
        String priority = intent.getStringExtra(EXTRA_PRIORITY);
        JSONObject temp = new JSONObject();

        try {
            temp.put(DataModel.SHORT_DESCRIPTION, short_desc);
            temp.put(DataModel.DESCRIPTION, desc);
            temp.put(DataModel.PRIORITY, priority);

            Log.d(TAG, "New Item Added: " + temp.toString());

            dataModel.addNewListItemData(title,temp);
            dataModel.addID(title);

        } catch (JSONException e){
            Log.d(TAG, e.getMessage());
        }

        return   new Intent(ACTION_SENT_ITEM)
                .putExtra(EXTRA_TITLE,title)
                .putExtra(EXTRA_PRIORITY,priority)
                .putExtra(EXTRA_DESCRIPTION,desc)
                .putExtra(EXTRA_SHORT_DESCRIPTION,short_desc);
    }


    private Intent removeItem(Intent intent) {
        String listTitle = intent.getStringExtra(EXTRA_TITLE);
        dataModel.deleteListItemData(listTitle);
        dataModel.deleteID(listTitle);

        return new Intent(ACTION_SENT_DELETE_ITEM).putExtra(EXTRA_TITLE,listTitle);
    }

    private Intent requestItem(String title){

        JSONObject temp = dataModel.getListInfo(title);
        String desc = "";
        String short_desc = "";
        String priority = "";

        try {
            if(temp.has(DataModel.DESCRIPTION))
                desc = temp.getString(DataModel.DESCRIPTION);
            if(temp.has(DataModel.SHORT_DESCRIPTION))
                short_desc = temp.getString(DataModel.SHORT_DESCRIPTION);
            if(temp.has(DataModel.PRIORITY))
                priority = temp.getString(DataModel.PRIORITY);
        } catch (JSONException e) {
            Log.d(TAG,e.getMessage());
        }

        return   new Intent(ACTION_SENT_ITEM)
                .putExtra(EXTRA_TITLE,title)
                .putExtra(EXTRA_PRIORITY,priority)
                .putExtra(EXTRA_DESCRIPTION,desc)
                .putExtra(EXTRA_SHORT_DESCRIPTION,short_desc);

    }

    private class ControllerServiceBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();
            String title;

            Log.d(TAG, "BroadcastReceiver, Action: " + action);

            switch (action){
                case ACTION_MODIFY_ITEM:
                    getApplicationContext().sendBroadcast(modifyItem(intent));
                    break;
                case ACTION_ADD_ITEM:
                    getApplicationContext().sendBroadcast(addItem(intent));
                    break;
                case ACTION_REMOVE_ITEM:
                    getApplicationContext().sendBroadcast(removeItem(intent));
                case ACTION_REFRESH:
                    break;
                case ACTION_REQUEST_ALL_ITEMS:
                    for(String t : dataModel.getIdData()){
                        getApplicationContext().sendBroadcast(requestItem(t));
                    }
                    break;
                case ACTION_REQUEST_ITEM:
                    title = intent.getStringExtra(EXTRA_TITLE);
                    getApplicationContext().sendBroadcast(requestItem(title));
                    break;
                case ACTION_REQUEST_PRIORITY:
                    title = intent.getStringExtra(EXTRA_TITLE);
                    getApplicationContext().sendBroadcast(new Intent(ACTION_SENT_PRIORITY).putExtra(EXTRA_PRIORITY, dataModel.getShortDescription(title)));
                    break;
                case ACTION_REQUEST_DESCRIPTION:
                    title = intent.getStringExtra(EXTRA_TITLE);
                    getApplicationContext().sendBroadcast(new Intent(ACTION_SENT_DESCRIPTION).putExtra(EXTRA_DESCRIPTION, dataModel.getShortDescription(title)));
                    break;
                case ACTION_REQUEST_SHORT_DESCRIPTION:
                    title = intent.getStringExtra(EXTRA_TITLE);
                    getApplicationContext().sendBroadcast(new Intent(ACTION_SENT_SHORT_DESCRIPTION).putExtra(EXTRA_SHORT_DESCRIPTION, dataModel.getShortDescription(title)));
                    break;

            }

        }
    }

}
