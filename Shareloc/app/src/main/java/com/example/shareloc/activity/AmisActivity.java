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
import androidx.annotation.Nullable;

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
    private static final int REQUEST_CODE = 1;
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

        setupAdapter();
        loadFriends();

        friendIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {loadUsernames(charSequence.toString());}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        ImageView backButton = findViewById(R.id.loupe);
        backButton.setOnClickListener(view -> openSearchActivity());
    }

    private void setupAdapter() {
        adapter = new UserListAdapter(this, new ArrayList<>(), currentUserId);
        adapter.setOnDataChangeListener(() -> loadFriends());
        friendsListView.setAdapter(adapter);
    }

    private void openSearchActivity() {
        Intent intent = new Intent(AmisActivity.this, SearchFriendActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            loadFriends();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFriends();
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
                adapter.updateData(allUsers);
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
                        friend.setUserId(dataSnapshot.getKey());
                        allUsers.add(friend);
                    }

                    if (allUsers.size() == friendIds.size()) {
                        updateListView();
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
        adapter.updateData(allUsers);
    }

    private void loadUsernames(String searchTerm) {
        searchTerm = searchTerm.toLowerCase();
        List<User> filteredUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getUsername().toLowerCase().contains(searchTerm)) {
                filteredUsers.add(user);
            }
        }
        adapter.updateData(filteredUsers);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.amis_page;
    }
}
