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
 *
 * Implementation of a second view to show that it is
 * a MVC type design.
 *
 * Note:
 * Modifying doesn't correctly update the second
 * view if done inside of it. However it does correctly
 * notify the controller to update the data.
 */

public class SecondActivity extends AppCompatActivity {

    private static String TAG = "SecondActivity";

    private ListView listView;
    private ArrayListAdapter arrayListAdapter;
    private ArrayList<String> items = new ArrayList<>();
    private ToDoBroadcastReceiver toDoBroadcastReceiver;
    private AlertDialog.Builder builder;
    private View newItemView;
    private View modifyItemView;
    private int selectedItem;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);
        listView = findViewById(R.id.listView);


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
                return true;
            case R.id.modifyItem:
                modifyDialog();
                return true;
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

    private void modifyDialog(){
        final AlertDialog.Builder modifyDialog = new AlertDialog.Builder(SecondActivity.this);
        if(modifyItemView == null) {
            LayoutInflater layoutInflater = getLayoutInflater();
            modifyItemView = layoutInflater.inflate(R.layout.modify_item, null);
        }
//        TextView description = modifyItemView.findViewById(R.id.modDesc);
//        TextView shortDescription = modifyItemView.findViewById(R.id.modShortDesc);
//        Spinner priority = modifyItemView.findViewById(R.id.modPriority);
//
//        String item = items.get(selectedItem);

        //Doesn't work, Implement this to start the modify dialog with current values for the selected item.

//        description.setText(Pattern.compile("^Description: [\\w\\W]+",Pattern.DOTALL).matcher(item).replaceAll(""));
//        shortDescription.setText(Pattern.compile("(?!Short Description: )[\\w\\W]+(?=Description)").matcher(item).replaceAll(""));
//
//        switch (Pattern.compile("Priority: [(Low|Medium|High)]+").matcher(item).toString()){
//            case "Low":
//                priority.setSelection(0);
//                break;
//            case "Medium":
//                priority.setSelection(1);
//                break;
//            case "High":
//                priority.setSelection(2);
//                break;
//        }

        modifyDialog.setView(modifyItemView);
        modifyDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    dialog.dismiss();
                    ((ViewGroup) modifyItemView.getParent()).removeView(modifyItemView);
                    return true;
                }
                return false;
            }
        });
        modifyDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = arrayListAdapter.getItemTitle(selectedItem);
                Spinner priority = modifyItemView.findViewById(R.id.modPriority);
                EditText desc = modifyItemView.findViewById(R.id.modDesc);
                EditText shortDesc = modifyItemView.findViewById(R.id.modShortDesc);
                broadcastModifyItem(title,priority.getSelectedItem().toString(),desc.getText().toString(),shortDesc.getText().toString());

                ((ViewGroup) modifyItemView.getParent()).removeView(modifyItemView);
            }
        });
        modifyDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((ViewGroup) modifyItemView.getParent()).removeView(modifyItemView);
            }
        });
        modifyDialog.show();
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

    private void broadcastModifyItem(String title, String priority,String description, String shortDescription){

        SecondActivity.this.sendBroadcast(new Intent(ControllerService.ACTION_MODIFY_ITEM)
                .putExtra(ControllerService.EXTRA_TITLE,title.trim())
                .putExtra(ControllerService.EXTRA_PRIORITY,priority.trim())
                .putExtra(ControllerService.EXTRA_DESCRIPTION,description.trim())
                .putExtra(ControllerService.EXTRA_SHORT_DESCRIPTION,shortDescription.trim()));
    }


    private class ArrayListAdapter extends ArrayAdapter<String>{

        private ArrayList<String> items;

        public ArrayListAdapter(@NonNull Context context, int resource, int textViewId, ArrayList<String> items) {
            super(context, resource,textViewId,items);
            this.items = items;
        }

        @Override
        public void clear() {
            super.clear();
            notifyDataSetChanged();
        }

        public String getItem(int position){
            return items.get(position);
        }

        public String getItemByTitle(String title){

            for(String item : items){
                if(item.contains("Title: "+ title)){
                    return item;
                }
            }
            return "";
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

            //In order to effect the data stored in the adapter need to change original data set
            //and notify the adapter. CANNOT change the data in the adapter by extra functions
            //written and then notify from within the adapter. It will not work that way.
            items.add(title + "\n" + priority + "\n" + short_desc + "\n" + desc + "\n");
            arrayListAdapter.notifyDataSetChanged();

        }

        @Override
        protected void gotSentDeleteItem(Intent intent) {
            Log.d(TAG,"goSentDeleteItem");

            //In order to effect the data stored in the adapter need to change original data set
            //and notify the adapter. CANNOT change the data in the adapter by extra functions
            //written and then notify from within the adapter. It will not work that way.
            items.remove(arrayListAdapter.getItemByTitle(intent.getStringExtra(ControllerService.EXTRA_TITLE)));
            arrayListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void getSentModifyItem(Intent intent) {
            Log.d(TAG,"goSentModifyItem");
            String title =  intent.getStringExtra(ControllerService.EXTRA_TITLE).trim();
            String priority =  intent.getStringExtra(ControllerService.EXTRA_PRIORITY).trim();
            String desc =  intent.getStringExtra(ControllerService.EXTRA_DESCRIPTION).trim();
            String short_desc = intent.getStringExtra(ControllerService.EXTRA_SHORT_DESCRIPTION).trim();

            //For some reason this does not update the view and cannot find out why since it
            //works for adding and deleting items from the list. In the two methods above.
            modifyItem(title, priority, desc, short_desc);
            arrayListAdapter.notifyDataSetChanged();

        }

        private void modifyItem(String title, String priority, String description, String shortDescription){

            for(String s : items){
                if(s.contains("Title: "+ title)){
                    Log.d(TAG,"Contains!");
                    int pos = items.indexOf(s);
                    s = s.replace("Priority: [(Low|Medium|High)]+","Priority: " + priority);
                    s = s.replace("(?:Short Description: )[\\w\\W]+(?=Description)", "Short Description: " + shortDescription);
                    s = Pattern.compile("^Description: [\\w\\W]+",Pattern.DOTALL).matcher(s).replaceFirst("Description: " + description);
                    items.remove(pos);
                    arrayListAdapter.notifyDataSetChanged();
                    items.add(pos,s);
                    arrayListAdapter.notifyDataSetChanged();
                    break;
                }
            }

        }

        //Not used
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

