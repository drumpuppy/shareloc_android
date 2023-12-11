package com.example.shareloc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AmisActivity extends BaseActivity {
    private ApiManager apiManager;
    private ListView friendsListView;
    private ArrayAdapter<String> adapter;
    private EditText friendIdEditText;
    private Button addFriendButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amis_page);

        NavigationView navigationView = findViewById(R.id.nav_view);

        apiManager = new ApiManager();
        friendsListView = findViewById(R.id.friends_list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        friendsListView.setAdapter(adapter);

        friendIdEditText = findViewById(R.id.friend_id_edit_text);
        addFriendButton = findViewById(R.id.add_friend_button);

        String userId = "replace_this_with_actual_user_id";
        apiManager.getFriends(userId, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String friendId = snapshot.getValue(String.class);
                    adapter.add(friendId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String friendId = friendIdEditText.getText().toString().trim();
                if (!friendId.isEmpty()) {
                    apiManager.addFriend(userId, friendId, new ApiManager.OnFriendAddedListener() {
                        @Override
                        public void onFriendAdded(boolean success) {
                            if (success) {
                                adapter.add(friendId);
                                friendIdEditText.setText("");
                            } else {
                                Toast.makeText(AmisActivity.this, "Friend ID does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.amis_page;
    }
}
