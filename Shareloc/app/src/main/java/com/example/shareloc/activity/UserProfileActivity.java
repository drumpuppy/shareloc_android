package com.example.shareloc.activity;

import com.example.shareloc.R;
import com.example.shareloc.Class.User;
import com.example.shareloc.databinding.ActivityUserProfileBinding;
import com.example.shareloc.managers.ApiManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.util.Log;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;


public class UserProfileActivity extends BaseActivity {

    private ActivityUserProfileBinding binding;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.violet_clair));



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

                        displayAchievements(user.getAchievements());
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


    private void displayAchievements(Map<String, Boolean> achievements) {
        LinearLayout achievementsLayout = findViewById(R.id.achievementsLayout);
        achievementsLayout.removeAllViews();

        for (Map.Entry<String, Boolean> entry : achievements.entrySet()) {
            String achievementDescription = entry.getKey();
            boolean isAchieved = entry.getValue();

            String drawableName = mapDrawableName(achievementDescription);
            int imageId = getResources().getIdentifier(drawableName, "drawable", getPackageName());

            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    convertDpToPixel(48, this),
                    convertDpToPixel(48, this)
            ));
            if (imageId != 0) {
                imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), imageId, null));
            } else {
                Log.e("UserProfileActivity", "Drawable not found for: " + drawableName);
            }

            TextView textView = new TextView(this);
            textView.setText(achievementDescription);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            int bottomPadding = convertDpToPixel(8, this);
            layoutParams.setMargins(0, 0, 0, bottomPadding);
            textView.setLayoutParams(layoutParams);

            float alphaValue = isAchieved ? 1.0f : 0.2f;
            float betaValue = isAchieved ? 1.0f : 0.5f;
            imageView.setAlpha(alphaValue);
            textView.setAlpha(betaValue);

            achievementsLayout.addView(imageView);
            achievementsLayout.addView(textView);
        }
    }




    public static int convertDpToPixel(int dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    private String mapDrawableName(String achievementName) {
        switch (achievementName) {
            case "Mon petit poney : trouve ton premier pays":
                return "mon_petit_poney";
            case "Randoneur: découvre 5 pays":
                return "hiker";
            case "Expert: trouve 40 pays":
                return "expert";
            case "God Save the Queen: trouve tous les pays du CommonWealth":
                return "god_save_the_queen";
            case "Mister WorldWide: découvre le monde entier !":
                return "mister_worldwide";
            case "I go solo: fais toi un ami":
                return "i_go_solo";
            case "Où es-tu ?: découvre la carte d'un(e) ami(e)":
                return "where_are_you";
            case "Stalker: découvre la carte de 5 amis":
                return "stalker";
            case "Holy Moly: va au Vatican":
                return "holy_moly";
            default:
                return "default_achievement";
        }
    }


}
