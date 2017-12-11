package com.example.michael.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by Michael Purcell on 12/9/2017.
 *
 * Use by extending this class and set it up as a BroadcastReceiver.
 * Using it this way makes it easy to know what should be implemented
 * for the view.
 */

public abstract class ViewBroadcastReceiver extends BroadcastReceiver {
    private static String TAG = "ViewBroadcastReceiver";
    private IntentFilter intentFilter = new IntentFilter();

    public ViewBroadcastReceiver(){
        intentFilter.addAction(ControllerService.ACTION_SENT_ITEM);
        intentFilter.addAction(ControllerService.ACTION_SENT_DELETE_ITEM);
        intentFilter.addAction(ControllerService.ACTION_SENT_MODIFY_ITEM);
        intentFilter.addAction(ControllerService.ACTION_SENT_TITLE);
        intentFilter.addAction(ControllerService.ACTION_SENT_PRIORITY);
        intentFilter.addAction(ControllerService.ACTION_SENT_DESCRIPTION);
        intentFilter.addAction(ControllerService.ACTION_SENT_SHORT_DESCRIPTION);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.d(TAG, "ViewBroadcastReceiver, Action: " + action);

        switch (action){
            case ControllerService.ACTION_SENT_ITEM:
                gotSentItem(intent);
                break;
            case ControllerService.ACTION_SENT_DELETE_ITEM:
                gotSentDeleteItem(intent);
                break;
            case ControllerService.ACTION_SENT_MODIFY_ITEM:
                getSentModifyItem(intent);
                break;
            case ControllerService.ACTION_SENT_PRIORITY:
                gotSentPriority(intent);
                break;
            case ControllerService.ACTION_SENT_DESCRIPTION:
                gotSentDescription(intent);
                break;
            case ControllerService.ACTION_SENT_SHORT_DESCRIPTION:
                gotSentShortDescription(intent);
                break;
            default:
                Log.d(TAG, "Unhandled action: " + action);
        }
    }

    public IntentFilter getIntentFilter(){
        return this.intentFilter;
    }

    protected abstract void gotSentItem(Intent intent);
    protected abstract void gotSentDeleteItem(Intent intent);
    protected abstract void getSentModifyItem(Intent intent);
    protected abstract void gotSentPriority(Intent intent);
    protected abstract void gotSentDescription(Intent intent);
    protected abstract void gotSentShortDescription(Intent intent);

}
