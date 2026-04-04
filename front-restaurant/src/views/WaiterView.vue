<template>
  <div class="page">
    <h2>🍽️ 服务员 - 已完成菜品</h2>
    <button @click="load">刷新</button>

    <div class="item" v-for="o in orderList" :key="o.id">
      <p>桌号：{{ o.tableNum }}</p>
      <p>菜品：{{ o.dishName }} × {{ o.quantity }}</p>
      <button @click="serve(o.id)">✅ 已上菜</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return { orderList: [] };
  },
  mounted() { this.load(); },
  methods: {
    async load() {
      let r = await fetch("http://localhost:8080/order/waiter");
      this.orderList = await r.json();
    },
    async serve(id) {
      await fetch("http://localhost:8080/order/serve/" + id);
      this.load();
    }
  }
};
</script>

<style scoped>
.page{padding:20px}
.item{padding:15px;border:1px solid #ddd;margin:10px 0}
button{padding:6px 12px}
</style>