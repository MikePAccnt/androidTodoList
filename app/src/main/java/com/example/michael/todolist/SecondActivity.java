package com.example.michael.todolist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Michael Purcell on 12/9/2017.
 */

public class SecondActivity extends AppCompatActivity {

    private static String TAG = "SecondActivity";

    private ListView listView;
    private ArrayListAdapter arrayListAdapter;
    private ArrayList<String> items;
    private ToDoBroadcastReceiver toDoBroadcastReceiver;
    private AlertDialog.Builder builder;
    private View newItemView;
    private View deleteItemView;
    private int selectedItem;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);
        listView = findViewById(R.id.listView);

        items = new ArrayList<>();
        arrayListAdapter = new ArrayListAdapter(this,R.layout.second_row_items,R.id.textView5,items);

        listView.setAdapter(arrayListAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = position;
                return false;
            }
        });


        registerForContextMenu(listView);
        toDoBroadcastReceiver = new ToDoBroadcastReceiver();
        registerReceiver(toDoBroadcastReceiver,toDoBroadcastReceiver.getIntentFilter());
        initNewItemDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"Sending request all items");
        getApplicationContext().sendBroadcast(new Intent(ControllerService.ACTION_REQUEST_ALL_ITEMS));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterForContextMenu(listView);
        unregisterReceiver(toDoBroadcastReceiver);
    }


        @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId()==R.id.listView){
            getMenuInflater().inflate(R.menu.item_options_menu,menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.deleteItem:
                broadcastRemoveItem();
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_second_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_todo_second:
                builder.show();
                return true;
            case R.id.changeViewSecond:
                finish();
                return true;
            default:
                return false;
        }
    }

    private void initNewItemDialog(){

        builder = new AlertDialog.Builder(SecondActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        newItemView = inflater.inflate(R.layout.new_item,null);
        final EditText editTitle = newItemView.findViewById(R.id.editTitle);;
        final Spinner editPriority = newItemView.findViewById(R.id.editPriority);
        final EditText editDesc = newItemView.findViewById(R.id.editDesc);
        final EditText editShortDesc = newItemView.findViewById(R.id.editShortDesc);

        builder.setView(newItemView);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    dialog.dismiss();
                    ((ViewGroup)newItemView.getParent()).removeView(newItemView);
                    return true;
                }
                return false;
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                broadcastAddItem(editTitle,editPriority,editDesc,editShortDesc);

                editTitle.setText("");
                //priority.setText("");
                editDesc.setText("");
                editShortDesc.setText("");
                dialog.dismiss();
                ((ViewGroup)newItemView.getParent()).removeView(newItemView);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                editTitle.setText("");
                //priority.setText("");
                editDesc.setText("");
                editShortDesc.setText("");
                dialog.dismiss();
                ((ViewGroup)newItemView.getParent()).removeView(newItemView);
            }
        });
    }

    private void broadcastAddItem(EditText editTitle,Spinner editPriority, EditText editDesc, EditText editShortDesc){
        getApplicationContext().sendBroadcast(new Intent(ControllerService.ACTION_ADD_ITEM)
                .putExtra(ControllerService.EXTRA_TITLE,editTitle.getText().toString().trim())
                .putExtra(ControllerService.EXTRA_PRIORITY,editPriority.getSelectedItem().toString().trim())
                .putExtra(ControllerService.EXTRA_DESCRIPTION,editDesc.getText().toString().trim())
                .putExtra(ControllerService.EXTRA_SHORT_DESCRIPTION,editShortDesc.getText().toString().trim()));
    }
    private void broadcastRemoveItem(){
        String title = arrayListAdapter.getItemTitle(selectedItem);
        Log.d(TAG,title);
        SecondActivity.this.sendBroadcast(new Intent(ControllerService.ACTION_REMOVE_ITEM).putExtra(ControllerService.EXTRA_TITLE,title.trim()));
    }



    private class ArrayListAdapter extends ArrayAdapter<String>{

        private ArrayList<String> items;

        public ArrayListAdapter(@NonNull Context context, int resource, int textViewId, ArrayList<String> items) {
            super(context, resource,textViewId,items);
            this.items = items;
        }

        @Override
        public void add(@Nullable String object) {
            super.add(object);
        }


        @Override
        public void remove(@Nullable String object) {
            super.remove(object);
        }

        @Override
        public void clear() {
            super.clear();
            notifyDataSetChanged();
        }

        //Not sure if this works but this will never get called since this view has no modify option
        //on an item.
        public void modifyItem(String title, String priority, String description, String shortDescription){
            for(String s: items){
                if(s.contains("Title: " + title)){
                    int pos = items.indexOf(s);
                    s = s.replace("Priority: [(Low|Medium|High)]+","Priority: " + priority);
                    s = s.replace("(?:Short Description: )[\\w\\W]+(?=Description)", "Short Description: " + shortDescription);
                    s = Pattern.compile("^Description: [\\w\\W]+",Pattern.DOTALL).matcher(s).replaceFirst("Description: " + description);
                    items.remove(pos);
                    items.add(pos,s);
                    addAll(items);
                    notifyDataSetChanged();
                    break;
                }
            }
        }

        public String getItem(int position){
            return items.get(position);
        }

        public String getItemTitle(int position){
            String item = items.get(position);
            String s [] = item.split("(?:Title: )[\\w\\W]+(?=Priority)");

            for(String ss : s){
                item = item.replace(ss,"");
            }

            item = item.replace("Title: ","");
            item = item.trim();

            return item;
        }

    }

    private class ToDoBroadcastReceiver extends ViewBroadcastReceiver{

        @Override
        protected void gotSentItem(Intent intent) {
            Log.d(TAG,"goSentItem");
            String title = "Title: " + intent.getStringExtra(ControllerService.EXTRA_TITLE);
            String priority = "Priority: " + intent.getStringExtra(ControllerService.EXTRA_PRIORITY);
            String desc = "Description: " + intent.getStringExtra(ControllerService.EXTRA_DESCRIPTION);
            String short_desc = "Short Description: " + intent.getStringExtra(ControllerService.EXTRA_SHORT_DESCRIPTION);

            arrayListAdapter.add(title + "\n" + priority + "\n" + short_desc + "\n" + desc + "\n");
            arrayListAdapter.notifyDataSetChanged();

        }

        @Override
        protected void gotSentDeleteItem(Intent intent) {
            Log.d(TAG,"goSentDeleteItem");
            String title = "Title: " + intent.getStringExtra(ControllerService.EXTRA_TITLE);

            arrayListAdapter.remove(title);
            arrayListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void getSentModifyItem(Intent intent) {
            Log.d(TAG,"goSentModifyItem");
            String title = "Title: " + intent.getStringExtra(ControllerService.EXTRA_TITLE);
            String priority = "Priority: " + intent.getStringExtra(ControllerService.EXTRA_PRIORITY);
            String desc = "Description: " + intent.getStringExtra(ControllerService.EXTRA_DESCRIPTION);
            String short_desc = "Short Description: " + intent.getStringExtra(ControllerService.EXTRA_SHORT_DESCRIPTION);

            arrayListAdapter.modifyItem(title, priority, desc, short_desc);

            arrayListAdapter.notifyDataSetChanged();
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

