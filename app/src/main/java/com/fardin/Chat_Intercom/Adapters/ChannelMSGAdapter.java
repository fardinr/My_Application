package com.fardin.Chat_Intercom.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fardin.Chat_Intercom.Models.Message;
import com.fardin.Chat_Intercom.R;
import com.fardin.Chat_Intercom.databinding.ItemReceiveBinding;
import com.fardin.Chat_Intercom.databinding.RowConversationBinding;
import com.github.pgreze.reactions.PopupGravity;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChannelMSGAdapter extends RecyclerView.Adapter<ChannelMSGAdapter.MsgViewHolder> {

    Context context;
    ArrayList<Message> messages;
    String ServerUID;
    String ChannelUID;

    public ChannelMSGAdapter(Context context, ArrayList<Message> messages, String serverUID, String channelUID) {
        this.context = context;
        this.messages = messages;
        ServerUID = serverUID;
        ChannelUID = channelUID;
    }

    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false);
        return new ChannelMSGAdapter.MsgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgViewHolder holder, int position) {
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
                ChannelMSGAdapter.MsgViewHolder viewHolder = (ChannelMSGAdapter.MsgViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[posi]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                message.setFeeling(posi);

                HashMap<String,Object> updateFeeling = new HashMap<>();
                updateFeeling.put("feeling",message.getFeeling());
                FirebaseDatabase.getInstance().getReference()
                        .child("servers")
                        .child(ServerUID)
                        .child("channels")
                        .child(ChannelUID)
                        .child("messages")
                        .child(message.getMessageID()).updateChildren(updateFeeling);
                return true; // true is closing popup, false is requesting a new selection
            }else{
                return true;
            }
        });

        ChannelMSGAdapter.MsgViewHolder viewHolder = (ChannelMSGAdapter.MsgViewHolder) holder;
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        if (message.getType().equals("image")){
            viewHolder.binding.image.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(message.getMessage())
                    .into(viewHolder.binding.image);
//                viewHolder.binding.image.setImageURI(Uri.parse(message.getMessage()));
            viewHolder.binding.message.setVisibility(View.GONE);
        }else {
            viewHolder.binding.image.setVisibility(View.GONE);
            viewHolder.binding.message.setVisibility(View.VISIBLE);
            viewHolder.binding.message.setText(message.getMessage());
        }
        viewHolder.binding.time.setText(dateFormat.format(new Date(message.getTimestamp())));


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

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MsgViewHolder extends RecyclerView.ViewHolder {

        ItemReceiveBinding binding;

        public MsgViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }
}
