package com.pierrejanineh.geonote.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pierrejanineh.geonote.Model.FirebaseManager;
import com.pierrejanineh.geonote.R;
import com.pierrejanineh.geonote.UI.navigation.NavigationMainActivity;

public class LoginLogoutActivity extends AppCompatActivity {

    private final FirebaseManager firebase = FirebaseManager.getInstance();

    private boolean isLogin = true;

    private TextView title;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button loginBtn;
    private Button switchBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_lougout);

        title = findViewById(R.id.title);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.button);
        switchBtn = findViewById(R.id.switchButton);
        progressBar = findViewById(R.id.progressBar);

        assignButtonsActions();
    }

    /**
     * Set OnClick Listeners for buttons.
     */
    private void assignButtonsActions(){
        loginBtn.setOnClickListener(view -> {
            if (isLogin){
                login();
            } else {
                signUp();
            }
        });

        switchBtn.setOnClickListener(view -> {
            setIsLogin(!isLogin);
        });
    }

    /**
     * Gather User information and Login User.
     * Display a Toast if an exception is caught.
     */
    private void login(){
        String emailS = email.getText().toString();
        String passwordS = password.getText().toString();

        if (!emailS.isEmpty() && !passwordS.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            firebase.login(emailS, passwordS, task -> {
                if (task.isSuccessful()){
                    viewNotesAfterLogin();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginLogoutActivity.this, String.format(getResources().getString(R.string.failed_login), task.getException().getMessage()), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Gather User information and Signup User.
     * Display a Toast if an exception is caught.
     */
    private void signUp(){
        String nameS = name.getText().toString();
        String emailS = email.getText().toString();
        String passwordS = password.getText().toString();

        if (!nameS.isEmpty() && !emailS.isEmpty() && !passwordS.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            firebase.signUp(name.getText().toString(), email.getText().toString(), password.getText().toString(), task -> {
                if (task.isSuccessful()){
                    viewNotesAfterLogin();
                } else {

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginLogoutActivity.this, String.format(getResources().getString(R.string.failed_signup), task.getException().getMessage()), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Navigate to MainScreen for displaying Notes list.
     */
    private void viewNotesAfterLogin() {
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(LoginLogoutActivity.this, NavigationMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    /**
     * Switches between Sign-up and Login Screens.
     * @param login boolean to indicate whether to display Login/Signup.
     */
    private void setIsLogin(boolean login) {
        isLogin = login;
        if (login) {
            title.setText(R.string.login);
            name.setVisibility(View.GONE);
            loginBtn.setText(R.string.login);
            switchBtn.setText(R.string.switch_to_signup);
        } else {
            title.setText(R.string.signup);
            name.setVisibility(View.VISIBLE);
            loginBtn.setText(R.string.signup);
            switchBtn.setText(R.string.switch_to_login);
        }
    }
}