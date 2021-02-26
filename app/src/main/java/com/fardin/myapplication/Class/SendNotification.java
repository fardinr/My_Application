package com.fardin.myapplication.Class;

// this class is used to genrate notifiacion came from notificaion service
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.fardin.myapplication.Models.MessageStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SendNotification{

    // Sets an ID for the notification, so it can be updated.

    Context context;

    int notifyID = 1;

    String CHANNEL_ID = "ny_channel_01";// The id of the channel.

    public SendNotification(Context context) {

        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotification(String getSenderName,String getMessage) {

        CharSequence name = "My Channel";// The user-visible

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(mChannel);

// Create a notification and set the notification channel. SendNotification notification = new SendNotification. Builder(context)

        Notification notification = new Notification.Builder(context)
                .setContentTitle(getSenderName)
                .setContentText(getMessage)
                .setSmallIcon(android.R.drawable.btn_star_big_off)
                .setChannelId(CHANNEL_ID)
                .build();

// Issue the notification.
        mNotificationManager.notify(notifyID, notification);

    }



//    FirebaseDatabase database;
//    ArrayList<DataSnapshot> chatRooms;
//    String senderUid;
//
//
//    public void sendNotification(Context context, String msg) {
//        NotificationCompat.Builder Builder = new NotificationCompat.Builder(context);
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        senderUid = FirebaseAuth.getInstance().getUid();
//        chatRooms =  new ArrayList<>();
//        database.getInstance().getReference("chats")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            if (snapshot.getKey().indexOf(senderUid) != -1 ? true : false) {
//                                String reciverUid = String.valueOf(snapshot.getKey().replace(senderUid, ""));
//                                String reciverRoom = reciverUid + senderUid ;
//                                Log.i("fire", reciverRoom);
//                                database.getInstance().getReference()
//                                        .child(reciverRoom)
//                                        .child("messages")
//                                        .addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                                for (DataSnapshot snapshot1 :dataSnapshot.getChildren()){
//
//                                                    MessageStatus status = snapshot1.child("status").getValue(MessageStatus.class);
//                                                    Log.i("fire",String.valueOf(status.isSeen())+"seen");
//                                                    Log.i("fire",String.valueOf(status.isSend())+"send");
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        });
//
//                            }
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                    }
//                });
//        return START_STICKY;
//    }
}



//public class NotificationClass {
//
//// Sets an ID for the notification, so it can be updated.
//
//    Context context;
//
//    int notifyID = 1;
//
//    String CHANNEL_ID = "ny_channel_01";// The id of the channel.
//
//    public NotificationClass(Context context) {
//
//        this.context = context;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void createNotification() {
//
//        CharSequence name = "My Channel";// The user-visible
//
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
//        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.createNotificationChannel(mChannel);
//
//// Create a notification and set the notification channel. SendNotification notification = new SendNotification. Builder(context)
//
//        Notification notification = new Notification.Builder(context)
//                .setContentTitle("New Message")
//                .setContentText("You've received new messages.")
//                .setSmallIcon(android.R.drawable.btn_star_big_off)
//                .setChannelId(CHANNEL_ID)
//                .build();
//
//// Issue the notification.
//        mNotificationManager.notify(notifyID, notification);
//
//    }
//}