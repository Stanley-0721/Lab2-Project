<template>
  <div class="customer-page">
    <h2>🍽️ 顾客点餐</h2>

    <div class="dish-list">
      <div v-for="dish in dishList" :key="dish.id" class="dish-item">
        <span>{{ dish.name }} — ￥{{ dish.price }}</span>
        <button @click="addToCart(dish)">点这个</button>
      </div>
    </div>

    <div class="cart" v-if="cart.length > 0">
      <h3>已点菜品</h3>
      <div v-for="(item, index) in cart" :key="index">
        {{ item.name }} × {{ item.count }} — ￥{{ item.price * item.count }}
      </div>

      <input v-model="tableNum" placeholder="输入桌号" />
      <button @click="submitOrder">提交订单</button>
    </div>

    <p class="tip">{{ msg }}</p>
  </div>
</template>

<script>
export default {
  name: "CustomerView",
  data() {
    return {
      dishList: [],
      cart: [],
      tableNum: "",
      msg: ""
    };
  },
  mounted() {
    this.loadDishes();
  },
  methods: {
    async loadDishes() {
      let res = await fetch("http://localhost:8080/dish/list");
      this.dishList = await res.json();
    },

    addToCart(dish) {
      let exist = this.cart.find(i => i.id === dish.id);
      if (exist) {
        exist.count++;
      } else {
        this.cart.push({ ...dish, count: 1 });
      }
    },

    async submitOrder() {
      if (!this.tableNum) {
        this.msg = "请输入桌号！";
        return;
      }

      for (let item of this.cart) {
        let order = {
          dishName: item.name,
          price: item.price,
          quantity: item.count,
          tableNum: this.tableNum,
          status: "待制作"
        };
        await fetch("http://localhost:8080/order/add", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(order)
        });
      }

      this.msg = "✅ 下单成功！后厨已收到";
      this.cart = [];
      this.tableNum = "";
    }
  }
};
</script>

<style scoped>
.customer-page { padding: 20px; max-width: 500px; margin: auto; }
.dish-list { margin: 20px 0; }
.dish-item { padding: 10px; border: 1px solid #ddd; margin: 5px 0; display: flex; justify-content: space-between; }
.cart { margin-top: 30px; padding: 15px; border: 1px solid #666; }
input { padding: 8px; width: 100%; margin: 10px 0; }
button { padding: 6px 12px; cursor: pointer; }
.tip { color: green; margin-top: 10px; }
</style>