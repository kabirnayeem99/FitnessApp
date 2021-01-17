package ch.zli.eb.myfitnessjourney.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

import ch.zli.eb.myfitnessjourney.R;
import ch.zli.eb.myfitnessjourney.db.DbManager;
import ch.zli.eb.myfitnessjourney.model.Goal;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // VIEW ELEMENTS AS PROPERTIES
    ProgressBar progressBar;

    TextView stepsStatus;
    TextView todaysGoalDesc;

    Button historyButton;
    Button progressButton;
    Button currentButton;
    Button createButton;

    SensorManager sensorManager = null;
    Sensor stepCounter;

    Goal todaysGoal;

    boolean sensorRunning = false;
    int stepCount = 0;
    int previousStepCount = 0;

    Date loadDate;
    Date today;


    // DB HELPER USED TO FETCH GOALS FROM SQLITE DB
    DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            loadData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // ASSIGNING VIEW ELEMENTS TO PROPERTIES
        progressBar = findViewById(R.id.progressbar);
        progressBar.setMax(10000);

        stepsStatus = findViewById(R.id.stepsStatus);
        todaysGoalDesc = findViewById(R.id.goalDesc);

        historyButton = findViewById(R.id.historyButton);
        progressButton = findViewById(R.id.progressButton);
        currentButton = findViewById(R.id.currentButton);
        createButton = findViewById(R.id.createButton);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
        }

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

    // HANDLES currentButton CLICK -> REDIRECTS TO LIST ACTIVITY WITH TYPE CURRENT
    public void redirectToListActivityCurrent(View v) {
        Intent listActivity = new Intent(getApplicationContext(), ListActivity.class);
        listActivity.putExtra("history", "false");
        startActivity(listActivity);
    }

    // HANDLES historyButton CLICK -> REDIRECTS TO LIST ACTIVITY WITH TYPE HISTORY
    public void redirectToListActivityHistory(View v) {
        Intent listActivityHistory = new Intent(getApplicationContext(), ListActivity.class);
        listActivityHistory.putExtra("history", "true");
        startActivity(listActivityHistory);
    }

    // HANDLES progressButton CLICK
    public void startGoal(View v) {
        if (todaysGoal != null) {
            Intent startGoalIntent = new Intent(getApplicationContext(), ViewActivity.class);

            LocalTime timeStarted = LocalTime.now();

            startGoalIntent.putExtra("startedGoal", todaysGoal);
            startGoalIntent.putExtra("goalStarted", timeStarted.toString());
            startActivity(startGoalIntent);
        } else {
            Toast.makeText(getApplicationContext(), "No goals for today", Toast.LENGTH_LONG).show();
        }

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

            todaysGoal = g;

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

    @Override
    protected void onResume() {
        super.onResume();

        sensorRunning = true;

        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepCounter == null) {
            stepsStatus.setText("Device does not support this feature");
        } else {
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            saveSteps();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            saveSteps();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (sensorRunning) {
            stepCount = (int) event.values[0];
            int currentSteps = stepCount - previousStepCount;

            stepsStatus.setText(stepCount + " Steps from 10.000");
            progressBar.setProgress(currentSteps);
        }
    }

    public void resetData() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        dateFormatter.setLenient(false);
        today = dateFormatter.parse(dateFormatter.format(new Date()));
        stepCount = 0;
        previousStepCount = 0;

        String todayString = dateFormatter.format(today);

        SharedPreferences sharedPreferences = getSharedPreferences("MyFitnessJourney", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("steps", 0);
        editor.putString("lastloggedday", todayString);
        editor.apply();
    }

    public void saveSteps() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        dateFormatter.setLenient(false);
        today = dateFormatter.parse(dateFormatter.format(new Date()));

        String todayString = dateFormatter.format(today);

        SharedPreferences sharedPreferences = getSharedPreferences("MyFitnessJourney", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("steps", previousStepCount);
        editor.putString("lastloggedday", todayString);
        editor.apply();
    }

    public void loadData() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        dateFormatter.setLenient(false);
        today = dateFormatter.parse(dateFormatter.format(new Date()));

        SharedPreferences sharedPreferences = getSharedPreferences("MyFitnessJourney", Context.MODE_PRIVATE);
        int savedSteps = sharedPreferences.getInt("steps", 0);
        String loadDateStr = sharedPreferences.getString("lastloggedday", "");

        loadDate = dateFormatter.parse(loadDateStr);

        if (today.compareTo(loadDate) > 0) {
            resetData();
        } else {
            previousStepCount = savedSteps;
            today = loadDate;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}