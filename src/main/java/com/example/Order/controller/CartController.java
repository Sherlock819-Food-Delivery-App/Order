package com.example.Order.controller;

import com.example.Order.dto.CartDTO;
import com.example.Order.dto.CartItemDTO;
import com.example.Order.dto.CartRequestDTO;
import com.example.Order.model.CartItem;
import com.example.Order.service.CartService;
import com.example.Order.utilities.CartMapper;
import com.example.Order.utilities.CartItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @Autowired
    public CartController(CartService cartService, CartMapper cartMapper, CartItemMapper cartItemMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
    }

    @GetMapping
    public ResponseEntity<CartDTO> getCart() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return cartService.getCartByEmail(email)
                .map(cart -> ResponseEntity.ok(cartMapper.toDTO(cart)))
                .orElse(ResponseEntity.ok(null));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(@RequestBody CartRequestDTO cartRequestDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CartItem cartItem = cartItemMapper.toEntity(toCartItemDTO(cartRequestDTO));
        var updatedCart = cartService.addItemToCart(email, cartItem, cartRequestDTO.getRestaurantId());
        return ResponseEntity.ok(cartMapper.toDTO(updatedCart));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<Void> updateItemInCart(@PathVariable Long itemId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.updateItemInCart(email, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long itemId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.removeItemFromCart(email, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.clearCart(email);
        return ResponseEntity.noContent().build();
    }

    public CartItemDTO toCartItemDTO(CartRequestDTO cartRequestDTO) {
        return CartItemDTO.builder()
                .cartItemId(cartRequestDTO.getCartItemId())
                .itemId(cartRequestDTO.getItemId())
                .name(cartRequestDTO.getName())
                .quantity(cartRequestDTO.getQuantity())
                .price(cartRequestDTO.getPrice())
                .build();
    }
}
