package com.example.shareloc.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.shareloc.R;
import com.example.shareloc.Class.User;
import com.example.shareloc.adaptater.UserListAdapter;
import com.example.shareloc.managers.ApiManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFriendActivity extends BaseActivity {
    private ApiManager apiManager;

    private EditText friendIdEditText;
    private ListView friendsListView;
    private UserListAdapter adapter;
    private DatabaseReference usersRef;
    private List<User> allUsers;
    private String currentUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_friend);

        friendIdEditText = findViewById(R.id.friend_id_edit_text);
        friendsListView = findViewById(R.id.friends_list_view);
        allUsers = new ArrayList<>();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        apiManager = new ApiManager();

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> finish());

        loadAllUsers();

        friendIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {loadUsernames(charSequence.toString());}
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadAllUsers() {
        Log.d("SearchFriendActivity", "Loading all users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUsers.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        user.setUserId(userSnapshot.getKey());
                        allUsers.add(user);
                        Log.d("SearchFriendActivity", "Loaded user: " + user.getUsername() + ", ID: " + user.getUserId());
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
        Log.d("SearchFriendActivity", "Searching for: " + searchTerm);
        searchTerm = searchTerm.toLowerCase();
        List<User> filteredUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getUsername().toLowerCase().contains(searchTerm)) {
                filteredUsers.add(user);
                Log.d("SearchFriendActivity", "Matched user: " + user.getUsername());
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
