<template>
  <div class="page">
    <h2>👨‍🍳 后厨订单</h2>
    <button @click="loadOrders">刷新订单</button>

    <div class="order-item" v-for="order in orderList" :key="order.id">
      <p>桌号：{{ order.tableNum }}</p>
      <p>菜品：{{ order.dishName }} × {{ order.quantity }}</p>
      <p>金额：￥{{ order.price }}</p>
      <p>状态：{{ order.status }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "KitchenView",
  data() {
    return {
      orderList: []
    };
  },
  mounted() {
    this.loadOrders();
  },
  methods: {
    async loadOrders() {
      let res = await fetch("http://localhost:8080/order/kitchen");
      this.orderList = await res.json();
    }
  }
};
</script>

<style scoped>
.page { padding: 20px; }
.order-item { padding: 15px; border: 1px solid #ddd; margin: 10px 0; }
</style>