package com.example.shareloc;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawer;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.violet_clair));
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );


        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }

        toggle.syncState();

        mAuth = FirebaseAuth.getInstance();
        checkCurrentUser();
    }

    private void checkCurrentUser() {
        if (mAuth.getCurrentUser() == null) {
            redirectToLogin();
        }
    }

    protected abstract int getLayoutId();

    void logout() {
        mAuth.signOut();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home && !(this instanceof homePageActivity)) {
            startActivity(new Intent(this, homePageActivity.class));
        } if (id == R.id.nav_france && !(this instanceof FranceMapActivity)) {
            startActivity(new Intent(this, FranceMapActivity.class));
        } else if (id == R.id.nav_amis && !(this instanceof AmisActivity)) {
            startActivity(new Intent(this, AmisActivity.class));
        } else if (id == R.id.nav_user && !(this instanceof UserProfileActivity)) {
            startActivity(new Intent(this, UserProfileActivity.class));
        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
