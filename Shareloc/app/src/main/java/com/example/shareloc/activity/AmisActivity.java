package com.example.shareloc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

        adapter = new UserListAdapter(this, new ArrayList<>(), currentUserId);

        friendsListView.setAdapter(adapter);
        loadFriends();

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

        ImageView backButton = findViewById(R.id.loupe);
        backButton.setOnClickListener(view -> openSearchActivity());
    }

    private void openSearchActivity() {
        Intent intent = new Intent(AmisActivity.this, SearchFriendActivity.class);
        startActivity(intent);
    }

    private void loadFriends() {
        DatabaseReference friendListRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("friendList");
        friendListRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> friendIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    friendIds.add(snapshot.getValue(String.class));
                }
                fetchFriendDetails(friendIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void fetchFriendDetails(List<String> friendIds) {
        List<User> friends = new ArrayList<>();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        for (String friendId : friendIds) {
            usersRef.child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User friend = dataSnapshot.getValue(User.class);
                    if (friend != null) {
                        friends.add(friend);
                    }

                    if (friends.size() == friendIds.size()) {
                        adapter.updateData(friends);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }
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
