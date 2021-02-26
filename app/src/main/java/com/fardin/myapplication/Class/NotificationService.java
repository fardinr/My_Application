package com.fardin.myapplication.Class;
//  notification service class is used to check wheter any unseen msg is exist or not and
//  this is service that run bacground and startSaervice method is called at dashboard class file
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

import com.fardin.myapplication.Models.Message;
import com.fardin.myapplication.Models.MessageStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class NotificationService extends Service {
    FirebaseDatabase database;
    ArrayList<String> chatRooms;
    String senderUid;
    String messageAfterDecrypt;
    String reciverRoom;
    String reciverUid;
    Message message;
    MessageStatus messageStatus;
    SendNotification sendNotification;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        database = FirebaseDatabase.getInstance();

        senderUid = FirebaseAuth.getInstance().getUid();
        chatRooms =  new ArrayList<String>();
        database.getReference("chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getKey().indexOf(senderUid) != -1 ? true : false) {
                                reciverUid = String.valueOf(snapshot.getKey().replace(senderUid, ""));
                                reciverRoom = reciverUid + senderUid ;

                                Log.i("fire", reciverRoom);
                                FirebaseDatabase.getInstance().getReference().child("chats").child(reciverRoom).child("messages").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        Log.i("fire",dataSnapshot.toString());

                                        for (DataSnapshot snapshot1:dataSnapshot.getChildren()){
//                                            Log.i("fire",snapshot1.getValue().toString()+"----");
                                            message = snapshot1.getValue(Message.class);
                                            Log.i("fire",snapshot1.getKey().toString()+"@@@");
                                            message.setMessageID(snapshot1.getKey().toString());

                                            if (snapshot1.child("status").exists()) {
                                                Log.i("fire", snapshot1.child("status").toString() + "@@@");
//                                                Log.i("fire", snapshot1.child("status").child("seen").toString() + "@@@"+message.getMessageID());

                                                if (snapshot1.child("status").child("seen").getValue().equals(false)){
                                                    Log.i("fire", snapshot1.child("status").child("seen").toString() + "((((()))))"+message.getMessageID());

                                                    try {
                                                        messageAfterDecrypt = AESCrypt.decrypt(message.getMessageID(), message.getMessage());
                                                    } catch (GeneralSecurityException e) {}
                                                    notificaionGenrator();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

//        for (String chatRoom : chatRooms) {
//            database.getReference()
//                    .child(reciverRoom)
//                    .child("messages")
//                    .child(chatRoom)
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
//                            for (DataSnapshot snapshot2 : dataSnapshot2.getChildren()) {
//                                Log.i("fire", snapshot2.getValue().toString());
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//        }
        return START_STICKY;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void notificaionGenrator(){
        sendNotification = new SendNotification(this);
        sendNotification.createNotification(message.getMessageID(),messageAfterDecrypt);
    }
}



