package com.example.Order.utilities;

import com.example.Order.dto.CartDTO;
import com.example.Order.model.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDTO toDTO(Cart cart);

    Cart toEntity(CartDTO cartDTO);
}
