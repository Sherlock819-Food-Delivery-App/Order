package com.example.Order.utilities;

import com.example.Order.dto.CartItemDTO;
import com.example.Order.model.CartItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItemDTO toDTO(CartItem cartItem);

    CartItem toEntity(CartItemDTO cartItemDTO);
}
