package com.example.shareloc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.Nullable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;

public class AmisActivity extends BaseActivity {

    private EditText friendIdEditText;
    private ListView friendsListView;
    private Button addFriendButton;
    private List<String> usernames;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amis_page);

        friendIdEditText = findViewById(R.id.friend_id_edit_text);
        friendsListView = findViewById(R.id.friends_list_view);
        addFriendButton = findViewById(R.id.add_friend_button);

        usernames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usernames);
        friendsListView.setAdapter(adapter);

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchTerm = friendIdEditText.getText().toString().trim();
                if (!searchTerm.isEmpty()) {
                    loadUsernames(searchTerm);
                }
            }
        });
    }

    private void loadUsernames(String searchTerm) {
        ApiManager apiManager = new ApiManager();
        apiManager.usersRef.orderByChild("username")
                .startAt(searchTerm)
                .endAt(searchTerm + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usernames.clear();

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String username = userSnapshot.child("username").getValue(String.class);
                            if (username != null) {
                                usernames.add(username);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle errors
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.amis_page;
    }
}
