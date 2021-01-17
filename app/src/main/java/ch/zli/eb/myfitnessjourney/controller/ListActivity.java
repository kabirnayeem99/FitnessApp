package ch.zli.eb.myfitnessjourney.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.zli.eb.myfitnessjourney.R;
import ch.zli.eb.myfitnessjourney.db.DbManager;
import ch.zli.eb.myfitnessjourney.model.Goal;

public class ListActivity extends AppCompatActivity {

    // VIEW ELEMENT AS PROPERTY
    ListView goalList;

    // LIST TYPE -> HISTORY OR CURRENT -> HISTORY: TRUE -> GOALS IN PAST ARE DISPLAYED -> FALSE -> CURRENT GOALS
    boolean history;

    // GOAL ARRAY LIST FOR POPULATING LISTVIEW
    ArrayList<Goal> goalListDb = new ArrayList<>();
    ArrayList<Goal> goalListTypeHistory = new ArrayList<>();
    ArrayList<Goal> goalListTypeCurrent = new ArrayList<>();

    // DB HELPER FOR GET ALL GOALS QUERY
    DbManager dbManager;
    ArrayAdapter goalArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // ASSIGNING VIEW ELEMENT TO PROPERTY
        goalList = findViewById(R.id.goalListUi);

        history = Boolean.parseBoolean(getIntent().getStringExtra("history"));

        // DB HELPER AND ARRAYLIST FOR QUERIES
        dbManager = new DbManager(getApplicationContext());

        // FILLS ARRAY LISTS ACCORDING TO TYPE
        try {
            setGoalLists();
            populateList();
            checkListStatus();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void populateList() throws ParseException {
        if (Boolean.parseBoolean(getIntent().getStringExtra("history"))) {
            goalArrayAdapter  = new ArrayAdapter<Goal>(getApplicationContext(), android.R.layout.simple_list_item_1, goalListTypeHistory);
        } else {
            goalArrayAdapter = new ArrayAdapter<Goal>(getApplicationContext(), android.R.layout.simple_list_item_1, goalListTypeCurrent);
        }
        goalList.setAdapter(goalArrayAdapter);

        if (goalListTypeCurrent.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No current goals have been found", Toast.LENGTH_LONG).show();
        } else if (goalListTypeHistory.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No completed goals have been found", Toast.LENGTH_LONG).show();
        }
    }

    public void setGoalLists() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        dateFormatter.setLenient(false);
        Date todaysDate = dateFormatter.parse(dateFormatter.format(new Date()));

        goalListDb = dbManager.getGoals();

        if (goalListDb.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No goals have been found", Toast.LENGTH_LONG).show();
        } else {
            for (Goal g: goalListDb) {
                if (g.getEndDate().compareTo(todaysDate) < 0) {
                    goalListTypeHistory.add(g);
                } else if (g.getEndDate().compareTo(todaysDate) == 0 || g.getEndDate().compareTo(todaysDate) > 0) {
                    goalListTypeCurrent.add(g);
                }
            }
        }
    }

    public void checkListStatus() {
        if (goalListTypeCurrent.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No current goals have been found", Toast.LENGTH_LONG).show();
        } else if (goalListTypeHistory.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No completed goals have been found", Toast.LENGTH_LONG).show();
        }
    }
}