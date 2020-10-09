package com.fardin.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;

public class DashBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
    }

    public void onClickLogout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(DashBoardActivity.this,PhoneLoginActivity.class));
        finish();
    }
}