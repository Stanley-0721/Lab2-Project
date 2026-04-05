<template>
  <div class="page">
    <h2>💁‍♂️ 服务员上菜</h2>

    <div class="table-card" v-for="(tableData, tableNum) in tableMap" :key="tableNum">
      <div class="table-header">
        桌号：{{ tableNum }} 号桌
      </div>

      <div class="dish-item" v-for="order in tableData.orders" :key="order.id">
        <div class="dish-info">
          {{ order.dishName }} × {{ order.quantity }}
        </div>
        <div class="status">{{ order.status }}</div>
        <button class="btn" @click="serve(order.id)">确认上菜</button>
      </div>
    </div>

    <div v-if="Object.keys(tableMap).length === 0" class="empty">
      🎉 所有菜品已全部上菜完成！
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      list: []
    }
  },
  mounted() {
    this.getList()
    setInterval(() => this.getList(), 2000)
  },
  methods: {
    async getList() {
      let res = await fetch("http://localhost:8080/order/waiter")
      this.list = await res.json()
    },
    async serve(id) {
      await fetch("http://localhost:8080/order/waiter/serve/" + id)
      this.getList()
    }
  },
  computed: {
    // 按桌号分组，只显示【已完成】待上菜的菜品
    tableMap() {
      let map = {}
      let readyOrders = this.list.filter(o => o.status === "已完成")

      for (let o of readyOrders) {
        let table = o.tableNum
        if (!map[table]) {
          map[table] = { orders: [] }
        }
        map[table].orders.push(o)
      }
      return map
    }
  }
}
</script>

<style scoped>
.page {
  padding: 24px;
  background: #f7f8fa;
  min-height: 100vh;
}

h2 {
  margin-bottom: 24px;
  color: #333;
}

.table-card {
  background: white;
  border-radius: 12px;
  padding: 18px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.table-header {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
  color: #28a745;
}

.dish-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.dish-item:last-child {
  border-bottom: none;
}

.dish-info {
  flex: 1;
  font-size: 15px;
}

.status {
  margin-right: 12px;
  color: #666;
  font-size: 14px;
}

.btn {
  background: #28a745;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
}

.empty {
  font-size: 16px;
  color: #888;
  text-align: center;
  padding: 40px;
}
</style>