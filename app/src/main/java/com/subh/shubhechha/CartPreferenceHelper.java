package com.subh.shubhechha;

import android.content.Context;

import com.subh.shubhechha.Model.CartResponse;
import com.subh.shubhechha.utils.SharedPref;

/**
 * Helper class to manage cart item count in SharedPreferences
 * Uses the existing SharedPref class
 */
public class CartPreferenceHelper {

    private Context context;
    private SharedPref sharedPref;

    public CartPreferenceHelper(Context context) {
        this.context = context;
        this.sharedPref = new SharedPref();
    }

    /**
     * Save cart item count to SharedPreferences
     * Call this whenever you receive cart data from API
     * @param count The total number of items in cart from API response
     */
    public void saveCartItemCount(int count) {
        sharedPref.setPrefInteger(context, sharedPref.cart_count, count);
    }

    /**
     * Get cart item count from SharedPreferences
     * @return The saved cart item count, or 0 if not found
     */
    public int getCartItemCount() {
        return sharedPref.getPrefInteger(context, sharedPref.cart_count);
    }

    /**
     * Clear cart item count (set to 0)
     * Call this when user logs out or cart is emptied
     */
    public void clearCartItemCount() {
        sharedPref.setPrefInteger(context, sharedPref.cart_count, 0);
    }

    /**
     * Update cart count directly from CartResponse
     * @param cartResponse The API response containing cart data
     */
    public void updateFromCartResponse(CartResponse cartResponse) {
        if (cartResponse != null && cartResponse.getData() != null) {
            int count = cartResponse.getData().getCart_item_count();
            saveCartItemCount(count);
        }
    }
}