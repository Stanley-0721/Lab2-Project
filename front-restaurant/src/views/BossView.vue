<template>
  <div class="page">
    <h2>📊 老板查看所有订单</h2>
    <button @click="loadAll">刷新</button>

    <div class="order-item" v-for="order in orderList" :key="order.id">
      <p>订单号：{{ order.id }}</p>
      <p>桌号：{{ order.tableNum }}</p>
      <p>菜品：{{ order.dishName }}</p>
      <p>状态：{{ order.status }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "BossView",
  data() {
    return {
      orderList: []
    };
  },
  mounted() {
    this.loadAll();
  },
  methods: {
    async loadAll() {
      let res = await fetch("http://localhost:8080/order/all");
      this.orderList = await res.json();
    }
  }
};
</script>

<style scoped>
.page { padding: 20px; }
.order-item { padding: 15px; border: 1px solid #ccc; margin: 10px 0; }
</style>