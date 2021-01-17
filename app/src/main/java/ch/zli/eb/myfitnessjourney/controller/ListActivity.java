package ch.zli.eb.myfitnessjourney.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.ParseException;
import java.util.ArrayList;

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

    // DB HELPER FOR GET ALL GOALS QUERY
    DbManager dbManager;
    ArrayAdapter goalArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // ASSIGNING VIEW ELEMENT TO PROPERTY
        goalList = findViewById(R.id.goalListUi);

        // DB HELPER AND ARRAYLIST FOR QUERIES
        dbManager = new DbManager(getApplicationContext());

        try {
            goalListDb = dbManager.getGoals();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        goalArrayAdapter  = new ArrayAdapter<Goal>(getApplicationContext(), android.R.layout.simple_list_item_1, goalListDb);
        goalList.setAdapter(goalArrayAdapter);

    }
}