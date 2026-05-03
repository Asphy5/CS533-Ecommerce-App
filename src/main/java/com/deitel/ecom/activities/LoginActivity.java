package com.deitel.ecom.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import com.deitel.ecom.R;
import com.deitel.ecom.api.ApiRepository;
import com.deitel.ecom.helpers.Session;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends Activity {
    // Initialize UI
    EditText username, password;
    Button loginBtn, registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        // Set up login button
        loginBtn.setOnClickListener(v ->
                new Thread(() -> auth("login.php")).start()
        );

        // Set up register button
        registerBtn.setOnClickListener(v ->
                new Thread(() -> auth("register.php")).start()
        );
    }


    private void auth(String endpoint) {
        try {
            // Build request
            JSONObject body = new JSONObject();
            body.put("username", username.getText().toString());
            body.put("password", password.getText().toString());

            // Attempt to POST to the endpoint
            JSONObject response = ApiRepository.post(endpoint, body);

            // The backend didn't respond
            if (response == null) {
                throw new Exception("Null response from server");
            }

            handleResponse(response);

        } catch (Exception e) {
            Log.e("LOGIN_ERROR", "Request failed", e);
            runOnUiThread(() ->
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
            );
        }
    }

    // Check the backend's response
    private void handleResponse(JSONObject json) {
        try {
            String status = json.optString("status");

            if ("success".equals(status)) {
                // Set the session variables
                Session.userId = json.optInt("user_id", -1);
                Session.role = json.optString("role", "user");
                Session.loggedIn = true;

                // Open the AdminActivity or MainActivity
                runOnUiThread(() -> {
                    Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();

                    if ("admin".equals(Session.role)) {
                        startActivity(new Intent(this, AdminActivity.class));
                    } else {
                        startActivity(new Intent(this, MainActivity.class));
                    }

                    finish();
                });
            } else {
                String message = json.optString("message");

                runOnUiThread(() -> {

                    if ("username_taken".equals(message)) {
                        Toast.makeText(this,
                                "Username already exists",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(this,
                                "Login/Registration failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Log.e("LOGIN_PARSE_ERROR", json.toString(), e);

            runOnUiThread(() ->
                    Toast.makeText(this,
                            "Invalid server response",
                            Toast.LENGTH_LONG).show()
            );
        }
    }
}