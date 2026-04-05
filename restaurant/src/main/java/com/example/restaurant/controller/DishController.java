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

    // 添加菜品
    @PostMapping("/add")
    public Dish add(@RequestBody Dish dish) {
        dish.setSales(0);
        return repo.save(dish);
    }

    // 修改价格
    @PostMapping("/update/{id}")
    public Dish update(@PathVariable Long id, @RequestBody Dish dish) {
        Dish old = repo.findById(id).orElseThrow();
        old.setPrice(dish.getPrice());
        old.setName(dish.getName());
        old.setCategory(dish.getCategory());
        return repo.save(old);
    }

    // 下架
    @GetMapping("/down/{id}")
    public String down(@PathVariable Long id) {
        Dish dish = repo.findById(id).orElseThrow();
        dish.setPrice(-1.0);
        repo.save(dish);
        return "success";
    }

    // 上架
    @GetMapping("/up/{id}")
    public String up(@PathVariable Long id) {
        Dish dish = repo.findById(id).orElseThrow();
        if (dish.getPrice() < 0) {
            dish.setPrice(1.0);
        }
        repo.save(dish);
        return "success";
    }
}