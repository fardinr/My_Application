package com.fardin.Chat_Intercom.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.fardin.Chat_Intercom.Models.Server;
import com.fardin.Chat_Intercom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private LottieAnimationView animationView;
    private LottieAnimationView welcomeAnimation;
    FirebaseAuth auth;
    String[] temp;
    String serverUID;
    Dialog dialog;
    TextView close;
    ImageView serverImage;
    TextView serverName;
    TextView memberCount;
    Button join;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new Dialog(this);

        getSupportActionBar().hide();

        auth= FirebaseAuth.getInstance();
        animationView=findViewById(R.id.animationView);
        welcomeAnimation=findViewById(R.id.welcome);

//        goNext();

        // for handleing the invite code

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            String referLink = deepLink.toString();
                         // https://serverinvite.page.link/?serveruid=-MWmIF3PWB_jIxmzmXT3

                            temp = referLink.split("=");
                            // temp[1] = serverUID

                            serverUID = temp[1];
                            Log.e("main","deep link = "+deepLink);
                            Log.e("main","split link = "+ Arrays.toString(temp));
                            Log.e("main","split link = "+ temp[1]);


                            joinServer();
                        }
                        else {
                            goNext();
                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("main", "getDynamicLink:onFailure", e);
                    }
                });

    }

    private void goNext() {

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

    private void joinServer () {

        dialog.setContentView(R.layout.join_server);
        close = dialog.findViewById(R.id.close);
        join = dialog.findViewById(R.id.join);
        serverImage = dialog.findViewById(R.id.serverImage);
        serverName = dialog.findViewById(R.id.serverName);
        memberCount = dialog.findViewById(R.id.memberCount);

        FirebaseDatabase.getInstance().getReference()
                .child("servers")
                .child(serverUID)
                .child("members").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                memberCount.setText((String.valueOf(dataSnapshot.getChildrenCount())));
            }
        });
        FirebaseDatabase.getInstance().getReference()
                .child("servers")
                .child(serverUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
//                    Log.e("firebase", "Error getting data", dataSnapshot.getValue().toString());
                        }
                        else {
                            Server server = snapshot.getValue(Server.class);
                            Glide.with(dialog.getContext()).load(server.getProfileImage()).placeholder(R.drawable.avatar).into(serverImage);
                            serverName.setText(server.getName());
//                            Glide.with(dialog.getContext()).load(snapshot.getValue(String.class)).placeholder(R.drawable.avatar).into(serverImage);

//                    Log.e("firebase", "server image = " + String.valueOf(task.getResult().getValue()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                goNext();
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String , Object> join = new HashMap<>();

                join.put(FirebaseAuth.getInstance().getUid(),FirebaseAuth.getInstance().getUid());

                FirebaseDatabase.getInstance().getReference()
                        .child("servers")
                        .child(serverUID)
                        .child("members")
                        .updateChildren(join)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                goNext();
                            }
                        });

            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}