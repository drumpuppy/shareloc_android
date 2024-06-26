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
import android.widget.Toast;

import java.util.Arrays;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


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

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
        checkAndUnlockFriendBasedAchievements();
        checkAndUpdateAchievements();
        checkAndUnlockSoloAchievement();
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

    private void checkAndUpdateAchievements() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            userRef.child("countriesVisited").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long visitedCount = 0;
                    boolean visitedVatican = false;
                    boolean visitedAllCommonwealth = true; // Assume true, check if false
                    List<String> commonwealthCountries = getCommonwealthCountries();

                    for (DataSnapshot countrySnapshot : dataSnapshot.getChildren()) {
                        if (countrySnapshot.getValue(Boolean.class)) {
                            visitedCount++;
                            String countryName = countrySnapshot.getKey();
                            if ("Vatican".equals(countryName)) {
                                visitedVatican = true;
                            }
                            if (commonwealthCountries.contains(countryName)) {
                                commonwealthCountries.remove(countryName);
                            }
                        }
                    }

                    if (visitedCount >= 40)
                        unlockAchievement("Expert: trouve 40 pays");
                    else if (visitedCount >= 5) {
                        unlockAchievement("Randoneur: découvre 5 pays");
                    } else if (visitedCount > 0) {
                        unlockAchievement("Mon petit poney : trouve ton premier pays");
                    }
                    if (visitedVatican) unlockAchievement("Holy Moly: va au Vatican");
                    if (commonwealthCountries.isEmpty()) unlockAchievement("God Save the Queen: trouve tous les pays du CommonWealth");

                    long finalVisitedCount = visitedCount;
                    getAllCountriesCount(totalCountries -> {
                        if (finalVisitedCount == totalCountries) unlockAchievement("Mister WorldWide: découvre le monde entier !");
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("UserProfileActivity", "Error fetching countries visited: " + databaseError.toException());
                }
            });
        } else {
            Log.w("UserProfileActivity", "No authenticated user found");
        }
    }

    private List<String> getCommonwealthCountries() {
        return Arrays.asList("Canada", "Australia", "India", "United Kingdom");
    }

    private void getAllCountriesCount(Consumer<Long> onResult) {
        DatabaseReference countriesRef = FirebaseDatabase.getInstance().getReference("countries");
        countriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                onResult.accept(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserProfileActivity", "Error fetching total number of countries: " + databaseError.toException());
            }
        });
    }

    private void checkAndUnlockSoloAchievement() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference friendListRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("friendList");

            friendListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        unlockAchievement("I go solo: fais toi un ami");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("UserProfileActivity", "Error checking friends list for achievements: " + databaseError.toException());
                }
            });
        }
    }

    private void unlockAchievement(String achievementName) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userAchievementsRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(userId)
                    .child("achievements");

            userAchievementsRef.child(achievementName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && Boolean.TRUE.equals(snapshot.getValue(Boolean.class))) {
                        // Achievement already unlocked, do nothing
                        Log.d("UserProfileActivity", "Achievement already unlocked: " + achievementName);
                    } else {
                        userAchievementsRef.child(achievementName).setValue(true)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("UserProfileActivity", "succès débloqué: " + achievementName);
                                    showAchievementUnlockedPopup(achievementName);
                                })
                                .addOnFailureListener(e -> Log.e("UserProfileActivity", "erreur pendant le deblocage: " + e.getMessage()));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("UserProfileActivity", "Error checking achievement status: " + databaseError.toException());
                }
            });
        } else {
            Log.w("UserProfileActivity", "No authenticated user found");
        }
    }


    private void showAchievementUnlockedPopup(String achievementName) {
        String message = "Bravo! tu as débloqué le succès : \n" + achievementName;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }



    private void checkAndUnlockFriendBasedAchievements() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        Map<String, Boolean> mapVisited = user.getMapVisited();
                        int uniqueMapVisitedCount = countUniqueMapVisited(mapVisited);

                        if (uniqueMapVisitedCount >= 1) unlockAchievement("Où es-tu ?: découvre la carte d'un(e) ami(e)");
                        if (uniqueMapVisitedCount >= 5) unlockAchievement("Stalker: découvre la carte de 5 amis");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("UserProfileActivity", "Error fetching user data: " + databaseError.toException());
                }
            });
        } else {
            Log.w("UserProfileActivity", "No authenticated user found");
        }
    }

    private int countUniqueMapVisited(Map<String, Boolean> mapVisited) {
        int count = 0;
        for (Boolean visited : mapVisited.values()) {
            if (Boolean.TRUE.equals(visited)) {
                count++;
            }
        }
        return count;
    }

}
