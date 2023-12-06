package com.example.shareloc;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.google.android.material.navigation.NavigationView;

public class AmisActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NavigationView navigationView = findViewById(R.id.nav_view);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.amis_page;
    }
}
