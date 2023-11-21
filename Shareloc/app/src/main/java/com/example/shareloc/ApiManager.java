package com.example.shareloc;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ApiManager {
    private final DatabaseReference usersRef;

    public ApiManager() {
        usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public void createUser(String userId, User user) {
        usersRef.child(userId).setValue(user);
    }

    public void updateUser(String userId, User user) {
        usersRef.child(userId).setValue(user);
    }

    // ... other methods ...
}
