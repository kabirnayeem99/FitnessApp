package ch.zli.eb.myfitnessjourney.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

import ch.zli.eb.myfitnessjourney.model.Goal;

public class DbManager extends SQLiteOpenHelper {

    private static final String DB_NAME = "FitnessJourney.db";
    private static final String TABLE_GOAL = "GOAL_TABLE";
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_START_DATE = "START_DATE";
    private static final String COLUMN_END_DATE = "END_DATE";
    private static final String COLUMN_REMINDERS = "REMINDERS";
    private static final String COLUMN_STARTED = "STARTED";
    private static final String COLUMN_TIME = "TIME";
    private static final String COLUMN_ID = "ID";

    public DbManager(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_GOAL + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " + COLUMN_STARTED + " INTEGER, " + COLUMN_REMINDERS + " INTEGER, " + COLUMN_START_DATE + " TEXT, " + COLUMN_END_DATE + " TEXT, " + COLUMN_TIME + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOAL);
        onCreate(db);
    }

    public boolean addRecord(Goal userGoal) {
        Format formatter = new SimpleDateFormat("dd.MM.yyyy");
        String startDateS = formatter.format(userGoal.getStartDate());
        String endDateS = formatter.format(userGoal.getEndDate());

        SQLiteDatabase goalDb = this.getWritableDatabase();
        ContentValues contentVal = new ContentValues();

        contentVal.put(COLUMN_NAME, userGoal.getName());
        contentVal.put(COLUMN_STARTED, (userGoal.isStarted()) ? 1 : 0);
        contentVal.put(COLUMN_REMINDERS, (userGoal.isReminders()) ? 1 : 0);
        contentVal.put(COLUMN_START_DATE, startDateS);
        contentVal.put(COLUMN_END_DATE, endDateS);
        contentVal.put(COLUMN_TIME, userGoal.getTime().toString());

        long result = goalDb.insert(TABLE_GOAL, null, contentVal);

        if (result == 1) {
            return false;
        } else {
            return true; 
        }
    }

    public ArrayList<Goal> getGoals() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        dateFormatter.setLenient(false);

        ArrayList<Goal> goalList = new ArrayList<>();

        String getAllQuery = "SELECT * FROM " + TABLE_GOAL;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getAllQuery, null);

        if (cursor.moveToFirst()) {
          while (!cursor.isAfterLast()) {
              // DATA FETCHED FROM EACH ROW
              int goalId = cursor.getInt(0);
              String goalName = cursor.getString(1);
              boolean started = (cursor.getInt(2) == 1) ? true : false;
              boolean reminders = (cursor.getInt(3) == 1) ? true : false;
              Date startDate = dateFormatter.parse(cursor.getString(4));
              Date endDate = dateFormatter.parse(cursor.getString(5));
              LocalTime time = LocalTime.parse(cursor.getString(6));

              goalList.add(new Goal(goalId, goalName, time, reminders, startDate, endDate, started));
            cursor.moveToNext();
          }
        }

        cursor.close();
        db.close();

        return goalList;
    }
}
