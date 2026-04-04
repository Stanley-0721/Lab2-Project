<template>
  <div class="cashier-page">
    <h2>💳 前台结账</h2>

    <!-- 桌号选择 -->
    <div class="table-select">
      <h4>选择桌号：</h4>
      <div class="table-btns">
        <button
          v-for="n in 10"
          :key="n"
          :class="{active: selectedTable === String(n)}"
          @click="selectTable(String(n))"
        >
          {{ n }}
        </button>
      </div>
    </div>

    <!-- 当前桌订单 -->
    <div class="order-list" v-if="selectedTable">
      <h4>📄 {{ selectedTable }} 号桌订单</h4>

      <div class="order-item" v-for="order in orderList" :key="order.id">
        <div>菜品：{{ order.dishName }} × {{ order.quantity }}</div>
        <div>单价：¥ {{ order.price }}</div>
        <div>状态：{{ order.status }}</div>
      </div>

      <div class="total" v-if="orderList.length > 0">
        合计金额：<span>¥ {{ totalPrice }}</span>
      </div>

      <button class="checkout-btn" @click="checkout" :disabled="checking">
        确认结账
      </button>
    </div>

    <div class="empty" v-if="selectedTable && orderList.length === 0">
      该桌暂无未结账订单
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      selectedTable: null,
      orderList: [],
      checking: false
    }
  },

  computed: {
    // 计算总价
    totalPrice() {
      return this.orderList.reduce((sum, item) => {
        return sum + (item.price || 0) * item.quantity
      }, 0).toFixed(2)
    }
  },

  methods: {
    // 选择桌号 → 查询订单
    async selectTable(tableNum) {
      this.selectedTable = tableNum
      let res = await fetch("http://localhost:8080/order/cashier/" + tableNum)
      this.orderList = await res.json()
    },

    // 结账
    async checkout() {
      if (!confirm("确认结账？")) return

      this.checking = true
      let res = await fetch("http://localhost:8080/order/checkout/" + this.selectedTable)
      let text = await res.text()

      alert(text)
      this.selectTable(this.selectedTable) // 刷新
      this.checking = false
    }
  }
}
</script>

<style scoped>
.cashier-page {
  padding: 24px;
  max-width: 700px;
  margin: 0 auto;
}

.table-select {
  margin-bottom: 20px;
}

.table-btns {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.table-btns button {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  border: 1px solid #ccc;
  background: white;
  font-size: 16px;
  cursor: pointer;
}

.table-btns button.active {
  background: #0d6efd;
  color: white;
  border-color: #0d6efd;
}

.order-list {
  margin-top: 20px;
}

.order-item {
  padding: 12px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 8px;
}

.total {
  font-size: 18px;
  font-weight: bold;
  margin: 16px 0;
  color: #d93025;
}

.checkout-btn {
  background: #d93025;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}

.checkout-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.empty {
  color: #666;
  margin-top: 20px;
}
</style>