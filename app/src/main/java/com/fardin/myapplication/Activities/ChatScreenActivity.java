package com.fardin.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.fardin.myapplication.Adapters.MessagesAdapter;
import com.fardin.myapplication.Class.UserState;
import com.fardin.myapplication.Models.Message;
import com.fardin.myapplication.Models.User;
import com.fardin.myapplication.R;
import com.fardin.myapplication.databinding.ActivityChatScreenBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatScreenActivity extends AppCompatActivity {

    ActivityChatScreenBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;

    String senderRoom , receiverRoom;

    FirebaseDatabase database;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


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




        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messages.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            Message message = dataSnapshot1.getValue(Message.class);
                            message.setMessageID(dataSnapshot1.getKey());
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

                String messageTxt = binding.messageBox.getText().toString();

                Date date = new Date();
                final Message message = new Message(messageTxt, senderUid, date.getTime());
                binding.messageBox.setText("");

                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg", message.getMessage());
                lastMsgObj.put("lastMsgTime", date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                String randomKey = database.getReference().push().getKey();
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

                            }
                        });
                    }
                });
            }
        });
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        UserState.updateUserState("test2");

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}