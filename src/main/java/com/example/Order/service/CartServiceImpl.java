package com.example.Order.service;

import com.example.Order.model.Cart;
import com.example.Order.model.CartItem;
import com.example.Order.model.Order;
import com.example.Order.model.OrderItem;
import com.example.Order.repository.CartItemRepository;
import com.example.Order.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public Cart addItemToCart(String email, CartItem cartItem, Long restaurantId) {
        Cart cart = cartRepository.findByEmail(email).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setEmail(email);
            newCart.setRestaurantId(restaurantId);
            return cartRepository.save(newCart);
        });

        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getItemId().equals(cartItem.getItemId()))
                .findFirst();

        if (existingCartItem.isPresent()) {
            existingCartItem.get().setQuantity(existingCartItem.get().getQuantity() + cartItem.getQuantity());
        } else {
            cart.addCartItem(cartItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Optional<Cart> getCartByEmail(String email) {
        return cartRepository.findByEmail(email);
    }

    @Override
    public void removeItemFromCart(String email, Long itemId) {
        cartRepository.findByEmail(email).ifPresent(cart -> {
            cart.getCartItems().removeIf(item -> item.getItemId().equals(itemId));
            if (cart.getCartItems().isEmpty()) {
                cartRepository.delete(cart);
            } else {
                cartRepository.save(cart);
            }
        });
    }

    @Override
    public void updateItemInCart(String email, Long itemId) {
        cartRepository.findByEmail(email).ifPresent(cart -> {
            cart.getCartItems().forEach(item -> {
                if (item.getItemId().equals(itemId)) {
                    item.setQuantity(item.getQuantity() + 1);
                }
            });
            cartRepository.save(cart);
        });
    }

    @Override
    public void clearCart(String email) {
        cartRepository.findByEmail(email).ifPresent(cartRepository::delete);
    }

    @Override
    public Order convertCartToOrder(String email) {
        Cart cart = cartRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + email));

        Order order = new Order();
        order.setEmail(cart.getEmail());
        order.setRestaurantId(cart.getRestaurantId());
        List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setItemId(cartItem.getItemId());
            orderItem.setName(cartItem.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            return orderItem;
        }).toList();
        order.setOrderItems(orderItems);
        return order;
    }

}
