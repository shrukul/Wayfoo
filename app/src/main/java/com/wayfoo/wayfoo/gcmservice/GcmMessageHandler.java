package com.wayfoo.wayfoo.gcmservice;

/**
 * Created by shrukul on 18/3/16.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.wayfoo.wayfoo.R;
import com.wayfoo.wayfoo.Rate_Hotel;

public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;
    PendingIntent pendingIntent;
    String hotel;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String balance = data.getString("balance");
        hotel = data.getString("hotel");
        System.out.println("Name of hotel is "+hotel);
        String typ="yay";
        String type = data.getString("type");
        int typeInt = Integer.parseInt(type);
        Intent myIntent = new Intent(this, Rate_Hotel.class);
        myIntent.putExtra("hotel",hotel);
        pendingIntent = PendingIntent.getActivity(
                this,
                0,
                myIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        System.out.println("Type"+typeInt);
        if (typeInt == 2) {
            message = "Your Order will be delivered. The amount payable is ₹"+message+" Click here to rate your experience";
            typ = "Done";
        } else if(typeInt == 1){
            message = "Your Order has been confirmed. The amount payable is ₹"+message;
            typ = "Confirmed";
        } else if(typeInt == 0){
            message = "Oops! your Order has been cancelled. Thanks for using our services";
            typ = "Cancelled";
        }

        createNotification(typ, message);
    }

    // Creates notification based on title and body received
    private void createNotification(String typ, String body) {
        Context context = getBaseContext();
/*        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notify).setContentTitle(title)
                .setContentText(body);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());*/
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        android.support.v7.app.NotificationCompat.Builder mBuilder = (android.support.v7.app.NotificationCompat.Builder) new android.support.v7.app.NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.food)
                .setContentTitle("Wayfoo")
                .setContentText(typ)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.app_icon))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body));

        if(!hotel.isEmpty())
            mBuilder.setContentIntent(pendingIntent);
        ;
        notificationManager.notify(1, mBuilder.build());
    }

}
