<template>
  <div class="page">
    <h2>🍽 顾客点餐</h2>

    <div class="layout">
      <!-- 左侧分类 -->
      <div class="left">
        <h3>菜品分类</h3>
        <div
          class="cate"
          :class="{on: currentCate === item}"
          v-for="item in cateList"
          @click="currentCate = item"
        >
          {{ item }}
        </div>
      </div>

      <!-- 右侧菜品 -->
      <div class="right">
        <div class="card" v-for="d in showList" :key="d.id">
          <div class="name">{{ d.name }}</div>
          <div class="sales">销量：{{ d.sales }}</div>
          <div class="price">¥ {{ d.price }}</div>
          <button class="btn" @click="add(d)">+ 点餐</button>
        </div>
      </div>
    </div>

    <!-- 购物车 -->
    <div class="cart">
      <h3>🛒 购物车</h3>
      <div v-for="(item, idx) in cart" :key="idx" class="cart-item">
        {{ item.name }} × {{ item.count }}
      </div>
      <div class="total" v-if="cart.length">合计：¥ {{ total }}</div>

      <!-- 桌号 -->
      <div class="table-box">
        <h4>选择桌号</h4>
        <div class="table-list">
          <button
            v-for="n in 10"
            :key="n"
            :class="{active: table == n}"
            @click="table = n"
          >
            {{ n }}
          </button>
        </div>
      </div>

      <button class="submit" @click="submit">提交订单</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      cateList: ["全部", "热菜", "凉菜", "主食", "汤品", "饮品"],
      currentCate: "全部",
      dishList: [],
      cart: [],
      table: null
    }
  },

  computed: {
    showList() {
      if (this.currentCate === "全部") return this.dishList
      return this.dishList.filter(d => d.category === this.currentCate)
    },
    total() {
      return this.cart.reduce((s, i) => s + i.price * i.count, 0).toFixed(2)
    }
  },

  mounted() {
    this.getList()
  },

  methods: {
    async getList() {
      let res = await fetch("http://localhost:8080/dish/all")
      this.dishList = await res.json()
    },

    add(dish) {
      let item = this.cart.find(i => i.id === dish.id)
      if (item) {
        item.count++
      } else {
        this.cart.push({ ...dish, count: 1 })
      }
    },

    async submit() {
      if (!this.cart.length) return alert("请选菜品")
      if (!this.table) return alert("请选桌号")

      // 生成统一订单号
      const date = new Date()
      const dateStr = date.toISOString().slice(0,10).replace(/-/g, '')
      const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
      const orderNo = 'O' + dateStr + random

      // 批量下单
      for (let item of this.cart) {
        await fetch("http://localhost:8080/order/add", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            orderNo: orderNo,
            dishName: item.name,
            price: item.price,
            quantity: item.count,
            tableNum: String(this.table),
            status: "待制作"
          })
        })
      }

      alert("下单成功！订单号：" + orderNo + " 桌号：" + this.table)
      this.cart = []
      this.table = null
    }
  }
}
</script>

<style scoped>
.page { padding: 20px; }
.layout { display: flex; gap: 20px; margin-bottom: 30px; }
.left { width: 150px; background: #f5f5f5; padding: 15px; border-radius: 8px; }
.cate { padding: 10px; margin: 6px 0; border-radius: 6px; cursor: pointer; }
.cate.on { background: #0d6efd; color: white; }
.right { flex: 1; display: grid; grid-template-columns: repeat(3,1fr); gap:15px; }
.card { border:1px solid #ddd; padding:15px; border-radius:10px; text-align:center; }
.name { font-weight:bold; margin-bottom:6px; }
.sales { font-size:12px; color: #666; margin-bottom:6px; }
.price { color:red; font-weight:bold; margin-bottom:10px; }
.btn { background:#0d6efd; color:white; border:none; padding:6px 12px; border-radius:6px; cursor:pointer; }
.cart { background: #f9f9f9; padding: 20px; border-radius: 10px; }
.cart-item { margin:4px 0; }
.total { font-weight:bold; color:red; margin:10px 0; }
.table-list { display:flex; gap:8px; margin:10px 0; }
.table-list button { width: 40px; height: 40px; border-radius:50%; border:1px solid #ccc; cursor:pointer; }
.table-list button.active { background:#0d6efd; color:white; }
.submit { background:red; color:white; border:none; padding:10px 20px; border-radius:8px; cursor:pointer; margin-top:10px; }
</style>