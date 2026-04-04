package com.example.restaurant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dishName;    // 菜名
    private Double price;       // 价格
    private Integer quantity;   // 数量
    private String tableNum;    // 桌号
    private String status;      // 状态：待制作 / 已完成
    private LocalDateTime createTime;

    public Order() {
        this.createTime = LocalDateTime.now();
        this.status = "待制作";
    }

    // getter & setter
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
}