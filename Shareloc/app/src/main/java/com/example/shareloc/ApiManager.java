package com.example.shareloc;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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

    public void addFriend(String userId, final String friendId, final OnFriendAddedListener listener) {
        usersRef.child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    usersRef.child(userId).child("friends").push().setValue(friendId);
                    if (listener != null) {
                        listener.onFriendAdded(true);
                    }
                } else {
                    if (listener != null) {
                        listener.onFriendAdded(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (listener != null) {
                    listener.onFriendAdded(false);
                }
            }
        });
    }

    public void removeFriend(String userId, String friendId) {
        usersRef.child(userId).child("friends").child(friendId).removeValue();
    }

    public void getFriends(String userId, ValueEventListener listener) {
        usersRef.child(userId).child("friends").addListenerForSingleValueEvent(listener);
    }

    public interface OnFriendAddedListener {
        void onFriendAdded(boolean success);
    }
}
