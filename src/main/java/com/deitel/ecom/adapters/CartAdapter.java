package com.deitel.ecom.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import com.deitel.ecom.api.ApiRepository;
import com.deitel.ecom.R;
import com.deitel.ecom.helpers.Session;
import com.deitel.ecom.fragments.CartFragment;

import org.json.JSONObject;

public class CartAdapter extends BaseAdapter {

    Activity context;
    String[] items;
    int[] productIds;
    int[] quantities;

    // Constructor
    public CartAdapter(Activity context,
                       String[] items,
                       int[] productIds,
                       int[] quantities) {

        this.context = context;
        this.items = items;
        this.productIds = productIds;
        this.quantities = quantities;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Initialize UI elements
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(R.layout.cart_item, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(R.id.itemText);
        Button plus = (Button) convertView.findViewById(R.id.plusBtn);
        Button minus = (Button) convertView.findViewById(R.id.minusBtn);

        String displayText = items[position];
        text.setText(displayText);

        int productId = productIds[position];

        // Create button listeners
        plus.setOnClickListener(v -> updateQuantity(productId, +1));
        minus.setOnClickListener(v -> updateQuantity(productId, -1));

        return convertView;
    }

    // Item Quantity Update
    private void updateQuantity(int productId, int delta) {
        new Thread(() -> {
            try {
                // Build request
                JSONObject req = new JSONObject();
                req.put("user_id", Session.userId);
                req.put("product_id", productId);
                req.put("delta", delta);

                JSONObject res;

                // Attempt to POST to update_cart_qty
                try {
                    res = ApiRepository.post("update_cart_qty.php", req);
                } catch (Exception e) {
                    Log.e("CART_UPDATE", "Request failed", e);
                    return;
                }

                // Server didn't respond
                if (res == null) {
                    Log.e("CART_UPDATE", "Null response from server");
                    return;
                }

                //Log.d("CART_UPDATE", res.toString());

                context.runOnUiThread(() -> {
                    // Refresh CartFragment
                    if (context.getFragmentManager() != null) {

                        CartFragment frag = (CartFragment)
                                context.getFragmentManager().findFragmentById(R.id.container);

                        if (frag != null) {
                            frag.refreshCart();
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("CART_UPDATE", "Failed", e);
            }

        }).start();
    }
}