package com.example.restaurant.controller;

import com.example.restaurant.entity.Dish;
import com.example.restaurant.repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishRepository dishRepository;

    @GetMapping("/list")
    public List<Dish> list() {
        return dishRepository.findAll();
    }
}