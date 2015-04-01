package il.ac.huji.todolist;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class TodoListManagerActivity extends ActionBarActivity {

    protected int current;
    protected ListView ls;
    protected Dialog dialog;
    protected ArrayAdapter<String> adapter;
    protected ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);

        ls = (ListView)findViewById(R.id.todoList);
        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                dialog = new Dialog(parent.getContext());
                LayoutInflater inflater = (LayoutInflater)
                        parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View bodyView = inflater.inflate(R.layout.dialog_body, null);
                ((TextView)bodyView.findViewById(R.id.textViewTitle)).setText(list.get(position));
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

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        if (id == R.id.menuItemAdd) {
            String tmp = ((EditText)findViewById(R.id.editText)).getText().toString();
            if (tmp != null && !tmp.isEmpty()) {
                list.add(tmp);
                adapter.notifyDataSetChanged();
                ((EditText)findViewById(R.id.editText)).getText().clear();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyAdapter extends ArrayAdapter<String> {

        public MyAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);
            }
            TextView tx = (TextView) convertView.findViewById(R.id.lstTodoItems);
            tx.setText(getItem(position));
            tx.setTextColor(Color.WHITE);
            tx.setBackgroundColor(position % 2 == 0 ? Color.RED : Color.BLUE);
            return convertView;
        }
    }
}
