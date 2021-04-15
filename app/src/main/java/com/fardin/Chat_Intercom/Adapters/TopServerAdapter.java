package com.fardin.Chat_Intercom.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fardin.Chat_Intercom.Activities.DashBoardActivity;
import com.fardin.Chat_Intercom.Models.Server;
import com.fardin.Chat_Intercom.R;
import com.fardin.Chat_Intercom.Activities.ServerDashBoardActivity;
import com.fardin.Chat_Intercom.databinding.ItemServerBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.ArrayList;

public class TopServerAdapter extends RecyclerView.Adapter<TopServerAdapter.TopSereverViewHonlder> {
    Context context;
    ArrayList<Server> Servers;
    Server servers;

    public TopServerAdapter(Context context, ArrayList<Server> servers) {
        this.context = context;
        this.Servers = servers;
    }

    @NonNull
    @Override
    public TopSereverViewHonlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_server,parent,false);
        return new TopSereverViewHonlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopSereverViewHonlder holder, int position) {
        servers = Servers.get(position);
        for (Object member :servers.getMembers().values().toArray()){

            if (member.equals(FirebaseAuth.getInstance().getUid())){
                Glide.with(context).load(servers.getProfileImage()).placeholder(R.drawable.avatar).into(holder.binding.image);
                holder.binding.name.setText(servers.getName());

                holder.binding.circularStatusView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
//                createReferLink(servers.getServerUID());
                        cretaeLink();
                        return false;
                    }
                });

                holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                Toast.makeText(context,servers.getServerUID(),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, ServerDashBoardActivity.class).putExtra("serverUID",servers.getServerUID());
                        context.startActivity(intent);
                    }
                });

            }
        }

    }

    private void cretaeLink() {
        Uri baseUrl = Uri.parse("https://serverinvite.page.link/?serveruid="+servers.getServerUID());
        String domain = "https://chatintercom.page.link";
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(baseUrl)
                .setDomainUriPrefix(domain)

                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())

                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.e("main", " link is "+dynamicLinkUri.toString() );
//        https://chatintercom.page.link?apn=com.fardin.Chat_Intercom&ibi=com.example.ios&link=
        createReferLink(dynamicLinkUri.toString());

    }

    private void createReferLink(String dynamicLinkUri) {
        // shorten the link
        // https://chatintercom.page.link?apn=com.fardin.Chat_Intercom&link=https%3A%2F%2Fserverinvite.page.link


        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(dynamicLinkUri))
                .buildShortDynamicLink()
                .addOnCompleteListener((Activity) context, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.e("main ", "short link "+ shortLink.toString());
                            Log.e("main ", "flowchartLink "+ flowchartLink.toString());

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());

                            context.startActivity(Intent.createChooser(intent, "Share Link"));
                        } else {
                            // Error
                            // ...
                            Log.e("main", " error "+task.getException() );
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return Servers.size();
    }

    public class TopSereverViewHonlder extends RecyclerView.ViewHolder{

        ItemServerBinding binding;

        public TopSereverViewHonlder(@NonNull View itemView) {
            super(itemView);
            binding = ItemServerBinding.bind(itemView);
        }
    }
}
