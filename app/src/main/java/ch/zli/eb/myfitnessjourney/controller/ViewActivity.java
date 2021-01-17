package ch.zli.eb.myfitnessjourney.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.zli.eb.myfitnessjourney.R;
import ch.zli.eb.myfitnessjourney.model.Goal;

public class ViewActivity extends AppCompatActivity {

    Goal startedGoal;

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

        try {
            handleDeadlineProgressBar();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
}