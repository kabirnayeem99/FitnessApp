package ch.zli.eb.myfitnessjourney.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import ch.zli.eb.myfitnessjourney.service.NotifService;
import ch.zli.eb.myfitnessjourney.speedometer.UserLocation;

public class ViewActivity extends AppCompatActivity implements LocationListener {

    Goal startedGoal;
    LocalTime timeStarted;

    double startLatitude;
    double startLongtitude;
    double calculatedDistance;


    // NEEDED FOR updateTime function
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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
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


        setUpGps();
        setStartPosition(new UserLocation(new Location("Start")));
        updateSpeed(null);


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

    private void updateDistance(UserLocation userLocation) {
        double latitude = userLocation.getLatitude();
        double longtitude = userLocation.getLongitude();

        float[] results = new float[2];

        UserLocation.distanceBetween(startLatitude, startLongtitude, latitude, longtitude, results);

        double distanceInMeters = results[0] / 1000;

        distance.setText(distanceInMeters + " m");
    }


    private void setStartPosition(UserLocation userLocation) {
        startLatitude = userLocation.getLatitude();
        startLongtitude = userLocation.getLongitude();
    }


    @SuppressLint("MissingPermission")
    private void setUpGps() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }




    private void updateSpeed(UserLocation userLocation) {
        float currentUserSpeed = 0;

        if (userLocation != null) {
            currentUserSpeed = userLocation.getSpeed();
        }

        speed.setText(currentUserSpeed + " km/h");

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


    @Override
    protected void onStop() {
        super.onStop();
        Intent notifService = new Intent(getApplicationContext(), NotifService.class);
        startService(notifService);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            UserLocation userLocation = new UserLocation(location);
            updateSpeed(userLocation);
            updateDistance(userLocation);
        }
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }


    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}