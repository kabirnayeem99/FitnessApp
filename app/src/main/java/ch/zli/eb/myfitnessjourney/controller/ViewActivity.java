package ch.zli.eb.myfitnessjourney.controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import static java.time.temporal.ChronoUnit.SECONDS;

import ch.zli.eb.myfitnessjourney.R;
import ch.zli.eb.myfitnessjourney.model.Goal;

public class ViewActivity extends AppCompatActivity {

    Goal startedGoal;
    LocalTime timeStarted;

    final Handler timerHandler = new Handler();
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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

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

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, startedGoal.getName());
                    values.put(MediaStore.Images.Media.DESCRIPTION, "Capture at end of goal");
                    Uri image_url = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Intent openCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                    openCamera.putExtra(MediaStore.EXTRA_OUTPUT, image_url);
                    startActivityForResult(openCamera, 1001);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timeUpdater);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Intent goToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(goToMainActivity);
        }
    }
}