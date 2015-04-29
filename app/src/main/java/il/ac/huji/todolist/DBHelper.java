package il.ac.huji.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int ID = 0;
    public static final int ITEM = 1;
    public static final int DATE = 2;
    public static final String NAME = "todo_db";
    public static final String[] COLLS = new String[]{"_id", "item", "date"};

    private static final int INIT_VERSION = 1;
    private static final String DB_CREATE = "create table " + NAME + " (" + COLLS[ID] +
            " integer primary key autoincrement, " + COLLS[ITEM] + " string, " + COLLS[DATE] + " integer);";

    public DBHelper(Context context) {
        super(context, NAME + ".db", null, INIT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + NAME);
        onCreate(db);
    }

}
