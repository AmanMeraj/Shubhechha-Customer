package com.subh.shubhechha.Adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.subh.shubhechha.Model.CartItem;
import com.subh.shubhechha.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartItemListener listener;
    private DecimalFormat priceFormat;

    // Interface for callbacks
    public interface OnCartItemListener {
        void onQuantityChanged(CartItem item, int position, int newQuantity);
        void onDeleteItem(CartItem item, int position);
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
        CartItem item = cartItems.get(position);
        if (item != null) {
            holder.bind(item, position);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public void setCartItems(List<CartItem> items) {
        if (items != null) {
            this.cartItems = new ArrayList<>(items);
            notifyDataSetChanged();
        }
    }

    public void addItem(CartItem item) {
        if (item != null) {
            cartItems.add(item);
            notifyItemInserted(cartItems.size() - 1);
        }
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
        }
    }

    public void updateQuantity(int position, int newQuantity) {
        if (position >= 0 && position < cartItems.size()) {
            CartItem item = cartItems.get(position);
            if (item != null) {
                item.setQuantity(newQuantity);
                notifyItemChanged(position);
            }
        }
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public double getTotalAmount() {
        double total = 0;
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                if (item != null) {
                    total += item.getTotalPrice();
                }
            }
        }
        return total;
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

        public void bind(CartItem item, int position) {
            if (item == null) return;

            // Set product name
            if (productName != null) {
                productName.setText(item.getProductName() != null ? item.getProductName() : "");
            }

            // Set product description
            if (productDescription != null) {
                productDescription.setText(item.getProductDescription() != null ? item.getProductDescription() : "");
            }

            // Set original price with strikethrough
            if (originalPrice != null) {
                originalPrice.setText("₹ " + priceFormat.format(item.getOriginalPrice()));
            }

            // Set current price
            if (currentPrice != null) {
                currentPrice.setText("₹ " + priceFormat.format(item.getCurrentPrice()));
            }

            // Set quantity
            if (quantityText != null) {
                quantityText.setText(String.valueOf(item.getQuantity()));
            }

            // Load product image using Glide (crash-safe)
            if (productImage != null) {
                try {
                    Glide.with(itemView.getContext())
                            .load(item.getImageUrl())
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
            updateButtonStates(item);

            // Decrement button click listener
            if (decrementButton != null) {
                decrementButton.setOnClickListener(v -> {
                    if (item.canDecrement()) {
                        int newQuantity = item.getQuantity() - 1;
                        item.setQuantity(newQuantity);

                        // Update UI immediately for smooth experience
                        if (quantityText != null) {
                            quantityText.setText(String.valueOf(newQuantity));
                        }
                        updateButtonStates(item);

                        // Notify listener
                        if (listener != null) {
                            listener.onQuantityChanged(item, position, newQuantity);
                        }
                    }
                });
            }

            // Increment button click listener
            if (incrementButton != null) {
                incrementButton.setOnClickListener(v -> {
                    if (item.canIncrement()) {
                        int newQuantity = item.getQuantity() + 1;
                        item.setQuantity(newQuantity);

                        // Update UI immediately for smooth experience
                        if (quantityText != null) {
                            quantityText.setText(String.valueOf(newQuantity));
                        }
                        updateButtonStates(item);

                        // Notify listener
                        if (listener != null) {
                            listener.onQuantityChanged(item, position, newQuantity);
                        }
                    }
                });
            }

            // Delete button click listener
            if (deleteIcon != null) {
                deleteIcon.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteItem(item, position);
                    }
                });
            }
        }

        private void updateButtonStates(CartItem item) {
            if (item == null) return;

            // Update decrement button state
            if (decrementButton != null) {
                decrementButton.setEnabled(item.canDecrement());
                decrementButton.setAlpha(item.canDecrement() ? 1.0f : 0.3f);
            }

            // Update increment button state
            if (incrementButton != null) {
                incrementButton.setEnabled(item.canIncrement());
                incrementButton.setAlpha(item.canIncrement() ? 1.0f : 0.3f);
            }
        }
    }
}
