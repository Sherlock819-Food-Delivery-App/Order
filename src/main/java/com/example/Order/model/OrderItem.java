package com.example.Order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_items")  // Changed table name for consistency
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)  // Updated column name to snake_case for consistency
    @JsonBackReference
    private Order order;

    @Column(nullable = false)  // Ensure itemId and quantity are not nullable
    private Long itemId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)  // Ensure quantity is not nullable
    private int quantity;

    @Column(nullable = false)  // Ensure price is not nullable
    private Double price;

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", itemId=" + itemId +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
