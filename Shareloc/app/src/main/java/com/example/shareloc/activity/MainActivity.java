package com.example.shareloc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.core.view.GravityCompat;

import com.example.shareloc.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    MainActivity.super.onBackPressed();
                }
            }
        });

        Button accountAccessButton = findViewById(R.id.button1);
        accountAccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHomePage();
            }
        });

        Button challengeButton = findViewById(R.id.buttonChallenge);
        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openFranceActivity();}
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void openHomePage() {
        Intent intent = new Intent(MainActivity.this, homePageActivity.class);
        startActivity(intent);
    }

    private void openFranceActivity() {
        Intent intent = new Intent(MainActivity.this, FranceMapActivity.class);
        startActivity(intent);
    }

    public static class FriendMapActivity extends Activity {
    }
}

