package com.example.restaurant.controller;

import com.example.restaurant.entity.Order;
import com.example.restaurant.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

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

    // ===================== 老板页增强版（最终版） =====================
// 1. 查看所有订单
    @GetMapping("/boss/all")
    public List<Order> bossAll() {
        List<Order> list = orderRepository.findAll();
        list.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        return list;
    }

    // 2. 按月统计营收
    @GetMapping("/boss/monthly")
    public List<Map<String, Object>> bossMonthly() {
        List<Order> list = orderRepository.findAll();
        Map<String, Double> sumMap = new HashMap<>();
        Map<String, Integer> countMap = new HashMap<>();

        for (Order o : list) {
            if (!"已结账".equals(o.getStatus())) continue;
            String month = o.getCreateTime().toString().substring(0, 7);
            double money = o.getPrice() * o.getQuantity();
            sumMap.put(month, sumMap.getOrDefault(month, 0.0) + money);
            countMap.put(month, countMap.getOrDefault(month, 0) + 1);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : sumMap.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("month", key);
            map.put("total", sumMap.get(key));
            map.put("count", countMap.get(key));
            result.add(map);
        }
        return result;
    }

    // 3. 按年统计营收
    @GetMapping("/boss/yearly")
    public List<Map<String, Object>> bossYearly() {
        List<Order> list = orderRepository.findAll();
        Map<String, Double> sumMap = new HashMap<>();
        Map<String, Integer> countMap = new HashMap<>();

        for (Order o : list) {
            if (!"已结账".equals(o.getStatus())) continue;
            String year = o.getCreateTime().toString().substring(0, 4);
            double money = o.getPrice() * o.getQuantity();
            sumMap.put(year, sumMap.getOrDefault(year, 0.0) + money);
            countMap.put(year, countMap.getOrDefault(year, 0) + 1);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : sumMap.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("year", key);
            map.put("total", sumMap.get(key));
            map.put("count", countMap.get(key));
            result.add(map);
        }
        return result;
    }

    // 服务员：只看【已完成】的菜
    @GetMapping("/waiter")
    public List<Order> waiter() {
        return orderRepository.findByStatus("已完成");
    }

    // 服务员：标记【已上菜】
    @GetMapping("/serve/{id}")
    public String serve(@PathVariable Long id) {
        Order o = orderRepository.findById(id).orElse(null);
        if (o != null) {
            o.setStatus("已上菜");
            orderRepository.save(o);
        }
        return "ok";
    }

    // 老板：总统计卡片（已修复时间类型错误）
    @GetMapping("/boss/stat")
    public Map<String, Object> bossStat() {
        List<Order> list = orderRepository.findAll();
        int totalCount = 0;
        double totalMoney = 0.0;
        LocalDateTime lastTime = null;

        for (Order o : list) {
            if (!"已结账".equals(o.getStatus())) continue;

            totalCount++;
            totalMoney += o.getPrice() * o.getQuantity();

            LocalDateTime currentTime = o.getCreateTime();
            if (lastTime == null || currentTime.isAfter(lastTime)) {
                lastTime = currentTime;
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("totalCount", totalCount);
        map.put("totalMoney", totalMoney);
        map.put("lastTime", lastTime != null ? lastTime.toString() : "");

        return map;
    }

    // 前台：按桌号汇总所有未结账订单 + 算金额
    @GetMapping("/reception")
    public Map<String, Object> reception() {
        List<Order> all = orderRepository.findAll();
        Map<String, List<Order>> groupByTable = all.stream()
                .filter(o -> !"已结账".equals(o.getStatus()))
                .collect(Collectors.groupingBy(Order::getTableNum));

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, List<Order>> entry : groupByTable.entrySet()) {
            String table = entry.getKey();
            List<Order> orders = entry.getValue();

            double total = 0;
            for (Order o : orders) {
                total += o.getPrice() * o.getQuantity();
            }

            Map<String, Object> tableInfo = new HashMap<>();
            tableInfo.put("orders", orders);
            tableInfo.put("total", total);
            result.put(table, tableInfo);
        }
        return result;
    }

    // 前台：结账 → 整桌状态改为已结账（增强健壮性）
    @GetMapping("/checkout/{tableNum}")
    public String checkout(@PathVariable String tableNum) {
        List<Order> orders = orderRepository.findAll()
                .stream()
                .filter(o -> tableNum.equals(o.getTableNum()))
                .toList();

        // 健壮性判断：只要有一个菜不是【已上菜】，就不让结账
        boolean canCheckout = true;
        for (Order o : orders) {
            if (!"已上菜".equals(o.getStatus())) {
                canCheckout = false;
                break;
            }
        }

        if (!canCheckout) {
            return "不能结账：该桌还有未上菜的菜品！";
        }

        // 全部已上菜 → 允许结账
        for (Order o : orders) {
            o.setStatus("已结账");
            orderRepository.save(o);
        }
        return "结账成功！";
    }
}