package com.example.shareloc;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import androidx.annotation.Nullable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AmisActivity extends BaseActivity {
    private ApiManager apiManager;
    private ListView friendsListView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiManager = new ApiManager();
        friendsListView = findViewById(R.id.friends_list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        friendsListView.setAdapter(adapter);

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
                // Gérer l'erreur
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.amis_page;
    }
}