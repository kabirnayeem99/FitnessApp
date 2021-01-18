package ch.zli.eb.myfitnessjourney.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.zli.eb.myfitnessjourney.R;
import ch.zli.eb.myfitnessjourney.db.DbManager;
import ch.zli.eb.myfitnessjourney.model.Goal;
import ch.zli.eb.myfitnessjourney.service.NotifService;

public class ListActivity extends AppCompatActivity {

    // VIEW ELEMENTS AS PROPERTY
    ListView goalList;
    TextView listDesc;

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
        listDesc = findViewById(R.id.listDesc);

        history = Boolean.parseBoolean(getIntent().getStringExtra("history"));

        // DB HELPER AND ARRAYLIST FOR QUERIES
        dbManager = new DbManager(getApplicationContext());

        // FILLS ARRAY LISTS ACCORDING TO TYPE
        try {
            setGoalLists();
            populateList();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void populateList() throws ParseException {
        ArrayList<String> nameListHistory = new ArrayList<>();
        ArrayList<String> nameListCurrent = new ArrayList<>();

        if (Boolean.parseBoolean(getIntent().getStringExtra("history"))) {
            if (!goalListTypeHistory.isEmpty()) {
                listDesc.setText("Completed Goals:");
                for (Goal g: goalListTypeHistory) {
                    nameListHistory.add(g.getId() + " | " + g.getName());
                }
                goalArrayAdapter  = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, nameListHistory);
            } else {
                listDesc.setText("No completed goals have been found");
            }

        } else if (!goalListTypeCurrent.isEmpty()) {
            listDesc.setText("Current Goals");
            for (Goal g: goalListTypeCurrent) {
                nameListCurrent.add(g.getId() + " | " + g.getName());
            }
            goalArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, nameListCurrent);
        } else {
            listDesc.setText("No current goals have been found");
        }
        goalList.setAdapter(goalArrayAdapter);
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
                if (g.getEndDate().compareTo(todaysDate) < 0 || g.isStarted()) {
                    goalListTypeHistory.add(g);
                } else if (g.getEndDate().compareTo(todaysDate) == 0 || g.getEndDate().compareTo(todaysDate) > 0) {
                    goalListTypeCurrent.add(g);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent notifService = new Intent(getApplicationContext(), NotifService.class);
        startService(notifService);
    }

}