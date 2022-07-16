package com.android.brogrammers.sportsm8.DataBaseConnection.OfflineDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Meeting;


/**
 * Created by Korbi on 20.09.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String TABLE_NAME = "meetings";
    private static String COL1 = "MeetingID";
    private static String COL2 = "meetingActivity";

    public DatabaseHelper(Context context) {
        super(context,TABLE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "+COL2 +" TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(Meeting meeting){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,meeting.meetingActivity);

        Log.d(TAG,"addData: Adding"+meeting.meetingActivity+"to" +TABLE_NAME);

        long result = db.insert(TABLE_NAME,null,contentValues);
        return result != -1;

    }
}
