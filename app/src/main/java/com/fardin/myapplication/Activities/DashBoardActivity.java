package com.fardin.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fardin.myapplication.Class.UserState;
import com.fardin.myapplication.R;
import com.fardin.myapplication.Models.User;
import com.fardin.myapplication.Adapters.UsersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DashBoardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        recyclerView = findViewById(R.id.recyclerView);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        users = new ArrayList<>();

        usersAdapter = new UsersAdapter(this,users);
        recyclerView.setAdapter(usersAdapter);


        UserState.updateUserState("in onCreate of dashboard");

    database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    if (user.getUid().equals(auth.getUid().toString())){
                        continue;
                    }else {
                        users.add(user);
                    }
                }

                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UserState.updateUserState("offline");
    }

    @Override
    protected void onPause() {
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
        switch (item.getItemId()){
            case R.id.search:
                Toast.makeText(this,"search clicked.",Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this,"settings clicked.",Toast.LENGTH_SHORT).show();
                break;
            case R.id.invite:
                    Toast.makeText(this,"invite clicked.",Toast.LENGTH_SHORT).show();
                    break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}