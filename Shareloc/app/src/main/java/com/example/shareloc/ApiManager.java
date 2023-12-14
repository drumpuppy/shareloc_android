package com.example.shareloc;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
}
