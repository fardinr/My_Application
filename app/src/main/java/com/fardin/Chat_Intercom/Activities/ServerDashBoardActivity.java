package com.fardin.Chat_Intercom.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fardin.Chat_Intercom.Adapters.ChannelAdapter;
import com.fardin.Chat_Intercom.Models.Channel;
import com.fardin.Chat_Intercom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ServerDashBoardActivity extends AppCompatActivity {
    RecyclerView channelList;
    RecyclerView serverlist;
    FirebaseDatabase database;
    ArrayList<Channel> channels;
    ChannelAdapter channelAdapter;
    String serverUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_dash_board);
        channelList = findViewById(R.id.channelList);
        serverlist = findViewById(R.id.serverList);

        serverUID= getIntent().getStringExtra("serverUID");
        database = FirebaseDatabase.getInstance();
        channels = new ArrayList<>();
        channelAdapter = new ChannelAdapter(this,channels,serverUID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        channelList.setLayoutManager(layoutManager);
        channelList.setAdapter(channelAdapter);

        database.getReference().child("servers").child(serverUID).child("channels").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                channels.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Channel channel = snapshot1.getValue(Channel.class);
                    channels.add(channel);
                }

                channelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //here write a code about creating a channel
    public void createChannel(View view){
        startActivity(new Intent(ServerDashBoardActivity.this, createChannelActivity.class).putExtra("serverUID",serverUID));

    }
}