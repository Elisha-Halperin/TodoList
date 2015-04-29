package il.ac.huji.todolist;

import android.support.v7.app.ActionBarActivity;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.content.Context;
import android.database.Cursor;
import android.widget.ListView;
import android.widget.TextView;
import android.graphics.Color;
import android.content.Intent;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.widget.Button;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.net.Uri;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TodoListManagerActivity extends ActionBarActivity {

    private static final int PHONE_NUMBER = 1;

    private String txt;
    private int current;
    private Dialog dialog;
    private ArrayAdapter adapter;
    private SQLiteDatabase write_db;
    private ArrayList<ListItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);

        ListView ls = (ListView) findViewById(R.id.lstTodoItems);
        ls.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, int position, long id) {
                Button call;
                dialog = new Dialog(parent.getContext());
                LayoutInflater inflater = (LayoutInflater)
                        parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View bodyView = inflater.inflate(R.layout.dialog_body, parent, false);
                dialog.setTitle(list.get(position).item);
                txt = list.get(position).item;
                if (txt != null && txt.toLowerCase().startsWith("call ")) {
                    call = (Button) bodyView.findViewById(R.id.menuItemCall);
                    call.setText(list.get(position).item);
                    call.setVisibility(View.VISIBLE);
                    call.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            startActivity(new Intent(Intent.ACTION_DIAL,
                                    Uri.parse("tel:" + txt.split(" ")[PHONE_NUMBER])));
                        }
                    });
                }
                Button btn = (Button) bodyView.findViewById(R.id.menuItemDelete);
                dialog.setContentView(bodyView);
                current = position;
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        write_db.delete(DBHelper.NAME, DBHelper.COLLS[DBHelper.ID] + "=" + list.get(current).id, null);
                        list.remove(current);
                        adapter.notifyDataSetChanged();
                    }
                });
                dialog.show();
                return true;
            }
        });
        DBHelper db = new DBHelper(this);
        write_db = db.getWritableDatabase();
        Cursor cursor = write_db.query(DBHelper.NAME, null, null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            list.add(new ListItem(cursor.getString(DBHelper.ITEM),
                    new Date(cursor.getLong(DBHelper.DATE)), cursor.getInt(DBHelper.ID)));
        }
        cursor.close();
        adapter = new MyAdapter(this, android.R.layout.simple_list_item_1, list);
        ls.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_list_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menuItemAdd) {
            startActivityForResult(new Intent(this, AddNewTodoItemActivity.class), 0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            String item = data.getStringExtra(AddNewTodoItemActivity.TITLE);
            Date date = (Date)data.getSerializableExtra(AddNewTodoItemActivity.DATE);
            ContentValues vals = new ContentValues();
            vals.put(DBHelper.COLLS[DBHelper.ITEM], item);
            vals.put(DBHelper.COLLS[DBHelper.DATE], date.getTime());
            write_db.insert(DBHelper.NAME, null, vals);
            Cursor cursor = write_db.query(DBHelper.NAME, null, null, null, null, null, null);
            cursor.moveToLast();
            list.add(new ListItem(item != null ? item : "", date, cursor.getInt(DBHelper.ID)));
            adapter.notifyDataSetChanged();
            cursor.close();
        }
    }

    private class MyAdapter extends ArrayAdapter<ListItem> {

        public MyAdapter(Context context, int textViewResourceId, List<ListItem> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);
            }
            Date dt = getItem(position).date;
            String tx = getItem(position).item;
            TextView text = (TextView) convertView.findViewById(R.id.txtTodoTitle);
            TextView date = (TextView) convertView.findViewById(R.id.txtTodoDueDate);
            text.setText(tx);
            DateFormat sdf = DateFormat.getDateInstance(DateFormat.SHORT);
            String dateString = sdf.format(dt);
            date.setText(dt != null ? dateString : "No due date");
            text.setTextColor(Color.BLACK);
            date.setTextColor(Color.BLACK);
            if (dt != null && dt.before(new Date())) {
                text.setTextColor(Color.RED);
                date.setTextColor(Color.RED);
            }
            return convertView;
        }
    }

    private class ListItem {

        public String item;
        public Date date;
        int id;

        public ListItem(String str, Date dt, int id) {
            this.item = str;
            this.date = dt;
            this.id = id;
        }
    }
}