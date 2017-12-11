package com.example.michael.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Michael Purcell on 12/9/2017.
 */

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private class ToDoBroadcastReceiver extends ViewBroadcastReceiver{

        @Override
        protected void gotSentItem(Intent intent) {

        }

        @Override
        protected void gotSentDeleteItem(Intent intent) {

        }

        @Override
        protected void getSentModifyItem(Intent intent) {

        }

        @Override
        protected void gotSentPriority(Intent intent) {

        }

        @Override
        protected void gotSentDescription(Intent intent) {

        }

        @Override
        protected void gotSentShortDescription(Intent intent) {

        }
    }
}

