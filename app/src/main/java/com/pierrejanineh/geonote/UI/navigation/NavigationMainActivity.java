package com.pierrejanineh.geonote.UI.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pierrejanineh.geonote.Model.FirebaseManager;
import com.pierrejanineh.geonote.R;
import com.pierrejanineh.geonote.databinding.ActivityNavigationMainBinding;

public class NavigationMainActivity extends AppCompatActivity {

    private ActivityNavigationMainBinding binding;

    private final FirebaseManager firebase = FirebaseManager.getInstance();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logoutButton) {
            firebase.logout(this, NavigationMainActivity.this);
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView();

        setFABAction();

        Toast.makeText(NavigationMainActivity.this, String.format(getResources().getString(R.string.welcome_msg), FirebaseManager.getInstance().getUser().getDisplayName()), Toast.LENGTH_SHORT).show();
    }

    /**
     * Creates Bottom Navigation Menu
     */
    private void bottomNavigationView() {
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_list, R.id.navigation_map)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    /**
     * Sets FloatingActionButton onClickListener method to navigate to Add note.
     */
    private void setFABAction() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddEditNoteActivity.class);
            startActivity(intent);
        });
    }
}