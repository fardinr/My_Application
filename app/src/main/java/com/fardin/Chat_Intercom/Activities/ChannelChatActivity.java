package com.fardin.Chat_Intercom.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.fardin.Chat_Intercom.Adapters.ChannelMSGAdapter;
import com.fardin.Chat_Intercom.Models.Message;
import com.fardin.Chat_Intercom.databinding.ActivityChannelChatBinding;
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

public class ChannelChatActivity extends AppCompatActivity {

    ActivityChannelChatBinding binding;
    ChannelMSGAdapter adapter;
    ArrayList<Message> messages;
    String messageAfterDecrypt;
    String encryptedMsg = "";
//    String encryptedMsg1 = "";
    String senderRoom , receiverRoom;
    FirebaseDatabase database;
    String serverUID,channelName,channelUID;

    private static final int PICK_IMAGES_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        serverUID = getIntent().getStringExtra("serverUID");
        channelName = getIntent().getStringExtra("name");
        channelUID = getIntent().getStringExtra("channelUID");

        database = FirebaseDatabase.getInstance();

        messages = new ArrayList<>();
        adapter = new ChannelMSGAdapter(this,messages,serverUID,channelUID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        binding.username.setText(channelName);

        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);


        database.getReference().child("servers")
                .child(serverUID)
                .child("channels")
                .child(channelUID)
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
                startActivity(new Intent(ChannelChatActivity.this,ServerDashBoardActivity.class));
                finishAffinity();
            }
        });


        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt = binding.messageBox.getText().toString();
                sendMSG(messageTxt,"message");
                binding.messageBox.setText("");
            }
        });

        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImagesIntent();
            }
        });

//        getSupportActionBar().setTitle(name);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
//                    imageUris.add(imageUri);
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

    private void sendMSG(String messageTxt, String type) {
        String randomKey = database.getReference().push().getKey();

        try {
            encryptedMsg = AESCrypt.encrypt(randomKey, messageTxt);
        }catch (GeneralSecurityException e){
            //handle error
        }

        Date date = new Date();
        final Message message = new Message("imaage",encryptedMsg, FirebaseAuth.getInstance().getUid(), date.getTime());


        HashMap<String, Object> lastMsgObj = new HashMap<>();
        lastMsgObj.put("lastMsg", message.getMessage());
        lastMsgObj.put("lastMsgTime", date.getTime());
        lastMsgObj.put("MessageID", randomKey);
        lastMsgObj.put("type",type);

        database.getReference().child("servers").child(serverUID).child("channels").child(channelUID).updateChildren(lastMsgObj);


        database.getReference().child("servers")
                .child(serverUID)
                .child("channels")
                .child(channelUID)
                .child("messages")
                .child(randomKey)
                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

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
//        UserState.updateUserState("online");
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