package com.fardin.myapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.fardin.myapplication.Adapters.MessagesAdapter;
import com.fardin.myapplication.Class.UserState;
import com.fardin.myapplication.Models.Message;
import com.fardin.myapplication.Models.MessageStatus;
import com.fardin.myapplication.Models.User;
import com.fardin.myapplication.R;
import com.fardin.myapplication.databinding.ActivityChatScreenBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatScreenActivity extends AppCompatActivity {

    ActivityChatScreenBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;
    String messageAfterDecrypt;
    String encryptedMsg = "";
    String encryptedMsg1 = "";
    String senderRoom , receiverRoom;

    FirebaseDatabase database;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        UserState.updateUserState("onCreate chatScreen");


        String name = getIntent().getStringExtra("name");
        String receiverUid = getIntent().getStringExtra("uid");
        String profile = getIntent().getStringExtra("profile");

        database = FirebaseDatabase.getInstance();
        final String senderUid = FirebaseAuth.getInstance().getUid();
        senderRoom = senderUid+receiverUid;
        receiverRoom = receiverUid+senderUid;

        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this,messages,senderRoom,receiverRoom);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        Glide.with(this).load(profile)
                .placeholder(R.drawable.avatar)
                .into(binding.profile);

        binding.username.setText(name);

        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);



        database.getReference().child("users")
                .child(receiverUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        binding.lastSeen.setText(user.getState());
                        binding.lastTime.setText(user.getTime()+" "+user.getDate());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messages.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            Message message = dataSnapshot1.getValue(Message.class);
                            Log.i("fire",dataSnapshot1.getValue().toString());
                            message.setMessageID(dataSnapshot1.getKey());

//                            database.getReference()
//                                    .child("chats")
//                                    .child(senderRoom)
//                                    .child("messages")
//                                    .child(message.getMessageID())
//                                    .child("status")
//                                    .addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            MessageStatus messageStatus = dataSnapshot.getValue(MessageStatus.class);
//                                            Message message1= message;
//                                            if(dataSnapshot.exists()) {
//                                                try {
//
//                                                    encryptedMsg1 = AESCrypt.encrypt(message1.getMessageID(), message1.getMessage());
//                                                }catch (GeneralSecurityException e){
//                                                    //handle error
//                                                }
//
//                                                message1.setMessage(encryptedMsg1);
//
//                                                /*todo prevous msg get encrpted after new msg get send. and after seen that new msg
//                                                        the every thing get to normal.and this occure after this encryption block
//                                                        that is in this comment*/
//                                                if (!messageStatus.isSeen()) {
//                                                    database.getReference().child("chats")
//                                                            .child(senderRoom)
//                                                            .child("unseen")
//                                                            .child(message1.getMessageID())
//                                                            .setValue(message1);
//                                                } else {
//                                                    database.getReference().child("chats")
//                                                            .child(senderRoom)
//                                                            .child("unseen")
//                                                            .child(message1.getMessageID())
//                                                            .removeValue();
//                                                }
//                                            }
//                                        }
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        }
//                                    });
//

                            if (message.getMessage()!=null && message.getMessageID()!=null) {
                                try {
                                    messageAfterDecrypt = AESCrypt.decrypt(message.getMessageID(), message.getMessage());
                                } catch (GeneralSecurityException e) {}
                                message.setMessage(messageAfterDecrypt);
                            }
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();

                        if (adapter.getItemCount() >= 1) {
                            binding.recyclerView.getLayoutManager().smoothScrollToPosition(binding.recyclerView, null, adapter.getItemCount() - 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatScreenActivity.this,DashBoardActivity.class));
                finishAffinity();
            }
        });
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String randomKey = database.getReference().push().getKey();
                String messageTxt = binding.messageBox.getText().toString();

                try {
                    encryptedMsg = AESCrypt.encrypt(randomKey, messageTxt);
                }catch (GeneralSecurityException e){
                    //handle error
                }

                Date date = new Date();
                final Message message = new Message(encryptedMsg, senderUid, date.getTime());
                binding.messageBox.setText("");

                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg", message.getMessage());
                lastMsgObj.put("lastMsgTime", date.getTime());
                lastMsgObj.put("MessageID", randomKey);

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);


                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .child(randomKey)
                                //todo 2.14.09
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                HashMap<String, Object> msgStatus = new HashMap<>();
                                msgStatus.put("send", true);
                                msgStatus.put("seen", false);

                                database.getReference().child("chats")
                                        .child(senderRoom)
                                        .child("messages")
                                        .child(randomKey)
                                        .child("status")
                                        .updateChildren(msgStatus);
                            }
                        });
                    }
                });
            }
        });
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        UserState.updateUserState("onStop in chatScreen");
//    }
//    @Override


    @Override
    protected void onPause() {
        binding.recyclerView.setAdapter(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        binding.recyclerView.setAdapter(adapter);
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserState.updateUserState("online");
    }
//    protected void onPostResume() {
//        super.onPostResume();
//        UserState.updateUserState("test2");
//
//    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}