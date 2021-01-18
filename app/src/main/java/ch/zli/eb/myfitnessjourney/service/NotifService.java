package ch.zli.eb.myfitnessjourney.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.GetChars;

import androidx.core.app.NotificationCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ch.zli.eb.myfitnessjourney.db.DbManager;
import ch.zli.eb.myfitnessjourney.model.Goal;

public class NotifService extends Service {
    private String channelId = "myfitnessjourney_1";
    private int notificationId = 101;

    private LocalBinder myBinder = new LocalBinder();

    private Timer notifTimer;
    private TimerTask notifTimerTask;

    private final int intervalSec = 5;

    private Handler notifHandler;


    public NotifService() throws ParseException {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        try {
            startTimer();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }


    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        stopTimerTask();
        super.onDestroy();
    }

    public void startTimer() throws ParseException {
        createNotificationChannel();
        notifTimer = new Timer();

        initializeTimerTask();

        notifTimer.schedule(notifTimerTask, 5000, intervalSec * 1000);
    }

    public void stopTimerTask() {
        if (notifTimer != null) {
            notifTimer.cancel();
            notifTimer = null;
        }
    }

    public void initializeTimerTask() throws ParseException {
        DbManager dbManager = new DbManager(this);
        ArrayList<Goal> goalList = dbManager.getGoals();

        // REQUIRED DATE FORMAT
        DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        dateFormatter.setLenient(false);
        Date todaysDate = dateFormatter.parse(dateFormatter.format(new Date()));

        notifTimerTask = new TimerTask() {
            public void run() {
                Looper.prepare();
                notifHandler = new Handler();
                notifHandler.post(new Runnable() {
                    public void run() {
                        for (Goal g : goalList) {
                            if (g.isReminders()) {
                                if (g.getEndDate().compareTo(todaysDate) == 0) {
                                    sendNotification(g);
                                }
                            }
                        }

                    }
                });
                Looper.loop();
            }
        };
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String notifTitle = "Title";
            String notifDesc = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notifChannel = new NotificationChannel(channelId, notifTitle, importance);
            getSystemService(NotificationManager.class).createNotificationChannel(notifChannel);
        }
    }

    private void sendNotification(Goal g) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Goal Deadline")
                .setContentText(g.getName() + "is about to pass the deadline!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        getSystemService(NotificationManager.class).notify(notificationId, builder.build());
    }

    public class LocalBinder extends Binder {
        NotifService getService() {
            return NotifService.this;
        }
    }
}