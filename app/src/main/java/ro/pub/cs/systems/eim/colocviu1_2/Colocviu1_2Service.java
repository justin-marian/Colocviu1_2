package ro.pub.cs.systems.eim.colocviu1_2;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Colocviu1_2Service extends Service {

    public static final String ACTION_SUM_BROADCAST = "ro.pub.cs.systems.eim.colocviu1_2.SUM_BROADCAST";
    public static final String EXTRA_SUM = "sum";
    public static final String EXTRA_TIMESTAMP = "timestamp";
    private final Handler handler = new Handler();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int sum = intent.getIntExtra(EXTRA_SUM, 0);

        handler.postDelayed(() -> {
            Intent broadcastIntent = new Intent(ACTION_SUM_BROADCAST);
            broadcastIntent.putExtra(EXTRA_SUM, sum);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            broadcastIntent.putExtra(EXTRA_TIMESTAMP, timestamp);

            sendBroadcast(broadcastIntent);
        }, 2000);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
