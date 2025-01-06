package com.example.Order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartRequestDTO {
    private Long cartItemId;
    private Long itemId;
    private Long restaurantId;
    private String name;
    private int quantity;
    private Double price;
}
