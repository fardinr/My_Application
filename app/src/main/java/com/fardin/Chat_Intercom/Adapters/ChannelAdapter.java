package com.fardin.Chat_Intercom.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fardin.Chat_Intercom.Activities.ChannelChatActivity;
import com.fardin.Chat_Intercom.Models.Channel;
import com.fardin.Chat_Intercom.R;
import com.fardin.Chat_Intercom.databinding.RowConversationBinding;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.ArrayList;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {
    Context context;
    ArrayList<Channel> channels;
    String serverUID;

    public ChannelAdapter(Context context, ArrayList<Channel> channels,String serverUID) {
        this.context = context;
        this.channels = channels;
        this.serverUID = serverUID;
    }

    @NonNull
    @Override
    public ChannelAdapter.ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation, parent, false);

        return new ChannelAdapter.ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChannelAdapter.ChannelViewHolder holder, int position) {
        final Channel channel = channels.get(position);

//        FirebaseDatabase.getInstance().getReference()
//                .child("server")
//                .child(serverUID)
//                .child("Channels")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.exists()) {
//                            String channelName = snapshot.getValue(String.class);
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
//                            holder.binding.msgTime.setText(dateFormat.format(new Date(time)));
//                            holder.binding.lastMsg.setText(messageAfterDecrypt);
//                        } else {
//                            holder.binding.lastMsg.setText("Tap to chat");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

        holder.binding.username.setText(channel.getChannelName());

        Glide.with(context).load(R.drawable.ic_document)
                .into(holder.binding.profile);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ChannelChatActivity.class);
                intent.putExtra("name", channel.getChannelName());
                intent.putExtra("serverUID", serverUID);
                intent.putExtra("channelUID", channel.getChannelUID());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public class ChannelViewHolder extends RecyclerView.ViewHolder {

        RowConversationBinding binding;

        public ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
