package com.example.shareloc;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home);
        }

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, homePage.class);
                startActivity(intent);
            }
        });

        //View headerView = navigationView.getHeaderView(0);
        //Button logoutButton = headerView.findViewById(R.id.btn_logout);

        //logoutButton.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
                // Implement logout functionality
        //        logout();
        //    }
        //});
    }

    private void logout() {
        // Implement your logout logic here
        // For example, navigate to the login screen or clear the user's session
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if (!(this instanceof MainActivity)) {
                Intent intent = new Intent(MainActivity.this, homePage.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_amis) {
            Intent intent2 = new Intent(MainActivity.this, AmisActivity.class);
            startActivity(intent2);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
