package com.example.shareloc.activity;// AmisActivity.java
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.shareloc.R;
import com.example.shareloc.Class.User;
import com.example.shareloc.adaptater.UserListAdapter;
import com.example.shareloc.managers.ApiManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

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
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadAllUsers();

        friendsListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = allUsers.get(position);
            handleUserSelection(selectedUser);
        });



    }

    private void loadAllUsers() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUsers.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        allUsers.add(user);
                    }
                }
                updateListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AmisActivity.this, "Error fetching users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void handleUserSelection(User selectedUser) {
        DatabaseReference currentUserRef = usersRef.child(currentUserId);
        currentUserRef.child("friendList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> friendList = dataSnapshot.getValue(List.class);
                if (friendList == null) {
                    friendList = new ArrayList<>();
                }
                if (friendList.contains(selectedUser.getUsername())) {
                    friendList.remove(selectedUser.getUsername());
                    Toast.makeText(AmisActivity.this, "Unfollowed " + selectedUser.getUsername(), Toast.LENGTH_SHORT).show();
                } else {
                    friendList.add(selectedUser.getUsername());
                    Toast.makeText(AmisActivity.this, "Followed " + selectedUser.getUsername(), Toast.LENGTH_SHORT).show();
                }
                currentUserRef.child("friendList").setValue(friendList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AmisActivity.this, "Error updating friend list: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.amis_page;
    }
}
