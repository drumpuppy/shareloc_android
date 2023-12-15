package com.example.shareloc.managers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.shareloc.Class.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ApiManager {
    public final DatabaseReference usersRef;

    public ApiManager() {
        usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public void createUser(String userId, User user) {
        usersRef.child(userId).setValue(user);
    }

    public void updateUser(String userId, User user) {
        usersRef.child(userId).setValue(user);
    }
    public void getUser(String userId, ValueEventListener listener) {
        usersRef.child(userId).addListenerForSingleValueEvent(listener);
    }

    public interface OnFriendAddedListener {
        void onFriendAdded(boolean success);
    }
}
