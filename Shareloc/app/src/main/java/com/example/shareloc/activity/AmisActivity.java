package com.example.shareloc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import com.example.shareloc.R;
import com.example.shareloc.Class.User;
import com.example.shareloc.adaptater.UserListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AmisActivity extends BaseActivity {
    private ListView friendsListView;
    private UserListAdapter adapter;
    private DatabaseReference friendListRef;
    private List<User> allUsers;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amis_page);

        EditText friendIdEditText = findViewById(R.id.friend_id_edit_text);
        friendsListView = findViewById(R.id.friends_list_view);
        allUsers = new ArrayList<>();
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        friendListRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("friendList");


        ImageView backButton = findViewById(R.id.loupe);
        backButton.setOnClickListener(view -> openSearchActivity());

        loadFriends();

        friendIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {loadUsernames(charSequence.toString());}
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void openSearchActivity() {
        Intent intent = new Intent(AmisActivity.this, SearchFriendActivity.class);
        startActivity(intent);
    }

    private void loadFriends() {
        friendListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUsers.clear();
                List<String> friendIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    friendIds.add(snapshot.getValue(String.class));
                }
                fetchFriendDetails(friendIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AmisActivity", "Error loading friend list: " + databaseError.getMessage());
            }
        });
    }

    private void fetchFriendDetails(List<String> friendIds) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        for (String friendId : friendIds) {
            usersRef.child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User friend = dataSnapshot.getValue(User.class);
                    if (friend != null) {
                        friend.setUserId(dataSnapshot.getKey()); // Set the user ID
                        allUsers.add(friend);
                    }

                    // Check if all friends are loaded
                    if (allUsers.size() == friendIds.size()) {
                        updateListView(); // Update the list view once all friends are loaded
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("AmisActivity", "Error fetching friend details: " + databaseError.getMessage());
                }
            });
        }
    }

    private void updateListView() {
        adapter = new UserListAdapter(this, allUsers, currentUserId);
        friendsListView.setAdapter(adapter);
    }

    private void loadUsernames(String searchTerm) {
        searchTerm = searchTerm.toLowerCase();
        List<User> filteredUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getUsername().toLowerCase().contains(searchTerm)) {
                filteredUsers.add(user);
            }
        }
        adapter = new UserListAdapter(this, filteredUsers, currentUserId);
        friendsListView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.amis_page;
    }
}