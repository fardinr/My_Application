package com.fardin.Chat_Intercom.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fardin.Chat_Intercom.R;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_activity);
    }
    public void sendOtpPress(View view){
        Toast.makeText(SignUpActivity.this, "OTP send", Toast.LENGTH_SHORT).show();
    }
    public void signUpPress(View view){
        Toast.makeText(SignUpActivity.this, "Sign UP Sccessfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignUpActivity.this,DashBoardActivity.class);
        startActivity(intent);
    }
    public void resendPress(View view){
        Toast.makeText(SignUpActivity.this, "Wait.... Resending", Toast.LENGTH_SHORT).show();
    }
}