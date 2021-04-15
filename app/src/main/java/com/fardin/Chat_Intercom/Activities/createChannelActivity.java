package com.fardin.Chat_Intercom.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fardin.Chat_Intercom.Models.Channel;
import com.fardin.Chat_Intercom.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class createChannelActivity extends AppCompatActivity {

    ProgressDialog dialog;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;
    public Button continueBtn;
    EditText nameBox;
    String serverUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);
        getSupportActionBar().hide();

        serverUID = getIntent().getStringExtra("serverUID");
        continueBtn = findViewById(R.id.continueBtn);
        nameBox = findViewById(R.id.nameBox);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating Server...");
        dialog.setCancelable(false);

        String randomKey = database.getReference().push().getKey();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameBox.getText().toString();

                if(name.isEmpty()) {
                    nameBox.setError("Please type a name");
                    return;
                }
                String owner = auth.getUid();

                Channel channel = new Channel(name,randomKey);

                Toast.makeText(createChannelActivity.this,serverUID,Toast.LENGTH_LONG).show();
                database.getReference()
                        .child("servers")
//                        .child(owner)
                        .child(serverUID)
                        .child("channels")
                        .child(randomKey)
                        .setValue(channel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
//                                Intent intent = new Intent(createChannelActivity.this, ServerDashBoardActivity.class).putExtra("serverUID",serverUID);
//                                startActivity(intent);
                                finish();
                            }
                        });

            }
        });

    }
}