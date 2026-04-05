<template>
  <div class="index-page">
    <div class="container">
      <h1>🍽 餐厅点餐管理系统</h1>
      <p class="subtitle">请选择操作入口</p>

      <div class="btn-group">
        <button class="btn customer" @click="$router.push('/customer')">
          👨‍👩‍👧‍👦 顾客点餐
        </button>
        <button class="btn kitchen" @click="$router.push('/kitchen')">
          👨‍🍳 后厨制作
        </button>
        <button class="btn waiter" @click="$router.push('/waiter')">
          💁‍♂️ 服务员上菜
        </button>
        <button class="btn cashier" @click="$router.push('/reception')">
          💳 前台结账
        </button>
        <button class="btn boss" @click="$router.push('/boss')">
          👔 老板中心
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Index"
}
</script>

<style scoped>
.index-page {
  width: 100vw;
  height: 100vh;
  background: #f7f8fa;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.container {
  width: 100%;
  max-width: 500px;
  padding: 20px;
}

h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
}

.subtitle {
  color: #666;
  font-size: 16px;
  margin-bottom: 30px;
}

.btn-group {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.btn {
  padding: 16px;
  font-size: 16px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-weight: bold;
  transition: 0.2s;
}

.btn:hover {
  transform: scale(1.02);
}

.customer { background: #0d6efd; color: white; }
.kitchen { background: #17a2b8; color: white; }
.waiter { background: #28a745; color: white; }
.cashier { background: #fd7e14; color: white; }
.boss { background: #6610f2; color: white; }
</style>