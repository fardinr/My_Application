package com.fardin.Chat_Intercom.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fardin.Chat_Intercom.Adapters.TopServerAdapter;
import com.fardin.Chat_Intercom.Adapters.UsersAdapter;
import com.fardin.Chat_Intercom.Class.UserState;
import com.fardin.Chat_Intercom.Models.Server;
import com.fardin.Chat_Intercom.Models.User;
import com.fardin.Chat_Intercom.R;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;

public class DashBoardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView serverlist;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    TopServerAdapter topServerAdapter;
    ArrayList<Server> servers;
    ValueEventListener listener1;
    ValueEventListener listener2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        Slidr.attach(this);
        /*
        startService(new Intent(this, NotificationService.class));
        the send notificaion service is started by above line
        */


        recyclerView = findViewById(R.id.recyclerView);
        serverlist = findViewById(R.id.serverList);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        users = new ArrayList<>();
        servers = new ArrayList<>();

        usersAdapter = new UsersAdapter(this, users);
        recyclerView.setAdapter(usersAdapter);
        topServerAdapter = new TopServerAdapter(this,servers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        serverlist.setLayoutManager(layoutManager);
        serverlist.setAdapter(topServerAdapter);

        //todo here i need to add the bottom nevigation code and also need to create activitys
        // the comented code isd right it needs to nevigate throw navigation bar
//        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
//        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//                switch (item.getItemId()) {
//                    case R.id.chats:
////
//                        break;
//                    case R.id.Status:
//
//                        break;
//                    case R.id.Calls:
////
//                        break;
//                }
//                return false;
//            }
//        });



//        UserState.updateUserState("in onCreate of dashboard");

        listener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    assert user != null;
                    assert FirebaseAuth.getInstance().getUid() != null;
                    if (user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                        continue;
                    } else {
                        users.add(user);
                    }
                }

                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference().child("users").addValueEventListener(listener1);
        listener2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    servers.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Server server = snapshot.getValue(Server.class);
//                        server.setName(snapshot.child("name").getValue(String.class));
//                        server.setOwner("");
//                        server.setServerUID("");
//                        server.setProfileImage(snapshot.child("profileImage").getValue(String.class));

                        for (Object member :server.getMembers().values().toArray()) {

                            if (member.equals(FirebaseAuth.getInstance().getUid())) {
                                servers.add(server);
                            }
                        }

                    }
                }
                topServerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        database.getReference().child("servers").addValueEventListener(listener2);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UserState.updateUserState("offline");
    }

    @Override
    protected void onPause() {
        database.getReference().child("users").removeEventListener(listener1);
        database.getReference().child("servers").removeEventListener(listener2);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        UserState.updateUserState("onStop of dasboard");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserState.updateUserState("offline");
    }

    //
    @Override
    protected void onResume() {
        super.onResume();
        UserState.updateUserState("online");

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(this, "search clicked.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "settings clicked.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.invite:
                Toast.makeText(this, "invite clicked.", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void createGroup(View view) {
        startActivity(new Intent(DashBoardActivity.this,createServer.class));
    }
}
