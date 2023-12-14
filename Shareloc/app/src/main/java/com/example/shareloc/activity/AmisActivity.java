package com.example.shareloc.activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = allUsers.get(position);
                handleUserSelection(selectedUser);
            }
        });

        loadAllUsers();

        friendIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadUsernames(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });
    }

    private void loadAllUsers() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUsers.clear();
                String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    User user = snapshot.getValue(User.class);
                    if (user != null && !user.getUsername().equals(currentUserEmail)) {
                        allUsers.add(user);
                    }
                }
                loadCurrentUserFriendList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AmisActivity.this, "Error loading users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCurrentUserFriendList() {
        DatabaseReference currentUserRef = usersRef.child(currentUserId);
        currentUserRef.child("friendList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
                Map<String, String> friendMap = dataSnapshot.getValue(t);
                if (friendMap == null) {
                    friendMap = new HashMap<>();
                }
                List<String> friendList = new ArrayList<>(friendMap.values());
                updateListView(friendList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AmisActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateListView(List<String> friendList) {
        adapter = new UserListAdapter(AmisActivity.this, allUsers, currentUserId, friendList);
        friendsListView.setAdapter(adapter);
    }

    private void loadUsernames(String searchTerm) {
        usersRef.orderByChild("username")
                .startAt(searchTerm)
                .endAt(searchTerm + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        allUsers.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null && !user.getUsername().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                                allUsers.add(user);
                            }
                        }
                        updateListView();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle errors
                    }
                });
    }



    private void updateListView() {
        DatabaseReference currentUserRef = usersRef.child(currentUserId);
        currentUserRef.child("friendList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
                Map<String, String> friendMap = dataSnapshot.getValue(t);
                if (friendMap == null) {
                    friendMap = new HashMap<>();
                }
                List<String> friendList = new ArrayList<>(friendMap.values());
                adapter = new UserListAdapter(AmisActivity.this, allUsers, currentUserId, friendList);
                friendsListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AmisActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void handleUserSelection(User selectedUser) {
        DatabaseReference currentUserRef = usersRef.child(currentUserId);
        currentUserRef.child("friendList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                List<String> friendList = dataSnapshot.getValue(t);
                if (friendList == null) {
                    friendList = new ArrayList<>();
                }

                updateFriendList(friendList, selectedUser.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AmisActivity.this, "Error updating friend list: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateFriendList(List<String> friendList, String username) {
        DatabaseReference currentUserRef = usersRef.child(currentUserId).child("friendList");

        if (friendList.contains(username)) {
            friendList.remove(username);
            currentUserRef.setValue(friendList)
                    .addOnSuccessListener(aVoid -> Toast.makeText(AmisActivity.this, "Unfollowed " + username, Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(AmisActivity.this, "Error unfollowing " + username, Toast.LENGTH_SHORT).show());
        } else {
            friendList.add(username);
            currentUserRef.setValue(friendList)
                    .addOnSuccessListener(aVoid -> Toast.makeText(AmisActivity.this, "Followed " + username, Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(AmisActivity.this, "Error following " + username, Toast.LENGTH_SHORT).show());
        }
    }



    @Override
    protected int getLayoutId() {
        return R.layout.amis_page;
    }
}
