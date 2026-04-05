<template>
  <div class="page">
    <h2>💳 前台收银台</h2>

    <!-- 10张桌台卡片 -->
    <div class="table-grid">
      <div
        class="table-card"
        :class="tableClass(tableNum)"
        v-for="tableNum in 10"
        :key="tableNum"
        @click="selectTable(String(tableNum))"
      >
        <div class="table-title">{{ tableNum }} 号桌</div>

        <!-- 显示该桌所有菜品 -->
        <div class="dish-list">
          <div class="dish" v-for="d in getTableDishes(String(tableNum))" :key="d.id">
            {{ d.dishName }} × {{ d.quantity }}
            <span class="status">{{ d.status }}</span>
          </div>
          <div v-if="getTableDishes(String(tableNum)).length === 0">
            空闲
          </div>
        </div>
      </div>
    </div>

    <!-- 选中后展开结账区域 -->
    <div class="check-area" v-if="selectedTable">
      <h3>📄 {{ selectedTable }} 号桌 · 结账</h3>

      <div class="order-item" v-for="order in orderList" :key="order.id">
        <div>菜品：{{ order.dishName }} × {{ order.quantity }}</div>
        <div>状态：{{ order.status }}</div>
        <div>单价：¥{{ order.price }}</div>
      </div>

      <div class="total" v-if="orderList.length">
        合计：¥{{ totalPrice }}
      </div>

      <button class="checkout" @click="checkout" :disabled="checking">
        确认结账
      </button>
    </div>

    <!-- ================== 虚拟发票弹窗 ================== -->
    <div class="invoice-modal" v-if="showInvoice">
      <div class="invoice">
        <div class="invoice-header">
          <h2>🍽️ 餐厅消费小票</h2>
          <p>感谢您的光临</p>
        </div>

        <div class="info">
          <div>桌号：{{ invoiceData.table }} 号桌</div>
          <div>时间：{{ invoiceData.time }}</div>
        </div>

        <div class="divider"></div>

        <div class="items">
          <div class="item" v-for="(d, idx) in invoiceData.items" :key="idx">
            <span>{{ d.name }} ×{{ d.count }}</span>
            <span>¥{{ d.price }}</span>
          </div>
        </div>

        <div class="divider"></div>

        <div class="total-price">
          合计：<span>¥{{ invoiceData.total }}</span>
        </div>

        <div class="invoice-footer">
          祝您用餐愉快
        </div>

        <button class="close-btn" @click="showInvoice = false">关闭</button>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  data() {
    return {
      selectedTable: null,
      orderList: [],
      checking: false,
      allOrders: [],
      // 发票相关
      showInvoice: false,
      invoiceData: {}
    }
  },

  mounted() {
    this.loadAllOrders()
    setInterval(() => this.loadAllOrders(), 2000)
  },

  computed: {
    totalPrice() {
      return this.orderList.reduce((s, i) => s + i.price * i.quantity, 0).toFixed(2)
    }
  },

  methods: {
    async loadAllOrders() {
      let res = await fetch("http://localhost:8080/order/boss/all")
      this.allOrders = await res.json()
    },

    getTableDishes(tableNum) {
      return this.allOrders.filter(o => o.tableNum === tableNum && o.status !== "已结账")
    },

    tableClass(tableNum) {
      let has = this.getTableDishes(String(tableNum)).length > 0
      return has ? "has-order" : "free"
    },

    async selectTable(tableNum) {
      this.selectedTable = tableNum
      let res = await fetch("http://localhost:8080/order/cashier/" + tableNum)
      this.orderList = await res.json()
    },

    // ================== 结账 + 弹出发票 ==================
    async checkout() {
      if (!confirm("确认结账？")) return
      this.checking = true

      let res = await fetch("http://localhost:8080/order/checkout/" + this.selectedTable)
      let text = await res.text()

      if (text === "结账成功") {
        // 生成发票数据
        this.invoiceData = {
          table: this.selectedTable,
          time: new Date().toLocaleString(),
          items: this.orderList.map(o => ({
            name: o.dishName,
            count: o.quantity,
            price: (o.price * o.quantity).toFixed(2)
          })),
          total: this.totalPrice
        }
        this.showInvoice = true
      }

      this.loadAllOrders()
      this.selectTable(this.selectedTable)
      this.checking = false
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

.table-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 30px;
}

.table-card {
  border-radius: 12px;
  padding: 14px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  cursor: pointer;
}

.table-card.free {
  background: #e6f7e6;
  border: 1px solid #96e696;
}

.table-card.has-order {
  background: #fff4e6;
  border: 1px solid #ffb86c;
}

.table-title {
  font-weight: bold;
  margin-bottom: 8px;
  font-size: 15px;
}

.dish-list {
  font-size: 13px;
  line-height: 1.4;
}

.dish {
  margin-bottom: 4px;
}

.status {
  color: #666;
  font-size: 12px;
  margin-left: 6px;
}

.check-area {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.order-item {
  padding: 10px;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}

.total {
  font-size: 18px;
  font-weight: bold;
  color: #e53935;
  margin: 16px 0;
}

.checkout {
  background: #e53935;
  color: white;
  border: none;
  padding: 10px 18px;
  border-radius: 8px;
  cursor: pointer;
}

.checkout:disabled {
  background: #ccc;
}

/* ================== 发票样式 ================== */
.invoice-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.invoice {
  background: white;
  width: 90%;
  max-width: 380px;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.2);
  font-family: "Microsoft YaHei";
}

.invoice-header {
  text-align: center;
  margin-bottom: 16px;
}

.invoice-header h2 {
  margin: 0;
  font-size: 18px;
}

.invoice-header p {
  margin: 4px 0;
  font-size: 12px;
  color: #666;
}

.info {
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 8px;
}

.divider {
  border-top: 1px dashed #ccc;
  margin: 10px 0;
}

.item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  margin: 4px 0;
}

.total-price {
  font-weight: bold;
  font-size: 16px;
  text-align: right;
  margin: 10px 0;
  color: #e53935;
}

.invoice-footer {
  text-align: center;
  font-size: 12px;
  color: #999;
  margin-top: 10px;
}

.close-btn {
  width: 100%;
  padding: 10px;
  background: #0d6efd;
  color: white;
  border: none;
  border-radius: 8px;
  margin-top: 16px;
  cursor: pointer;
}
</style>