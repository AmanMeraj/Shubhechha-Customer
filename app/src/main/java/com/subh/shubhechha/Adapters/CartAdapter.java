package com.subh.shubhechha.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.subh.shubhechha.Model.CartResponse;
import com.subh.shubhechha.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private ArrayList<CartResponse.CartItem> cartItems;
    private OnCartItemListener listener;
    private DecimalFormat priceFormat;

    // Interface for callbacks
    public interface OnCartItemListener {
        void onQuantityChanged(CartResponse.CartItem item, int position, int newQuantity, boolean isIncreasing);
        void onDeleteItem(CartResponse.CartItem item, int position);
    }

    public CartAdapter(OnCartItemListener listener) {
        this.cartItems = new ArrayList<>();
        this.listener = listener;
        this.priceFormat = new DecimalFormat("#,##0.00");
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartResponse.CartItem item = cartItems.get(position);
        if (item != null) {
            holder.bind(item, position);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public void setCartItems(ArrayList<CartResponse.CartItem> items) {
        if (items != null) {
            this.cartItems = new ArrayList<>(items);
        } else {
            this.cartItems = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
        }
    }

    public ArrayList<CartResponse.CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public double getTotalAmount() {
        double total = 0;
        if (cartItems != null) {
            for (CartResponse.CartItem item : cartItems) {
                if (item != null && item.getItem() != null) {
                    try {
                        int quantity = Integer.parseInt(item.getQuantity());
                        double price = getItemPrice(item);
                        total += (price * quantity);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return total;
    }

    private double getItemPrice(CartResponse.CartItem cartItem) {
        if (cartItem == null || cartItem.getItem() == null) return 0.0;

        try {
            CartResponse.Item item = cartItem.getItem();
            String offerPrice = item.getOffer_price();
            String originalPrice = item.getAmount();

            if (offerPrice != null && !offerPrice.isEmpty()
                    && !offerPrice.equals("0") && !offerPrice.equals("0.00")) {
                return Double.parseDouble(offerPrice);
            } else if (originalPrice != null && !originalPrice.isEmpty()) {
                return Double.parseDouble(originalPrice);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private boolean hasOffer(CartResponse.CartItem cartItem) {
        if (cartItem == null || cartItem.getItem() == null) return false;

        try {
            String offerPrice = cartItem.getItem().getOffer_price();
            return offerPrice != null && !offerPrice.isEmpty()
                    && !offerPrice.equals("0") && !offerPrice.equals("0.00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private ImageView deleteIcon;
        private ImageView decrementButton;
        private ImageView incrementButton;
        private TextView productName;
        private TextView productDescription;
        private TextView originalPrice;
        private TextView currentPrice;
        private TextView quantityText;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            originalPrice = itemView.findViewById(R.id.originalPrice);
            currentPrice = itemView.findViewById(R.id.currentPrice);
            quantityText = itemView.findViewById(R.id.quantityText);

            // Apply strikethrough to original price
            if (originalPrice != null) {
                originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        public void bind(CartResponse.CartItem cartItem, int position) {
            if (cartItem == null || cartItem.getItem() == null) return;

            CartResponse.Item item = cartItem.getItem();

            // Set product name
            if (productName != null) {
                productName.setText(item.getName() != null ? item.getName() : "");
            }

            // Set product description (if available in your model)
            if (productDescription != null) {
                // You might need to add description field to your Item model
                productDescription.setVisibility(View.GONE);
            }

            // Get quantity
            int quantity = 1;
            try {
                quantity = Integer.parseInt(cartItem.getQuantity());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            // Set quantity
            if (quantityText != null) {
                quantityText.setText(String.valueOf(quantity));
            }

            // Get prices
            double offerPrice = 0.0;
            double regularPrice = 0.0;

            try {
                String offerPriceStr = item.getOffer_price();
                String regularPriceStr = item.getAmount();

                if (offerPriceStr != null && !offerPriceStr.isEmpty()) {
                    offerPrice = Double.parseDouble(offerPriceStr);
                }
                if (regularPriceStr != null && !regularPriceStr.isEmpty()) {
                    regularPrice = Double.parseDouble(regularPriceStr);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            // Display prices based on offer availability
            if (hasOffer(cartItem)) {
                // Has offer - show both prices
                if (originalPrice != null) {
                    originalPrice.setVisibility(View.VISIBLE);
                    originalPrice.setText("₹ " + priceFormat.format(regularPrice));
                }
                if (currentPrice != null) {
                    currentPrice.setText("₹ " + priceFormat.format(offerPrice));
                }
            } else {
                // No offer - show only regular price
                if (originalPrice != null) {
                    originalPrice.setVisibility(View.GONE);
                }
                if (currentPrice != null) {
                    currentPrice.setText("₹ " + priceFormat.format(regularPrice));
                }
            }

            // Load product image using Glide
            if (productImage != null) {
                try {
                    String imageUrl = null;
                    if (item.getImage_path() != null && !item.getImage_path().isEmpty()) {
                        imageUrl = item.getImage_path();
                    }

                    Glide.with(productImage.getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.no_image)
                            .error(R.drawable.no_image)
                            .centerCrop()
                            .into(productImage);
                } catch (Exception e) {
                    e.printStackTrace();
                    productImage.setImageResource(R.drawable.no_image);
                }
            }

            // Update button states
            updateButtonStates(quantity);

            // Decrement button click listener
            if (decrementButton != null) {
                decrementButton.setOnClickListener(v -> {
                    // FIXED: Get current quantity from TextView instead of using captured variable
                    int currentQuantity = getCurrentQuantity();

                    if (currentQuantity > 0) {
                        int newQuantity = currentQuantity - 1;

                        // Update UI immediately
                        if (quantityText != null) {
                            quantityText.setText(String.valueOf(newQuantity));
                        }
                        updateButtonStates(newQuantity);

                        // Notify listener (isIncreasing = false)
                        if (listener != null) {
                            listener.onQuantityChanged(cartItem, getBindingAdapterPosition(), newQuantity, false);
                        }
                    }
                });
            }

            // Increment button click listener
            if (incrementButton != null) {
                incrementButton.setOnClickListener(v -> {
                    // FIXED: Get current quantity from TextView instead of using captured variable
                    int currentQuantity = getCurrentQuantity();
                    int newQuantity = currentQuantity + 1;

                    // Update UI immediately
                    if (quantityText != null) {
                        quantityText.setText(String.valueOf(newQuantity));
                    }
                    updateButtonStates(newQuantity);

                    // Notify listener (isIncreasing = true)
                    if (listener != null) {
                        listener.onQuantityChanged(cartItem, getBindingAdapterPosition(), newQuantity, true);
                    }
                });
            }

            // Delete button click listener
            if (deleteIcon != null) {
                deleteIcon.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteItem(cartItem, getBindingAdapterPosition());
                    }
                });
            }
        }

        // FIXED: Helper method to get current quantity from TextView
        private int getCurrentQuantity() {
            if (quantityText != null) {
                try {
                    String quantityStr = quantityText.getText().toString();
                    return Integer.parseInt(quantityStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }

        private void updateButtonStates(int quantity) {
            // Update decrement button state
            if (decrementButton != null) {
                boolean canDecrement = quantity > 0;
                decrementButton.setEnabled(canDecrement);
                decrementButton.setAlpha(canDecrement ? 1.0f : 0.3f);
            }

            // Increment button is always enabled
            if (incrementButton != null) {
                incrementButton.setEnabled(true);
                incrementButton.setAlpha(1.0f);
            }
        }
    }
}