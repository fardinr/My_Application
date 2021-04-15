package com.fardin.Chat_Intercom.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.fardin.Chat_Intercom.Adapters.MessagesAdapter;
import com.fardin.Chat_Intercom.Class.UserState;
import com.fardin.Chat_Intercom.Models.Message;
import com.fardin.Chat_Intercom.Models.User;
import com.fardin.Chat_Intercom.R;
import com.fardin.Chat_Intercom.databinding.ActivityChatScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class ChatScreenActivity extends AppCompatActivity {

    ActivityChatScreenBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;
    String messageAfterDecrypt;
    String encryptedMsg = "";
    String encryptedMsg1 = "";
    String senderRoom , receiverRoom;
    String senderUid;

    FirebaseDatabase database;

    ArrayList<Uri> imageUris;

    private static final int PICK_IMAGES_CODE = 0;
    int position = 0;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        UserState.updateUserState("onCreate chatScreen");


        String name = getIntent().getStringExtra("name");
        String receiverUid = getIntent().getStringExtra("uid");
        String profile = getIntent().getStringExtra("profile");

        database = FirebaseDatabase.getInstance();
        senderUid = FirebaseAuth.getInstance().getUid();
        senderRoom = senderUid+receiverUid;
        receiverRoom = receiverUid+senderUid;

        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this,messages,senderRoom,receiverRoom);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        imageUris =new ArrayList<>();

        Glide.with(this).load(profile)
                .placeholder(R.drawable.avatar)
                .into(binding.profile);

        binding.username.setText(name);

        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);



        database.getReference().child("users")
                .child(receiverUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        binding.lastSeen.setText(user.getState());
                        binding.lastTime.setText(user.getTime()+" "+user.getDate());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messages.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            Message message = dataSnapshot1.getValue(Message.class);
                            Log.i("fire",dataSnapshot1.getValue().toString());
                            message.setMessageID(dataSnapshot1.getKey());

//                            database.getReference()
//                                    .child("chats")
//                                    .child(senderRoom)
//                                    .child("messages")
//                                    .child(message.getMessageID())
//                                    .child("status")
//                                    .addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            MessageStatus messageStatus = dataSnapshot.getValue(MessageStatus.class);
//                                            Message message1= message;
//                                            if(dataSnapshot.exists()) {
//                                                try {
//
//                                                    encryptedMsg1 = AESCrypt.encrypt(message1.getMessageID(), message1.getMessage());
//                                                }catch (GeneralSecurityException e){
//                                                    //handle error
//                                                }
//
//                                                message1.setMessage(encryptedMsg1);
//
//                                                /*todo prevous msg get encrpted after new msg get send. and after seen that new msg
//                                                        the every thing get to normal.and this occure after this encryption block
//                                                        that is in this comment*/
//                                                if (!messageStatus.isSeen()) {
//                                                    database.getReference().child("chats")
//                                                            .child(senderRoom)
//                                                            .child("unseen")
//                                                            .child(message1.getMessageID())
//                                                            .setValue(message1);
//                                                } else {
//                                                    database.getReference().child("chats")
//                                                            .child(senderRoom)
//                                                            .child("unseen")
//                                                            .child(message1.getMessageID())
//                                                            .removeValue();
//                                                }
//                                            }
//                                        }
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        }
//                                    });
//

                            if (message.getMessage()!=null && message.getMessageID()!=null) {
                                try {
                                    messageAfterDecrypt = AESCrypt.decrypt(message.getMessageID(), message.getMessage());
                                } catch (GeneralSecurityException e) {}
                                message.setMessage(messageAfterDecrypt);
                            }
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();

                        if (adapter.getItemCount() >= 1) {
                            binding.recyclerView.getLayoutManager().smoothScrollToPosition(binding.recyclerView, null, adapter.getItemCount() - 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatScreenActivity.this,DashBoardActivity.class));
                finishAffinity();
            }
        });

        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImagesIntent();
            }
        });

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt = binding.messageBox.getText().toString();
                binding.messageBox.setText("");

                sendMSG(messageTxt,"message");

            }
        });


        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    public  void sendMSG(String messageTXT,String type){
        String randomKey = database.getReference().push().getKey();
        Date date = new Date();

        try {
            encryptedMsg = AESCrypt.encrypt(randomKey, messageTXT);
        }catch (GeneralSecurityException e){
            //handle error
        }
        final Message message = new Message(type,encryptedMsg, senderUid, date.getTime());


        HashMap<String, Object> lastMsgObj = new HashMap<>();
        lastMsgObj.put("lastMsg", message.getMessage());
        lastMsgObj.put("lastMsgTime", date.getTime());
        lastMsgObj.put("MessageID", randomKey);
        lastMsgObj.put("type", type);

        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);


        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .child(randomKey)
                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                database.getReference().child("chats")
                        .child(receiverRoom)
                        .child("messages")
                        .child(randomKey)
                        //todo 2.14.09
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        HashMap<String, Object> msgStatus = new HashMap<>();
                        msgStatus.put("send", true);
                        msgStatus.put("seen", false);

                        database.getReference().child("chats")
                                .child(senderRoom)
                                .child("messages")
                                .child(randomKey)
                                .child("status")
                                .updateChildren(msgStatus);
                    }
                });
            }
        });
    }
    public void pickImagesIntent(){
        Intent intent= new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE , true);
        startActivityForResult(Intent.createChooser(intent,"select Images"),PICK_IMAGES_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGES_CODE){

            if (resultCode == Activity.RESULT_OK){
                if (data.getClipData() != null){

                    int cout = data.getClipData().getItemCount();
                    for (int i =0; i<cout;i++){
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
//                        imageUris.add(imageUri);
                        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                                .child("chats")
                                .child(Objects.requireNonNull(database.getReference().push().getKey()));

                        reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Date date = new Date();
                                            sendMSG(uri.toString(),"image");
                                        }
                                    });
                                }
                            }
                        });
                    }
//                    imageUris.setImageURI(imageEris.get(0));
                }else {
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);
                    final StorageReference reference = FirebaseStorage.getInstance().getReference()
                            .child("chats")
                            .child(Objects.requireNonNull(database.getReference().push().getKey()));

                    reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Date date = new Date();
                                        sendMSG(uri.toString(),"image");
                                    }
                                });
                            }
                        }
                    });

                }
            }
        }
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        UserState.updateUserState("onStop in chatScreen");
//    }
//    @Override


    @Override
    protected void onPause() {
        binding.recyclerView.setAdapter(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        binding.recyclerView.setAdapter(adapter);
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserState.updateUserState("online");
    }
//    protected void onPostResume() {
//        super.onPostResume();
//        UserState.updateUserState("test2");
//
//    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}