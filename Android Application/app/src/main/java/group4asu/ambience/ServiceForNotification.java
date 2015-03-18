package group4asu.ambience;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by AmeyVikas on 3/16/2015.
 */
public class ServiceForNotification extends Service {

    String serverUrl;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            new listen().execute();
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void sendNotification(String not) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle("Ambience");
        builder.setContentText("Rapid increase in temperature detected");
        builder.setSmallIcon(R.drawable.ic_launcher);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(10, notification);
    }

    class listen extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ServerSocket ss;
            Socket soc;
            try {
                ss = new ServerSocket(8000);
                while (true) {
                    soc = ss.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    String line, resp = "";
                    in.readLine();
                    in.readLine();
                    in.readLine();
                    while ((line = in.readLine()) != null) {
                        resp += line;
                    }
                    sendNotification(resp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}