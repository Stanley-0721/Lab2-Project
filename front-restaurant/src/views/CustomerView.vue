<template>
  <div class="page">
    <h2>顾客点餐页面</h2>
    <button @click="testApi">测试连接后端</button>
    <p>{{ msg }}</p>
  </div>
</template>

<script>
export default {
  name: 'CustomerView',
  data() {
    return {
      msg: ''
    }
  },
  methods: {
    async testApi() {
      const res = await fetch('http://localhost:8080/hello')
      this.msg = await res.text()
    }
  }
}
</script>

<style scoped>
.page { padding: 20px; }
</style>