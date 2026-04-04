package com.example.restaurant.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dish")
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;
    private String image;

    // 新增
    private String category; // 分类
    private int sales;      // 销量

    public Dish() {}

    // getter & setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    // 新增的 getter/setter
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getSales() { return sales; }
    public void setSales(int sales) { this.sales = sales; }
}