package com.example.shareloc.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

public class SearchFriendActivity extends BaseActivity {

    private ListView friendsListView;
    private UserListAdapter adapter;
    private DatabaseReference usersRef;
    private List<User> allUsers;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_friend);

        EditText friendIdEditText = findViewById(R.id.friend_id_edit_text);
        friendsListView = findViewById(R.id.friends_list_view);
        allUsers = new ArrayList<>();
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        setupAdapter();

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> finish());

        loadAllUsers();

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
    }
    private void setupAdapter() {
        adapter = new UserListAdapter(this, new ArrayList<>(), currentUserId, SearchFriendActivity.class);
        adapter.setOnDataChangeListener(this::loadAllUsers);
        friendsListView.setAdapter(adapter);
    }

    private void loadAllUsers() {
        Log.d("SearchFriendActivity", "Loading all users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUsers.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null && !userSnapshot.getKey().equals(currentUserId)) {
                        user.setUserId(userSnapshot.getKey());
                        allUsers.add(user);
                    }
                }
                updateListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SearchFriendActivity", "Error fetching users: " + databaseError.getMessage());
                Toast.makeText(SearchFriendActivity.this, "Error fetching users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
        adapter.updateData(filteredUsers);
    }

    private void updateListView() {
        adapter.updateData(allUsers);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.search_friend;
    }
}
