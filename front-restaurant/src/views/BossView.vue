<template>
  <div class="page">
    <h2>📊 老板查看订单（按订单号分组）</h2>
    <button @click="load">刷新</button>

    <div class="order-box" v-for="(items, no) in orders" :key="no">
      <h3>订单号：{{ no }}</h3>
      <p>桌号：{{ items[0].tableNum }}</p>
      <p>时间：{{ items[0].createTime }}</p>

      <div v-for="o in items" :key="o.id">
        🍽 {{ o.dishName }} × {{ o.quantity }} — {{ o.status }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return { orders: {} };
  },
  mounted() { this.load(); },
  methods: {
    async load() {
      let r = await fetch("http://localhost:8080/order/boss");
      let data = await r.json();
      this.orders = data.orders;
    }
  }
};
</script>

<style scoped>
.page{padding:20px}
.order-box{padding:20px;border:1px solid #ccc;margin:15px 0}
</style>