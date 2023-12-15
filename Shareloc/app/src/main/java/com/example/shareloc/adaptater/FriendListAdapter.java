package com.example.shareloc.adaptater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

public class FriendListAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> users;
    private String currentUserId;
    private List<String> friendUserIds;
    private OnDataChangeListener onDataChangeListener;

    public interface OnDataChangeListener {
        void onDataChanged();
    }

    public void setOnDataChangeListener(OnDataChangeListener listener) {
        this.onDataChangeListener = listener;
    }

    public FriendListAdapter(Context context, List<User> users, String currentUserId) {
        super(context, R.layout.friend_list_item, users);
        this.context = context;
        this.users = users;
        this.currentUserId = currentUserId;
        this.friendUserIds = new ArrayList<>();
        loadFriendUserIds();
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View listItemView = inflater.inflate(R.layout.friend_list_item, parent, false);

        TextView tvUsername = listItemView.findViewById(R.id.tvUsername);
        ImageView imgFollow = listItemView.findViewById(R.id.imgFollow);
        ImageView imgUnfollow = listItemView.findViewById(R.id.imgUnfollow);

        User user = getItem(position);
        tvUsername.setText(user.getUsername());

        if (isUserFollowed(user.getUserId())) {
            imgFollow.setVisibility(View.GONE);
            imgUnfollow.setVisibility(View.VISIBLE);
        } else {
            imgFollow.setVisibility(View.VISIBLE);
            imgUnfollow.setVisibility(View.GONE);
        }

        imgFollow.setOnClickListener(view -> handleFollow(user.getUserId()));
        imgUnfollow.setOnClickListener(view -> handleUnfollow(user.getUserId()));

        return listItemView;
    }

    private boolean isUserFollowed(String userId) {
        return friendUserIds.contains(userId);
    }

    private void loadFriendUserIds() {
        DatabaseReference friendListRef = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserId)
                .child("friendList");

        friendListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendUserIds.clear();
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    String friendUserId = friendSnapshot.getValue(String.class);
                    if (friendUserId != null) {
                        friendUserIds.add(friendUserId);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FriendListAdapter", "Error loading friend user IDs: " + databaseError.getMessage());
            }
        });
    }

    private void handleFollow(String userId) {
        DatabaseReference friendListRef = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserId)
                .child("friendList");

        friendListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean alreadyFriend = false;
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    if (friendSnapshot.getValue(String.class).equals(userId)) {
                        alreadyFriend = true;
                        break;
                    }
                }

                if (!alreadyFriend) {
                    friendListRef.push().setValue(userId)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Followed user", Toast.LENGTH_SHORT).show();
                                if (onDataChangeListener != null) {
                                    onDataChangeListener.onDataChanged();
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to follow user", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(context, "User already followed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUnfollow(String userId) {
        DatabaseReference friendListRef = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserId)
                .child("friendList");

        friendListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String keyToRemove = null;
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    if (friendSnapshot.getValue(String.class).equals(userId)) {
                        keyToRemove = friendSnapshot.getKey();
                        break;
                    }
                }

                if (keyToRemove != null) {
                    friendListRef.child(keyToRemove).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Unfollowed user", Toast.LENGTH_SHORT).show();
                                if (onDataChangeListener != null) {
                                    onDataChangeListener.onDataChanged();
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to unfollow user", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(context, "User not found in friend list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
