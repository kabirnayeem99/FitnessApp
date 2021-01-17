package ch.zli.eb.myfitnessjourney.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

import ch.zli.eb.myfitnessjourney.R;
import ch.zli.eb.myfitnessjourney.model.Goal;

public class ViewActivity extends AppCompatActivity {

    Goal startedGoal;
    LocalTime timeStarted;

    Runnable timeUpdater;
    int timePassed = 0;

    // VIEW ELEMENTS AS PROPERTIES
    TextView goalName;

    ProgressBar deadlineProgress;
    ProgressBar timeLeft;

    TextView distance;
    TextView speed;

    TextView deadlineStart;
    TextView deadlineEnd;
    TextView deadlineMid;

    TextView timeStart;
    TextView timeMid;
    TextView timeEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        // ASSIGNING VIEW ELEMENTS TO PROPERTIES
        goalName = findViewById(R.id.goalNameView);

        deadlineProgress = findViewById(R.id.deadlineBar);
        timeLeft = findViewById(R.id.timeBar);

        distance = findViewById(R.id.distanceUnit);
        speed = findViewById(R.id.speedUnit);

        deadlineStart = findViewById(R.id.deadlineStart);
        deadlineEnd = findViewById(R.id.deadlineEnd);
        deadlineMid = findViewById(R.id.deadlineMid);

        timeStart = findViewById(R.id.timeStart);
        timeMid = findViewById(R.id.timeMid);
        timeEnd = findViewById(R.id.timeEnd);

        startedGoal = (Goal) getIntent().getSerializableExtra("startedGoal");
        startedGoal.setStarted(true);

        timeStarted = LocalTime.parse(getIntent().getStringExtra("goalStarted"));
        setGoalName();

        try {
            handleDeadlineProgressBar();
            updateTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void setGoalName() {
        goalName.setText(startedGoal.getName());
    }

    private void handleDeadlineProgressBar() throws ParseException {
        // REQUIRED DATE FORMAT
        DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        dateFormatter.setLenient(false);
        Date todaysDate = dateFormatter.parse(dateFormatter.format(new Date()));

        deadlineProgress.setMax(Integer.MAX_VALUE);
        deadlineProgress.setProgress(Math.toIntExact(todaysDate.getTime() / 1000));

        deadlineEnd.setText(dateFormatter.format(startedGoal.getEndDate()));
        deadlineMid.setText(dateFormatter.format(todaysDate));
        deadlineStart.setText(dateFormatter.format(startedGoal.getStartDate()));
    }

    private void updateTime() {
        timeStart.setText("00:00:00");

        final Handler timerHandler = new Handler();
        LocalTime midnight = LocalTime.parse("00:00:00");
        int timeTotal = (int) midnight.until(startedGoal.getTime(), SECONDS);

        timeLeft.setMax(timeTotal);
        timeEnd.setText(LocalTime.ofSecondOfDay(timeTotal).toString());

        timeUpdater = new Runnable() {
            @Override
            public void run() {
                timePassed++;
                if (timePassed > timeTotal) {
                    timerHandler.removeCallbacks(timeUpdater);
                    timePassed = 0;
                    
                } else {
                    LocalTime timePassedFormat = LocalTime.ofSecondOfDay(timePassed);
                    timeMid.setText(timePassedFormat.toString());
                    timeLeft.setProgress(timePassed);
                }

                timerHandler.postDelayed(timeUpdater,1000);
            }
        };

        timerHandler.post(timeUpdater);
    }
}