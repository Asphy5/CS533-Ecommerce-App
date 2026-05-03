package com.deitel.ecom.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.deitel.ecom.api.ApiRepository;
import com.deitel.ecom.adapters.CartAdapter;
import com.deitel.ecom.R;
import com.deitel.ecom.helpers.Session;

import org.json.JSONArray;
import org.json.JSONObject;

public class CartFragment extends Fragment {
    // Initialize UI
    ListView cartList;
    Button checkoutBtn, backBtn;
    EditText cardInput;
    TextView totalPrice;

    String[] items;
    int[] productIds;
    int[] quantities;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Initialize UI elements
        cartList = (ListView) view.findViewById(R.id.cartList);
        checkoutBtn = (Button) view.findViewById(R.id.checkoutBtn);
        cardInput = (EditText) view.findViewById(R.id.cardInput);
        totalPrice = (TextView) view.findViewById(R.id.totalPrice);
        backBtn = (Button) view.findViewById(R.id.backBtn);


        // Set up button actions & load the cart
        setupCheckout();
        setupBackButton();
        setupRemoveItem();
        loadCart();

        return view;
    }

    // Load the cart's data
    private void loadCart() {
        new Thread(() -> {
            try {
                // Attempt to POST to get_cart.php
                JSONObject req = new JSONObject();
                req.put("user_id", Session.userId);

                // Create JSONArray from response
                JSONObject response = ApiRepository.post("get_cart.php", req);
                JSONArray arr = response.getJSONArray("cart");


                items = new String[arr.length()];
                productIds = new int[arr.length()];
                quantities = new int[arr.length()];
                double total = 0;

                // Separate JSONarray into individual arrays
                for (int i = 0; i < arr.length(); i++) {

                    JSONObject obj = arr.getJSONObject(i);

                    // Get name, price, qty, pid from each JSON object
                    String name = obj.optString("name");
                    double price = obj.optDouble("price", 0);
                    int qty = obj.optInt("quantity", 1);
                    int productId = obj.optInt("product_id");

                    items[i] = name + " x" + qty + " - $" + price;
                    productIds[i] = productId;
                    quantities[i] = qty;

                    total += price * qty;
                }

                double finalTotal = total;

                getActivity().runOnUiThread(() -> {
                    // Update cart UI
                    cartList.setAdapter(new CartAdapter(
                            getActivity(),
                            items,
                            productIds,
                            quantities
                    ));
                    totalPrice.setText("Total: $" + String.format("%.2f", finalTotal));
                });

            } catch (Exception e) {
                Log.e("CART_LOAD", "Failed to load cart", e);
            }

        }).start();
    }

    // Item removal button setup
    private void setupRemoveItem() {
        cartList.setOnItemClickListener((parent, view, position, id) -> {
            int productId = productIds[position];

            new Thread(() -> {
                try {
                    // Attempt to POSt to remove_from_cart.php
                    JSONObject req = new JSONObject();
                    req.put("user_id", Session.userId);
                    req.put("product_id", productId);

                    JSONObject response = ApiRepository.post("remove_from_cart.php", req);

                    //Log.d("REMOVE_CART", response.toString());

                    getActivity().runOnUiThread(() -> {
                        // Show item removed message & update UI
                        Toast.makeText(getActivity(),
                                "Item removed",
                                Toast.LENGTH_SHORT).show();

                        loadCart();
                    });
                } catch (Exception e) {
                    Log.e("REMOVE_CART", "Error removing item", e);
                }

            }).start();
        });
    }

    // Interface for CartAdapter to refresh cart
    public void refreshCart() {
        loadCart();
    }

    // Set up checkout button
    private void setupCheckout() {
        checkoutBtn.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    // Attempt to POST to checkout.php
                    JSONObject req = new JSONObject();
                    req.put("user_id", Session.userId);
                    req.put("card_number", cardInput.getText().toString());

                    JSONObject res = ApiRepository.post("checkout.php", req);

                    String status = res.optString("status");

                    // Update UI with success or error message
                    getActivity().runOnUiThread(() -> {
                        if ("success".equals(status)) {
                            Toast.makeText(getActivity(),
                                    "Checkout successful!",
                                    Toast.LENGTH_SHORT).show();

                            loadCart(); // clears cart view
                        } else if ("invalid_card".equals(status)) {
                            Toast.makeText(getActivity(),
                                    "Invalid card number",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(),
                                    "Checkout failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e("CHECKOUT", "Error", e);
                }

            }).start();
        });
    }

    // Set up back button
    private void setupBackButton() {
        // Hide the fragment and show MainActivity UI again
        backBtn.setOnClickListener(v -> {
            getActivity().findViewById(R.id.container).setVisibility(View.GONE);
            getActivity().findViewById(R.id.productList).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.viewCart).setVisibility(View.VISIBLE);

            getFragmentManager().popBackStack();
        });
    }
}