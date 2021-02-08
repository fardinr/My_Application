package com.fardin.myapplication.Class;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UserState {
    //online offline user status updating in firebase here

    static public void updateUserState(String state){
        String time,date;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        time = currentTime.format(calendar.getTime());
        SimpleDateFormat currentDate = new SimpleDateFormat("mm dd ,yyyy");
        date = currentDate.format(calendar.getTime());

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("time",time);
        onlineState.put("date",date);
        onlineState.put("state",state);

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .updateChildren(onlineState);
    }
}
