package com.pierrejanineh.geonote.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.pierrejanineh.geonote.Model.FirebaseManager;
import com.pierrejanineh.geonote.R;
import com.pierrejanineh.geonote.UI.navigation.NavigationMainActivity;

/**
 * Base Activity to for checking whether the user is logged in before directing to Login screen.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        if (FirebaseManager.getInstance().checkIfUserIsLoggedIn()) {
            Intent intent = new Intent(BaseActivity.this, NavigationMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        } else {
            Intent intent = new Intent(BaseActivity.this, LoginLogoutActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }
}