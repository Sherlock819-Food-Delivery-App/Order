package com.example.Order.service;

import com.example.Order.model.Cart;
import com.example.Order.model.CartItem;

import java.util.Optional;

public interface CartService {
    Cart addItemToCart(String email, CartItem cartItem, Long restaurantId);

    Optional<Cart> getCartByEmail(String email);

    void removeItemFromCart(String email, Long itemId);

    void updateItemInCart(String email, Long itemId);

    void clearCart(String email);
}
