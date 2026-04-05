package com.example.restaurant.controller;

import com.example.restaurant.entity.Dish;
import com.example.restaurant.entity.Order;
import com.example.restaurant.repository.DishRepository;
import com.example.restaurant.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DishRepository dishRepository;

    // ================== 顾客下单 ==================
    @PostMapping("/add")
    public String add(@RequestBody Order order) {
        order.setStatus("待制作");
        orderRepository.save(order);

        // ===================== 自动增加菜品销量 =====================
        Optional<Dish> dishOptional = dishRepository.findByName(order.getDishName());
        if (dishOptional.isPresent()) {
            Dish dish = dishOptional.get();
            dish.setSales(dish.getSales() + order.getQuantity());
            dishRepository.save(dish);
        }

        return "ok";
    }

    // ================== 后厨：看【待制作】 ==================
    @GetMapping("/kitchen")
    public List<Order> kitchen() {
        return orderRepository.findByStatus("待制作");
    }

    // 后厨：完成制作 → 改为“已完成”
    @GetMapping("/kitchen/finish/{id}")
    public String kitchenFinish(@PathVariable Long id) {
        Order o = orderRepository.findById(id).orElse(null);
        if (o != null) {
            o.setStatus("已完成");
            orderRepository.save(o);
        }
        return "ok";
    }

    // ================== 服务员：看【已完成】待上菜 ==================
    @GetMapping("/waiter")
    public List<Order> waiter() {
        return orderRepository.findByStatus("已完成");
    }

    // 服务员：上菜 → 改为“已上菜”
    @GetMapping("/waiter/serve/{id}")
    public String serve(@PathVariable Long id) {
        Order o = orderRepository.findById(id).orElse(null);
        if (o != null) {
            o.setStatus("已上菜");
            orderRepository.save(o);
        }
        return "ok";
    }

    // ================== 前台：看某桌是否全部上菜 → 可结账 ==================
    @GetMapping("/cashier/{tableNum}")
    public List<Order> cashierTable(@PathVariable String tableNum) {
        return orderRepository.findByTableNumAndStatusNot(tableNum, "已结账");
    }

    // 前台：结账
    @GetMapping("/checkout/{tableNum}")
    public String checkout(@PathVariable String tableNum) {
        List<Order> orders = orderRepository.findByTableNumAndStatusNot(tableNum, "已结账");

        for (Order o : orders) {
            if (o.getStatus().equals("待制作") || o.getStatus().equals("已完成")) {
                return "还有未上菜的菜品，不能结账";
            }
        }

        for (Order o : orders) {
            o.setStatus("已结账");
            orderRepository.save(o);
        }
        return "结账成功";
    }

    // ===================== 老板页面接口 =====================
    @GetMapping("/boss/all")
    public List<Order> bossAll() {
        return orderRepository.findAll();
    }

    @GetMapping("/boss/stat")
    public Map<String, Object> bossStat() {
        List<Order> list = orderRepository.findAll();
        Map<String, Object> map = new HashMap<>();

        Set<String> orderSet = new HashSet<>();
        double totalMoney = 0;
        for (Order o : list) {
            String key = o.getOrderNo() != null ? o.getOrderNo() : "NO_ID_" + o.getId();
            orderSet.add(key);
            totalMoney += o.getPrice() * o.getQuantity();
        }

        map.put("totalCount", orderSet.size());
        map.put("totalMoney", totalMoney);
        map.put("lastTime", list.isEmpty() ? "" : list.get(list.size()-1).getCreateTime());
        return map;
    }

    @GetMapping("/boss/yearly")
    public List<Map<String, Object>> bossYearly() {
        List<Order> list = orderRepository.findAll();
        Map<String, Map<String, Object>> yearMap = new HashMap<>();

        for (Order o : list) {
            if (o.getCreateTime() == null) continue;
            String year = o.getCreateTime().toString().split("-")[0];
            String orderKey = o.getOrderNo() != null ? o.getOrderNo() : "NO_ID_" + o.getId();

            if (!yearMap.containsKey(year)) {
                Map<String, Object> item = new HashMap<>();
                item.put("year", year);
                item.put("orderSet", new HashSet<String>());
                item.put("total", 0.0);
                yearMap.put(year, item);
            }

            Map<String, Object> item = yearMap.get(year);
            ((Set<String>) item.get("orderSet")).add(orderKey);
            item.put("total", (Double) item.get("total") + o.getPrice() * o.getQuantity());
        }

        List<Map<String, Object>> res = new ArrayList<>();
        for (Map<String, Object> item : yearMap.values()) {
            item.put("count", ((Set<String>) item.get("orderSet")).size());
            item.remove("orderSet");
            res.add(item);
        }
        return res;
    }

    @GetMapping("/boss/monthly")
    public List<Map<String, Object>> bossMonthly() {
        List<Order> list = orderRepository.findAll();
        Map<String, Map<String, Object>> monthMap = new HashMap<>();

        for (Order o : list) {
            if (o.getCreateTime() == null) continue;
            String month = o.getCreateTime().toString().substring(0, 7);
            String orderKey = o.getOrderNo() != null ? o.getOrderNo() : "NO_ID_" + o.getId();

            if (!monthMap.containsKey(month)) {
                Map<String, Object> item = new HashMap<>();
                item.put("month", month);
                item.put("orderSet", new HashSet<String>());
                item.put("total", 0.0);
                monthMap.put(month, item);
            }

            Map<String, Object> item = monthMap.get(month);
            ((Set<String>) item.get("orderSet")).add(orderKey);
            item.put("total", (Double) item.get("total") + o.getPrice() * o.getQuantity());
        }

        List<Map<String, Object>> res = new ArrayList<>();
        for (Map<String, Object> item : monthMap.values()) {
            item.put("count", ((Set<String>) item.get("orderSet")).size());
            item.remove("orderSet");
            res.add(item);
        }
        return res;
    }
}