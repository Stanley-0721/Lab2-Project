<template>
  <div class="page">
    <h2>💁‍♂️ 前台 - 结账管理</h2>
    <button @click="load">刷新</button>

    <div class="table-box" v-for="(info, tableNum) in tableData" :key="tableNum">
      <h3>桌号：{{ tableNum }}</h3>
      <div v-for="o in info.orders" :key="o.id">
        🍽 {{ o.dishName }} × {{ o.quantity }} — ￥{{ o.price }}
      </div>
      <p><b>总计：￥{{ info.total.toFixed(2) }}</b></p>
      <button @click="checkout(tableNum)">✅ 已结账</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return { tableData: {} };
  },
  mounted() { this.load(); },
  methods: {
    async load() {
      let r = await fetch("http://localhost:8080/order/reception");
      this.tableData = await r.json();
    },
    async checkout(tableNum) {
      await fetch("http://localhost:8080/order/checkout/" + tableNum);
      this.load();
    }
  }
};
</script>

<style scoped>
.page{padding:20px}
.table-box{padding:20px;border:1px solid #ccc;margin:15px 0}
button{padding:8px 14px}
</style>