package ch.zli.eb.myfitnessjourney.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SensorService extends Service {
    private LocalBinder myBinder = new LocalBinder();

    public SensorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class LocalBinder extends Binder {
        SensorService getService() {
            return SensorService.this;
        }
    }
}