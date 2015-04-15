package il.ac.huji.todolist;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TodoListManagerActivity extends ActionBarActivity {

    protected String txt;
    protected int current;
    protected ListView ls;
    protected Dialog dialog;
    protected ArrayAdapter adapter;
    protected ArrayList<ListObj> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);

        ls = (ListView)findViewById(R.id.lstTodoItems);
        ls.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, int position, long id) {
                Button call;
                dialog = new Dialog(parent.getContext());
                LayoutInflater inflater = (LayoutInflater)
                        parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View bodyView = inflater.inflate(R.layout.dialog_body, null);
                dialog.setTitle(list.get(position).txt);
                txt = list.get(position).txt;
                if (txt != null && txt.toLowerCase().startsWith("call ")) {
                    call = (Button) bodyView.findViewById(R.id.menuItemCall);
                    call.setText(list.get(position).txt);
                    call.setVisibility(View.VISIBLE);
                    call.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            startActivity(new Intent(Intent.ACTION_DIAL,
                                    Uri.parse("tel:" + txt.split(" ")[1])));
                        }
                    });
                }
                Button btn = (Button)bodyView.findViewById(R.id.menuItemDelete);
                dialog.setContentView(bodyView);
                current = position;
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        list.remove(current);
                        adapter.notifyDataSetChanged();
                    }
                });
                dialog.show();
                return true;
            }
        });
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
            list.add(new ListObj(data.getStringExtra("title"), (Date) data.getSerializableExtra("dueDate")));
            adapter.notifyDataSetChanged();
        }
    }

    private class MyAdapter extends ArrayAdapter<ListObj> {

        public MyAdapter(Context context, int textViewResourceId, List<ListObj> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);
            }
            Date dt = getItem(position).date;
            String tx = getItem(position).txt;
            TextView text = (TextView) convertView.findViewById(R.id.txtTodoTitle);
            TextView date = (TextView) convertView.findViewById(R.id.txtTodoDueDate);
            text.setText(tx != null ? tx : "");
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
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

    private class ListObj {

        public String txt;
        public Date date;

        public ListObj(String str, Date dt) {
            this.txt = str;
            this.date = dt;
        }
    }
}
