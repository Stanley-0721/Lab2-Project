package com.example.restaurant.controller;

import com.example.restaurant.entity.Order;
import com.example.restaurant.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    // 顾客下单
    @PostMapping("/add")
    public Order add(@RequestBody Order order) {
        return orderRepository.save(order);
    }

    // 后厨看待制作订单
    @GetMapping("/kitchen")
    public List<Order> kitchen() {
        return orderRepository.findByStatus("待制作");
    }

    // 老板看所有订单
    @GetMapping("/all")
    public List<Order> all() {
        return orderRepository.findAll();
    }
}