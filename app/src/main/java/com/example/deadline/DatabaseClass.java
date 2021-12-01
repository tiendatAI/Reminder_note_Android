package com.example.deadline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseClass extends SQLiteOpenHelper {
    Context context;
    private static final String DatabaseName = "MyReminders";
    private static final int DatabaseVersion = 1;

    private static final String TableName = "my_reminders";
    private static final String ColumnId = "id_reminder";
    private static final String ColumnTitle = "title_reminder";
    private static final String ColumnDes1 = "description1_reminder";
    private static final String ColumnDes2 = "description2_reminder";

    public DatabaseClass(@Nullable Context context) {
        super(context, DatabaseName, null, DatabaseVersion);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TableName +
                " (" + ColumnId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ColumnTitle + " TEXT, " +
                ColumnDes1 + " TEXT, " +
                ColumnDes2 + " TEXT);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TableName);
        onCreate(db);
    }

    Integer addReminders(String title, String des1, String des2){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ColumnTitle, title);
        contentValues.put(ColumnDes1, des1);
        contentValues.put(ColumnDes2, des2);

        long resultValue = sqLiteDatabase.insert(TableName, null, contentValues); //importance

        if(resultValue == -1){
            Toast.makeText(context, "Data not added", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(context, "Data added successfully", Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, String.valueOf(resultValue), Toast.LENGTH_SHORT).show();
            return (int) resultValue;
        }
        return 0;
    }

    Cursor readAllData() {
        String query = "SELECT * FROM " + TableName;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public void deleteAllReminders() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "DELETE FROM " + TableName;
        sqLiteDatabase.execSQL(query);
    }

    public void deleteSingleItem(String id) {
        SQLiteDatabase database = this.getWritableDatabase();

        long result = database.delete(TableName, "id_reminder=?", new String[]{id});
        if (result == -1) {
            Toast.makeText(context, "Item not deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
        }
    }


}
