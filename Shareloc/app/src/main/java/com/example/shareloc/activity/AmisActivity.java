package com.example.shareloc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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

    private EditText friendIdEditText;
    private ListView friendsListView;
    private UserListAdapter adapter;
    private DatabaseReference usersRef;
    private List<User> allUsers;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amis_page);

        friendIdEditText = findViewById(R.id.friend_id_edit_text);
        friendsListView = findViewById(R.id.friends_list_view);
        allUsers = new ArrayList<>();
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Load friends
        loadFriends();

        // Setup search functionality
        friendIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadUsernames(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup add friend button
        ImageView backButton = findViewById(R.id.loupe);
        backButton.setOnClickListener(view -> openSearchActivity());
    }

    private void openSearchActivity() {
        Intent intent = new Intent(AmisActivity.this, SearchFriendActivity.class);
        startActivity(intent);
    }

    private void loadFriends() {
        DatabaseReference currentUserRef = usersRef.child(currentUserId).child("friendList");
        Log.d("AmisActivity", "Loading friends for user: " + currentUserId);

        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d("AmisActivity", "No friends found in friendList for user: " + currentUserId);
                    return;
                }

                allUsers.clear();
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    String friendUserId = friendSnapshot.getKey();
                    Log.d("AmisActivity", "Friend ID found: " + friendUserId);

                    DatabaseReference friendRef = usersRef.child(friendUserId);
                    friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User friend = dataSnapshot.getValue(User.class);
                            if (friend != null) {
                                allUsers.add(friend);
                                Log.d("AmisActivity", "Added friend: " + friend.getUsername());
                            } else {
                                Log.d("AmisActivity", "Friend data is null for ID: " + friendUserId);
                            }
                            updateListView();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("AmisActivity", "Error fetching friend details: " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AmisActivity", "Error fetching friends list: " + databaseError.getMessage());
            }
        });
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

    private void updateListView() {
        adapter = new UserListAdapter(this, allUsers, currentUserId);
        friendsListView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.amis_page;
    }
}
