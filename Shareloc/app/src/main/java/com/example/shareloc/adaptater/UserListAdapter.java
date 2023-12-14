package com.example.shareloc.adaptater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.shareloc.Class.User;
import com.example.shareloc.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;



public class UserListAdapter extends ArrayAdapter<User> {
    private Context context;
    private List<User> users;
    private String currentUserId;
    private List<String> friendList;

    public UserListAdapter(Context context, List<User> users, String currentUserId, List<String> friendList) {
        super(context, R.layout.friend_list_item, users);
        this.context = context;
        this.users = users;
        this.currentUserId = currentUserId;
        this.friendList = friendList;
    }
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("ViewHolder") View listItemView = inflater.inflate(R.layout.friend_list_item, parent, false);

        TextView tvUsername = listItemView.findViewById(R.id.tvUsername);
        Button btnFollow = listItemView.findViewById(R.id.btnFollow);
        Button btnUnfollow = listItemView.findViewById(R.id.btnUnfollow);

        User user = users.get(position);
        tvUsername.setText(user.getUsername());
        boolean isFollowed = checkIfUserIsFollowed(user.getUsername());
        btnFollow.setVisibility(isFollowed ? View.GONE : View.VISIBLE);
        btnUnfollow.setVisibility(isFollowed ? View.VISIBLE : View.GONE);

        btnFollow.setOnClickListener(view -> handleFollow(user.getUsername()));
        btnUnfollow.setOnClickListener(view -> handleUnfollow(user.getUsername()));

        return listItemView;
    }

    private void handleFollow(String username) {
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        currentUserRef.child("friendList").push().setValue(username)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Followed " + username, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to follow " + username, Toast.LENGTH_SHORT).show());
    }

    private void handleUnfollow(String username) {
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        currentUserRef.child("friendList").orderByValue().equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    childSnapshot.getRef().removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(context, "Unfollowed " + username, Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to unfollow " + username, Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkIfUserIsFollowed(String username) {
        return friendList.contains(username);
    }
}
