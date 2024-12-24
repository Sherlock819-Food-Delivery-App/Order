package com.example.Order.utilities;

import com.example.Order.dto.OrderItemDTO;
import com.example.Order.model.OrderItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    // Convert entity to DTO
    OrderItemDTO toDTO(OrderItem orderItem);

    // Convert DTO to entity
    OrderItem toEntity(OrderItemDTO orderItemDTO);

    // List of OrderItem -> List of OrderItemDTO
    List<OrderItemDTO> toDTO(List<OrderItem> orderItems);

    // List of OrderItemDTO -> List of OrderItem
    List<OrderItem> toEntity(List<OrderItemDTO> orderItemDTOs);
}
