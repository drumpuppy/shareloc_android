package com.example.shareloc;

import com.example.shareloc.databinding.ActivityUserProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

import android.util.Log;
import android.os.Bundle;
import android.widget.Button;

import java.util.Map;


public class UserProfileActivity extends BaseActivity {

    private ActivityUserProfileBinding binding;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiManager = new ApiManager();
        loadUserData();
        binding.buttonSaveChanges.setOnClickListener(v -> updateUserData());

        Button logoutButton = findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(view -> logout());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_profile;
    }

    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        binding.editTextNickname.setText(user.getNickname());

                        Map<String, Boolean> countriesVisited = user.getCountriesVisited();
                        if (countriesVisited != null && !countriesVisited.isEmpty()) {
                            StringBuilder countriesVisitedText = new StringBuilder();
                            for (Map.Entry<String, Boolean> entry : countriesVisited.entrySet()) {
                                if (entry.getValue()) { // Check if the country was visited
                                    countriesVisitedText.append(entry.getKey()).append("; ");
                                }
                            }
                            if (countriesVisitedText.length() > 0) {
                                binding.textViewCountriesVisited.setText(countriesVisitedText.toString());
                            } else {
                                binding.textViewCountriesVisited.setText(R.string.none);
                            }
                        } else {
                            binding.textViewCountriesVisited.setText(R.string.none);
                        }


                        Map<String, Boolean> achievements = user.getAchievements();
                        if (achievements != null && !achievements.isEmpty()) {
                            StringBuilder achievementsText = new StringBuilder();
                            for (Map.Entry<String, Boolean> entry : achievements.entrySet()) {
                                achievementsText.append(entry.getKey())
                                        .append(": ")
                                        .append(entry.getValue() ? "Achieved" : "Not achieved")
                                        .append("\n");
                            }
                            binding.textViewAchievements.setText(achievementsText.toString());
                        } else {
                            binding.textViewAchievements.setText(R.string.none);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("UserProfileActivity", "loadUserData:onCancelled", databaseError.toException());
                }
            });
        }
    }



    private void updateUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String nickname = binding.editTextNickname.getText().toString();

            Log.d("UserProfileActivity", "Updating user: " + userId + " with nickname: " + nickname);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        user.setNickname(nickname);
                    }
                    apiManager.updateUser(userId, user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("UserProfileActivity", "loadUserData:onCancelled", error.toException());
                }
            });

        } else {
            Log.w("UserProfileActivity", "No authenticated user found");
        }
    }

}
