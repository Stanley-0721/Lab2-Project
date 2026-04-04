package com.example.restaurant.controller;

import com.example.restaurant.entity.Dish;
import com.example.restaurant.repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishRepository repo;

    // 获取所有菜品
    @GetMapping("/all")
    public List<Dish> getAll() {
        return repo.findAll();
    }
}