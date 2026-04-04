<template>
  <div class="page">
    <h2>👨‍🍳 后厨操作台</h2>
    <div class="order-list">
      <div class="order-item" v-for="order in list" :key="order.id">
        <div>桌号：{{ order.tableNum }} 号桌</div>
        <div>菜品：{{ order.dishName }} × {{ order.quantity }}</div>
        <div>状态：{{ order.status }}</div>
        <button @click="finish(order.id)">完成制作</button>
      </div>
      <div v-if="list.length === 0">暂无待制作订单</div>
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
      let res = await fetch("http://localhost:8080/order/kitchen")
      this.list = await res.json()
    },
    async finish(id) {
      await fetch("http://localhost:8080/order/kitchen/finish/"+id)
      alert("已完成制作")
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
  background:#0d6efd;
  color:white;
  border:none;
  padding:8px 14px;
  border-radius:6px;
  cursor:pointer;
  margin-top:8px;
}
</style>