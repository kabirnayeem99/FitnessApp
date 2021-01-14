package ch.zli.eb.myfitnessjourney.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import ch.zli.eb.myfitnessjourney.R;

public class ViewActivity extends AppCompatActivity {

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
    }
}