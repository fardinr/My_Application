package com.fardin.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    private LottieAnimationView animationView;
    private LottieAnimationView welcomeAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animationView=findViewById(R.id.animationView);
        welcomeAnimation=findViewById(R.id.welcome);
        welcomeAnimation.setAnimation("welcome.json");
        welcomeAnimation.getMaxFrame();
        welcomeAnimation.playAnimation();

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            animationView.setAnimation("sign-up.json");
            animationView.playAnimation();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this,PhoneLoginActivity.class));
                    finish();
                }
                },3000);
        }

        else {
            animationView.setAnimation("unlock-login.json");
            animationView.playAnimation();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this,DashBoardActivity.class));
                    finish();
                }
            },3000);
        }
    }
}