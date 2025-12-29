package com.subh.shubhechha.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.subh.shubhechha.Activities.SplashActivity;
import com.subh.shubhechha.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class FcmService extends FirebaseMessagingService {
    private static final String TAG = "FCM";
    String noti_data=null;
    String module_id=null;
    String shop_id="";
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From1: " + remoteMessage.getFrom());
        Log.e(TAG, "From2: " + remoteMessage.getNotification().getTitle());
        Log.e(TAG, "From3: " + remoteMessage.getNotification().getBody());
         Log.e(TAG, "From4: " + remoteMessage.getData());
        Log.e(TAG, "image: " + remoteMessage.getNotification().getImageUrl());


        Log.e(TAG, "channelid: " +remoteMessage.getNotification().getChannelId());
        Log.e(TAG, "clickaction: " +remoteMessage.getNotification().getClickAction());
        Log.e(TAG, "sound: " +remoteMessage.getNotification().getSound());

        String channelid = remoteMessage.getNotification().getChannelId();
        String clickAction = remoteMessage.getNotification().getClickAction();

        String image="";

        if (remoteMessage.getNotification().getImageUrl() != null) {
            image=remoteMessage.getNotification().getImageUrl().toString();
        }


        String pid = remoteMessage.getData().get("pid");


        sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),image,channelid,clickAction,pid);

    }
    @Override
    public void onNewToken(String token) {
        Log.e(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

//    private void scheduleJob() {
//        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class).build();
//        WorkManager.getInstance(this).beginWith(work).enqueue();
//    }

    private void handleNow() {
        Log.e(TAG, "Short lived task is done.");
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void sendNotification(String title, String messageBody, String img, String channelId ,String clickAction,String pid) {


        Intent intent;
        if (clickAction != null) {
            // Handle custom click action if provided
            intent = new Intent(clickAction);
            intent.putExtra("id", Integer.parseInt(pid)); // Optional: You can pass additional data if needed
        } else {
            // Default intent to open SplashActivity
            intent = new Intent(this, SplashActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder;

        if (channelId != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Use the provided channelId if not null and API level is 26+
            notificationBuilder = new NotificationCompat.Builder(this, channelId);
        } else {
            // Fallback to a default channel if channelId is null or API level < 26
            channelId = "zruri"; // Default channel ID
            notificationBuilder = new NotificationCompat.Builder(this, channelId);

            // Create the default channel if API level is 26+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "All Notification",
                        NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(getBitmapfromUrl(img))
                        .setBigContentTitle(title))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        // Notify using the specified channel
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(0 , notificationBuilder.build());
    }






    public Bitmap getBitmapfromUrl(String imageUrl) {

        Log.d(TAG, "getBitmapfromUrl: "+imageUrl);
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}
