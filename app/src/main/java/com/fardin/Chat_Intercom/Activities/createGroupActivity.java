package com.fardin.Chat_Intercom.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fardin.Chat_Intercom.R;

public class createGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
    }
    public void createGroup(View view){
        Toast.makeText(createGroupActivity.this, "Group Created", Toast.LENGTH_SHORT).show();
    }
}