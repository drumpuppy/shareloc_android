package com.example.shareloc;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    public void getUser(String userId, ValueEventListener listener) {
        usersRef.child(userId).addListenerForSingleValueEvent(listener);
    }

    public void addFriend(String userId, String friendId) {
        usersRef.child(userId).child("friends").push().setValue(friendId);
    }


    public void removeFriend(String userId, String friendId) {
        usersRef.child(userId).child("friends").child(friendId).removeValue();
    }

    public void getFriends(String userId, ValueEventListener listener) {
        usersRef.child(userId).child("friends").addListenerForSingleValueEvent(listener);
    }

}
