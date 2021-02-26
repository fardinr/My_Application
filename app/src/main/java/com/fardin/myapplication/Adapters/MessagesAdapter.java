package com.fardin.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fardin.myapplication.Models.Message;
import com.fardin.myapplication.Models.MessageStatus;
import com.fardin.myapplication.R;
import com.fardin.myapplication.databinding.ItemReceiveBinding;
import com.fardin.myapplication.databinding.ItemSentBinding;
import com.github.pgreze.reactions.PopupGravity;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessagesAdapter extends RecyclerView.Adapter{


    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;

    String senderRoom;
    String receiverRoom;

    public MessagesAdapter(Context context, ArrayList<Message> messages,String senderRoom,String receiverRoom){
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;

    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent,parent,false);
            return new SentViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderID())){
            return  ITEM_SENT;
        }else{
            return ITEM_RECEIVE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);



        int reactions[] = new int[] {
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .withPopupGravity(PopupGravity.PARENT_RIGHT)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (posi) -> {
            if (posi !=-1) {
                if (holder.getClass() == SentViewHolder.class) {
                    SentViewHolder viewHolder = (SentViewHolder) holder;
                    viewHolder.binding.feeling.setImageResource(reactions[posi]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                    message.setFeeling(posi);
                } else {
                    ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
                    viewHolder.binding.feeling.setImageResource(reactions[posi]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                    message.setFeeling(posi);
                }
                HashMap<String,Object> updateFeeling = new HashMap<>();
                updateFeeling.put("feeling",message.getFeeling());
                FirebaseDatabase.getInstance().getReference()
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.getMessageID()).updateChildren(updateFeeling);
                FirebaseDatabase.getInstance().getReference()
                        .child("chats")
                        .child(receiverRoom)
                        .child("messages")
                        .child(message.getMessageID()).updateChildren(updateFeeling);
                return true; // true is closing popup, false is requesting a new selection
            }else{
                return true;
            }
        });


        if(holder.getClass() == SentViewHolder.class){
            SentViewHolder viewHolder = (SentViewHolder) holder;
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            viewHolder.binding.message.setText(message.getMessage());
            viewHolder.binding.time.setText(dateFormat.format(new Date(message.getTimestamp())));

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageID())
                    .child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    MessageStatus messageStatus = dataSnapshot.getValue(MessageStatus.class);
                    if(dataSnapshot.exists()) {
                        if (messageStatus.isSend()){
                            viewHolder.binding.msgStatus.setText("Send");
                            if (messageStatus.isSeen()){
                                viewHolder.binding.msgStatus.setText("Send, Seen");
                            }
                        } else {
                            viewHolder.binding.msgStatus.setText("");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });





            if (message.getFeeling()>=0){
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
        }else{
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            viewHolder.binding.message.setText(message.getMessage());
            viewHolder.binding.time.setText(dateFormat.format(new Date(message.getTimestamp())));

            HashMap<String, Object> msgStatus = new HashMap<>();
            msgStatus.put("seen", true);
            FirebaseDatabase.getInstance().getReference().child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageID())
                    .child("status")
                    .updateChildren(msgStatus);

            if (message.getFeeling()>=0){
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }
            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder{

        ItemSentBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        ItemReceiveBinding binding;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }

}
