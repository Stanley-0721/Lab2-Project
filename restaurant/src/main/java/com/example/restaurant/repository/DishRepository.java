package com.example.restaurant.repository;

import com.example.restaurant.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findByCategory(String category);
    Optional<Dish> findByName(String name);
}