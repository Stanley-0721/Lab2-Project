<template>
  <div class="page">
    <h2>💁‍♂️ 服务员上菜</h2>
    <div class="order-list">
      <div class="order-item" v-for="order in list" :key="order.id">
        <div>桌号：{{ order.tableNum }} 号桌</div>
        <div>菜品：{{ order.dishName }} × {{ order.quantity }}</div>
        <div>状态：{{ order.status }}</div>
        <button @click="serve(order.id)">确认上菜</button>
      </div>
      <div v-if="list.length === 0">暂无需要上菜的订单</div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return { list: [] }
  },
  mounted() { this.getList() },
  methods: {
    async getList() {
      let res = await fetch("http://localhost:8080/order/waiter")
      this.list = await res.json()
    },
    async serve(id) {
      await fetch("http://localhost:8080/order/waiter/serve/"+id)
      alert("已上菜！")
      this.getList()
    }
  }
}
</script>

<style scoped>
.page { padding: 20px; }
.order-item {
  border:1px solid #ddd;
  padding:15px;
  margin:10px 0;
  border-radius:8px;
}
.order-item button {
  background: #28a745;
  color:white;
  border:none;
  padding:8px 14px;
  border-radius:6px;
  cursor:pointer;
  margin-top:8px;
}
</style>