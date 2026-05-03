package com.deitel.ecom.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import com.deitel.ecom.api.ApiRepository;
import com.deitel.ecom.R;
import com.deitel.ecom.helpers.Session;

import org.json.JSONArray;
import org.json.JSONObject;

public class AdminActivity extends Activity {
    // Initialize UI
    EditText name, price;
    Button addBtn;
    ListView productList;

    String[] items;
    int[] ids;

    int selectedId = -1; // -1 = new product

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize UI
        name = (EditText) findViewById(R.id.name);
        price = (EditText) findViewById(R.id.price);
        addBtn = (Button) findViewById(R.id.addBtn);
        productList = (ListView) findViewById(R.id.productList);
        Button logoutBtn = (Button) findViewById(R.id.logoutBtn);

        // Action for logout button
        logoutBtn.setOnClickListener(v -> {
            Session.logout(this);
        });

        // Action for save button
        addBtn.setOnClickListener(v -> {
            new Thread(() -> {
                try { // Attempt to save product to backend
                    JSONObject req = new JSONObject();
                    req.put("id", selectedId);
                    req.put("name", name.getText().toString());
                    req.put("price", Double.parseDouble(price.getText().toString()));

                    // POST to save_product.php
                    ApiRepository.post("save_product.php", req);

                    // Show either Added or Updated message
                    runOnUiThread(() -> {
                        Toast.makeText(this,
                                (selectedId == -1 ? "Added" : "Updated"),
                                Toast.LENGTH_SHORT).show();

                        // Reset the form & reload product list
                        clearForm();
                        loadProducts();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });

        // Action for editing (tapping product in product list)
        productList.setOnItemClickListener((parent, view, position, id) -> {
            // Get data from product
            selectedId = ids[position];
            String[] parts = items[position].split(" - ");

            // Set input fields to product data
            name.setText(parts[0]);
            price.setText(parts[1]);
        });

        loadProducts();
    }

    // product loader
    private void loadProducts() {
        new Thread(() -> {
            try {
                // Get a JSONarray of all products from backend
                JSONArray arr = ApiRepository.getArray("products.php");
                Log.d("ADMIN_RAW", arr.toString());
                items = new String[arr.length()];
                ids = new int[arr.length()];

                // Separate JSONArray into individual objects
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    ids[i] = obj.getInt("id");
                    items[i] = obj.getString("name")
                            + " - " + obj.getDouble("price");
                }

                runOnUiThread(() -> {
                    // Update UI with product list
                    productList.setAdapter(new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_list_item_1,
                            items
                    ));
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

    // Reset the form
    private void clearForm() {
        selectedId = -1;
        name.setText("");
        price.setText("");
    }
}