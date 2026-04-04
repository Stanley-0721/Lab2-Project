package com.example.restaurant.controller;

import com.example.restaurant.entity.Order;
import com.example.restaurant.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    // 下单（自动生成订单号）
    @PostMapping("/add")
    public String add(@RequestBody List<Order> orders) {
        String orderNo = "O" + System.currentTimeMillis();
        for (Order o : orders) {
            o.setOrderNo(orderNo);
            orderRepository.save(o);
        }
        return "下单成功，订单号：" + orderNo;
    }

    // 后厨看待制作
    @GetMapping("/kitchen")
    public List<Order> kitchen() {
        return orderRepository.findByStatus("待制作");
    }

    // 后厨 → 改状态为已完成
    @GetMapping("/finish/{id}")
    public String finish(@PathVariable Long id) {
        Order o = orderRepository.findById(id).orElse(null);
        if (o != null) {
            o.setStatus("已完成");
            orderRepository.save(o);
        }
        return "ok";
    }

    // 老板看所有订单（按订单号分组）
    @GetMapping("/boss")
    public Map<String, Object> boss() {
        List<Order> all = orderRepository.findAll();

        Map<String, List<Order>> map = all.stream()
                .collect(Collectors.groupingBy(Order::getOrderNo));

        Map<String, Object> result = new HashMap<>();
        result.put("orders", map);
        return result;
    }
}