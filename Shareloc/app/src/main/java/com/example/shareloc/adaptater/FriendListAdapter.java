package com.example.shareloc.adaptater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareloc.Class.User;
import com.example.shareloc.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FriendListAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> users;
    private String currentUserId;

    public FriendListAdapter(Context context, List<User> users, String currentUserId) {
        super(context, R.layout.friend_list_item, users);
        this.context = context;
        this.users = users;
        this.currentUserId = currentUserId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View listItemView = inflater.inflate(R.layout.friend_list_item, parent, false);

        TextView tvUsername = listItemView.findViewById(R.id.tvUsername);
        Button btnFollow = listItemView.findViewById(R.id.btnFollow);
        Button btnUnfollow = listItemView.findViewById(R.id.btnUnfollow);

        User user = users.get(position);
        tvUsername.setText(user.getUsername());

        btnFollow.setOnClickListener(view -> handleFollow(user.getUsername()));
        btnUnfollow.setOnClickListener(view -> handleUnfollow(user.getUsername()));

        return listItemView;
    }

    private void handleFollow(String username) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        userRef.child("friendList").push().setValue(username)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Followed " + username, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to follow " + username, Toast.LENGTH_SHORT).show());
    }

    private void handleUnfollow(String username) {
        getFriendKey(username, new FriendKeyCallback() {
            @Override
            public void onKeyFound(String key) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
                userRef.child("friendList").child(key).removeValue()
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Unfollowed " + username, Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Failed to unfollow " + username, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFriendKey(String username, FriendKeyCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        userRef.child("friendList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String friendUsername = snapshot.getValue(String.class);
                    if (username.equals(friendUsername)) {
                        callback.onKeyFound(snapshot.getKey());
                        return;
                    }
                }
                callback.onError("Key not found");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }
}
