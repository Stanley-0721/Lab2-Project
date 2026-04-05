package com.example.restaurant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dishName;
    private Double price;
    private Integer quantity;
    private String tableNum;
    private String status;
    private LocalDateTime createTime;

    // 订单号
    private String orderNo;

    public Order() {}

    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
    }

    // ===================== 全部 getter & setter =====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDishName() { return dishName; }
    public void setDishName(String dishName) { this.dishName = dishName; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getTableNum() { return tableNum; }
    public void setTableNum(String tableNum) { this.tableNum = tableNum; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    // ✅ 关键：你之前缺少这两个！！！
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
}