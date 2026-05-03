package com.deitel.ecom.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.deitel.ecom.api.ApiRepository;
import com.deitel.ecom.fragments.CartFragment;
import com.deitel.ecom.R;
import com.deitel.ecom.helpers.Session;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends Activity {
    // Initialize UI elements
    ListView listView;
    Button viewCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        listView = (ListView) findViewById(R.id.productList);
        viewCart = (Button) findViewById(R.id.viewCart);
        Button logoutBtn = (Button) findViewById(R.id.logoutBtn);

        // Set up logout button
        logoutBtn.setOnClickListener(v -> {
            Session.logout(this);
        });

        // Set up view cart button
        viewCart.setOnClickListener(v -> {
            // hide product UI & show fragment
            listView.setVisibility(View.GONE);
            viewCart.setVisibility(View.GONE);
            findViewById(R.id.container).setVisibility(View.VISIBLE);

            // Bring up the fragment
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new CartFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Add to cart click
        listView.setOnItemClickListener((parent, view, position, id) -> {
            new Thread(() -> {
                try {
                    // Build request
                    JSONObject data = new JSONObject();
                    data.put("user_id", Session.userId);
                    data.put("product_id", position + 1);
                    data.put("quantity", 1);

                    // Attempt to POST to add_to_cart
                    ApiRepository.post("add_to_cart.php", data);

                    // Show added to cart message
                    runOnUiThread(() ->
                            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
                    );
                } catch (Exception e) {
                    Log.e("ADD_TO_CART", "Failed", e);
                }
            }).start();

        });

        loadProducts();
    }

    @Override
    public void onBackPressed() {
        // Set MainActivity visible
        if (findViewById(R.id.container).getVisibility() == View.VISIBLE) {

            findViewById(R.id.container).setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            viewCart.setVisibility(View.VISIBLE);

        } else {
            super.onBackPressed();
        }
    }


    private void loadProducts() {
        new Thread(() -> {
            try {
                // Get a JSONarray of all products from backend
                JSONArray arr = ApiRepository.getArray("products.php");

                String[] items = new String[arr.length()];

                // Separate JSONarray into individual objects
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    String name = obj.optString("name", "Unknown");
                    String price = obj.optString("price", "0");

                    items[i] = name + " - $" + price;
                }

                // Update listView with product list
                runOnUiThread(() -> {
                    if (!isFinishing()) {
                        listView.setAdapter(new ArrayAdapter<>(
                                MainActivity.this,
                                android.R.layout.simple_list_item_1,
                                items
                        ));
                    }
                });
            } catch (Exception e) {
                Log.e("LOAD_PRODUCTS_ERROR", "Network failure", e);
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Failed to load products",
                                Toast.LENGTH_SHORT).show()
                );
            }

        }).start();
    }
}