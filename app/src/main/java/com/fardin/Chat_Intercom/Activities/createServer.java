package com.fardin.Chat_Intercom.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.fardin.Chat_Intercom.Models.Server;
import com.fardin.Chat_Intercom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class createServer extends AppCompatActivity {
    ProgressDialog dialog;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;
    public Button continueBtn;
    private ImageView img;
    EditText nameBox;
    String randomKey;
    HashMap<String , Object> join = new HashMap<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_server);
        getSupportActionBar().hide();

        continueBtn = findViewById(R.id.continueBtn);
        img = findViewById(R.id.imageView);
        nameBox = findViewById(R.id.nameBox);


        join.put(FirebaseAuth.getInstance().getUid(),FirebaseAuth.getInstance().getUid());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating Server...");
        dialog.setCancelable(false);

        randomKey = database.getReference().push().getKey();


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,45);

            }
        });


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameBox.getText().toString();

                if(name.isEmpty()) {
                    nameBox.setError("Please type a name");
                    return;
                }

                if(selectedImage!=null){
                    dialog.show();
                    final StorageReference reference = storage.getReference().child("Servers").child(randomKey);
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        String imageUrl = uri.toString();
                                        String serverUID = randomKey;
                                        String owner = auth.getUid();
                                        String name = nameBox.getText().toString();


                                        Server server = new Server(serverUID, name, owner, imageUrl);

                                        database.getReference()
//                                                .child(owner)
                                                .child("servers")
                                                .child(serverUID)
                                                .setValue(server)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        database.getReference()
                                                                .child("servers")
                                                                .child(serverUID)
                                                                .child("members")
                                                                .setValue(join).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                dialog.dismiss();
                                                                Intent intent = new Intent(createServer.this, DashBoardActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });

                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                } else {
                    String serverUID = randomKey;
                    String owner = auth.getUid();

                    Server server = new Server(serverUID, name, owner, "No Image");

                    database.getReference()
                            .child("servers")
                            .child(serverUID)
                            .setValue(server)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    database.getReference()
                                            .child("servers")
                                            .child(serverUID)
                                            .child("members")
                                            .setValue(join).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(createServer.this, DashBoardActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                                }
                            });

                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null){
            if(data.getData() != null){
                img.setImageURI(data.getData());
                selectedImage=data.getData();
            }
        }
    }
    }