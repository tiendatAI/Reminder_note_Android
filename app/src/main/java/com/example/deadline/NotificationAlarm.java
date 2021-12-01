package com.example.deadline;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //get information
        String title = intent.getStringExtra("title");
        String subject = intent.getStringExtra("subject");
        Integer ID = intent.getIntExtra("ID", 0);

        // Create an explicit intent for an Activity in your app
        Intent intent_1 = new Intent(context, AlertDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent_1, 0);

        //build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_baseline_event_note_24)
                .setContentTitle(title)
                .setContentText(subject)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(ID, builder.build());
    }

}
