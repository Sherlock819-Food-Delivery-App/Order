package com.example.Order.utilities;

import com.example.Order.dto.OrderDTO;
import com.example.Order.model.Order;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    // Convert entity to DTO
    OrderDTO toDTO(Order order);

    // Convert DTO to entity
    Order toEntity(OrderDTO orderDTO);

    // List of Order -> List of OrderDTO
    List<OrderDTO> toDTO(List<Order> orders);

    // List of OrderDTO -> List of Order
    List<Order> toEntity(List<OrderDTO> orderDTOs);

    @AfterMapping
    default void setOrderItem(@MappingTarget Order order) {
        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(items -> items.setOrder(order));
        }
    }
}
