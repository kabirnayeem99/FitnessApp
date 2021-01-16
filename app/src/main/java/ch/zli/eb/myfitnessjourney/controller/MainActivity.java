package ch.zli.eb.myfitnessjourney.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.zli.eb.myfitnessjourney.R;
import ch.zli.eb.myfitnessjourney.db.DbManager;
import ch.zli.eb.myfitnessjourney.model.Goal;

public class MainActivity extends AppCompatActivity {

    // VIEW ELEMENTS AS PROPERTIES
    ProgressBar progressBar;

    TextView stepsStatus;
    TextView todaysGoalDesc;

    Button historyButton;
    Button progressButton;
    Button currentButton;
    Button createButton;

    Goal todaysGoal;
    // DB HELPER USED TO FETCH GOALS FROM SQLITE DB
    DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ASSIGNING VIEW ELEMENTS TO PROPERTIES
        progressBar = findViewById(R.id.progressbar);

        stepsStatus = findViewById(R.id.stepsStatus);
        todaysGoalDesc = findViewById(R.id.goalDesc);

        historyButton = findViewById(R.id.historyButton);
        progressButton = findViewById(R.id.progressButton);
        currentButton = findViewById(R.id.currentButton);
        createButton = findViewById(R.id.createButton);

        try {
            setTodaysGoal();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // HANDLES createButton CLICK -> REDIRECTS TO CREATE ACTIVITY
    public void redirectToCreateActivity(View v) {
        Intent createForm = new Intent(getApplicationContext(), CreateActivity.class);
        startActivity(createForm);
    }

    // HANDLES currentButton CLICK -> REDIRECTS TO LIST ACTIVITY
    public void redirectToListActivityCurrent(View v) {
        Intent listActivity = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(listActivity);
    }

    // HANDLES currentButton CLICK -> REDIRECTS TO LIST ACTIVITY
    public void redirectToListActivityHistory(View v) {
        Intent listActivityHistory = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(listActivityHistory);
    }

    public void setTodaysGoal() throws ParseException {
        dbManager = new DbManager(getApplicationContext());
        ArrayList<Goal> goals = new ArrayList<>();

        goals = dbManager.getGoals();

        for (Goal g: goals) {
            DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
            dateFormatter.setLenient(false);

            Date start = g.getStartDate();
            Date end = g.getEndDate();
            Date today = dateFormatter.parse(dateFormatter.format(new Date()));

            if (start.compareTo(today) < 0 && end.compareTo(today) > 0) {
                todaysGoalDesc.setText(g.getName());
                break;
            } else if (start.compareTo(today) == 0 && end.compareTo(today) > 0) {
                todaysGoalDesc.setText(g.getName());
                break;
            } else if (start.compareTo(today) < 0 && end.compareTo(today) == 0) {
                todaysGoalDesc.setText(g.getName());
                break;
            } else if (start.compareTo(today) == 0 && end.compareTo(today) == 0) {
                todaysGoalDesc.setText(g.getName());
                break;
            }
        }
    }
}