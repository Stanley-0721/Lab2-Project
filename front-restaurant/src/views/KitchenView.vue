<template>
  <div class="page">
    <h2>👨‍🍳 后厨订单</h2>
    <button @click="loadOrders">刷新</button>

    <div class="item" v-for="o in orderList" :key="o.id">
      <p>订单号：{{ o.orderNo }}</p>
      <p>桌号：{{ o.tableNum }}</p>
      <p>菜：{{ o.dishName }} × {{ o.quantity }}</p>
      <p>状态：{{ o.status }}</p>
      <button @click="finish(o.id)">✅ 标记为已完成</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return { orderList: [] };
  },
  mounted() { this.loadOrders(); },
  methods: {
    async loadOrders() {
      let r = await fetch("http://localhost:8080/order/kitchen");
      this.orderList = await r.json();
    },
    async finish(id) {
      await fetch("http://localhost:8080/order/finish/" + id);
      this.loadOrders();
    }
  }
};
</script>

<style scoped>
.page{padding:20px}
.item{padding:15px;border:1px solid #ddd;margin:10px 0}
button{padding:6px 10px}
</style>