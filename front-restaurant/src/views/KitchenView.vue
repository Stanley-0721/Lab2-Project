<template>
  <div class="page">
    <h2>👨‍🍳 后厨操作台</h2>

    <!-- 按桌号分组后的列表 -->
    <div class="table-card" v-for="(tableData, tableNum) in tableMap" :key="tableNum">
      <div class="table-header">
        桌号：{{ tableNum }} 号桌
        <!-- 等待时间 → 超过30分钟变红 -->
        <span class="wait-time" :class="{ red: tableData.waitMinutes >= 30 }">
          已等待：{{ tableData.waitTime }}
        </span>
      </div>

      <div class="dish-item" v-for="order in tableData.orders" :key="order.id">
        <div class="dish-info">
          {{ order.dishName }} × {{ order.quantity }}
        </div>
        <div class="status">{{ order.status }}</div>
        <button class="btn" @click="finish(order.id)">完成制作</button>
      </div>
    </div>

    <div v-if="Object.keys(tableMap).length === 0" class="empty">
      🎉 所有订单都已制作完成！
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
      let res = await fetch("http://localhost:8080/order/kitchen")
      this.list = await res.json()
    },
    async finish(id) {
      await fetch("http://localhost:8080/order/kitchen/finish/" + id)
      this.getList()
    }
  },
  computed: {
    tableMap() {
      let map = {}
      let now = new Date()

      // 只保留待制作
      let pendingOrders = this.list.filter(o => o.status === "待制作")

      for (let o of pendingOrders) {
        let table = o.tableNum
        if (!map[table]) {
          map[table] = { orders: [] }
        }
        map[table].orders.push(o)
      }

      // ================== 计算每桌等待时间 ==================
      for (let table in map) {
        let orders = map[table].orders

        // 取这桌最早的一单时间
        let createTimes = orders
          .filter(o => o.createTime)
          .map(o => new Date(o.createTime))

        if (createTimes.length === 0) {
          map[table].waitTime = "00:00"
          map[table].waitMinutes = 0
          continue
        }

        let minTime = new Date(Math.min(...createTimes))
        let diffMs = now - minTime

        let min = Math.floor(diffMs / 1000 / 60)
        let sec = Math.floor((diffMs / 1000) % 60)

        map[table].waitTime = `${String(min).padStart(2, '0')}:${String(sec).padStart(2, '0')}`
        map[table].waitMinutes = min
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
  color: #0d6efd;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 等待时间样式 */
.wait-time {
  font-size: 14px;
  color: #666;
  font-weight: normal;
}
/* 超时30分钟 → 红色 */
.wait-time.red {
  color: #e53935 !important;
  font-weight: bold;
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
  background: #0d6efd;
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