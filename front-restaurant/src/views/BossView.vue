<template>
  <div class="page">
    <h2>📊 老板数据中心</h2>

    <!-- 总统计卡片 -->
    <div class="stat-card" v-if="stat.totalCount >= 0">
      <h3>🏪 餐厅总统计</h3>
      <p>
        总订单数：{{ stat.totalCount }} 单 &nbsp;&nbsp;
        总营收：¥ {{ stat.totalMoney.toFixed(2) }}
      </p>
      <p>最近消费时间：{{ stat.lastTime }}</p>
    </div>

    <div class="tabs">
      <button @click="switchTab('all')">全部订单</button>
      <button @click="switchTab('month')">按月营收</button>
      <button @click="switchTab('year')">按年营收</button>
    </div>

    <div class="sorts">
      <span>排序：</span>
      <select v-model="sortKey">
        <option value="time">时间</option>
        <option value="money">金额</option>
        <option value="table">桌号</option>
      </select>
      <button @click="toggleSort">{{ sortType === 'asc' ? '↑升序' : '↓降序' }}</button>
    </div>

    <!-- 全部订单 -->
    <div v-if="currentTab === 'all'">
      <div class="item" v-for="o in sortedList" :key="o.id">
        订单号：{{ o.orderNo }} | 桌号：{{ o.tableNum }} |
        时间：{{ o.createTime }} | 金额：¥{{ o.price * o.quantity }} |
        状态：{{ o.status }}
      </div>
    </div>

    <!-- 按月 -->
    <div v-if="currentTab === 'month'">
      <div class="item" v-for="(m, idx) in sortedList" :key="idx">
        月份：{{ m.month }} | 订单数：{{ m.count }} | 营收：¥{{ m.total.toFixed(2) }}
      </div>
    </div>

    <!-- 按年 -->
    <div v-if="currentTab === 'year'">
      <div class="item" v-for="(y, idx) in sortedList" :key="idx">
        年份：{{ y.year }} | 订单数：{{ y.count }} | 总营收：¥{{ y.total.toFixed(2) }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      currentTab: 'all',
      sortKey: 'time',
      sortType: 'desc',
      rawList: [],
      stat: { totalCount:0, totalMoney:0, lastTime:'' }
    }
  },
  mounted() {
    this.loadStat()
    this.load()
  },
  computed: {
    sortedList() {
      let arr = [...this.rawList]
      let key = this.sortKey
      let type = this.sortType

      arr.sort((a, b) => {
        if (this.currentTab === 'all') {
          let va, vb
          if (key === 'time') {
            va = new Date(a.createTime).getTime()
            vb = new Date(b.createTime).getTime()
          } else if (key === 'money') {
            va = a.price * a.quantity
            vb = b.price * b.quantity
          } else {
            va = a.tableNum
            vb = b.tableNum
          }
          return type === 'asc' ? (va > vb ? 1 : -1) : (va > vb ? -1 : 1)
        }
        if (this.currentTab === 'month') {
          if (key === 'time') return type === 'asc' ? (a.month > b.month ? 1 : -1) : (a.month > b.month ? -1 : 1)
          if (key === 'money') return type === 'asc' ? a.total - b.total : b.total - a.total
          return 0
        }
        if (this.currentTab === 'year') {
          if (key === 'time') return type === 'asc' ? (a.year > b.year ? 1 : -1) : (a.year > b.year ? -1 : 1)
          if (key === 'money') return type === 'asc' ? a.total - b.total : b.total - a.total
          return 0
        }
      })
      return arr
    }
  },
  methods: {
    async loadStat() {
      let r = await fetch('http://localhost:8080/order/boss/stat')
      this.stat = await r.json()
    },
    async load() {
      let url = ''
      if (this.currentTab === 'all') url = '/order/boss/all'
      if (this.currentTab === 'month') url = '/order/boss/monthly'
      if (this.currentTab === 'year') url = '/order/boss/yearly'
      let res = await fetch('http://localhost:8080' + url)
      this.rawList = await res.json()
    },
    switchTab(tab) {
      this.currentTab = tab
      this.load()
    },
    toggleSort() {
      this.sortType = this.sortType === 'asc' ? 'desc' : 'asc'
    }
  }
}
</script>

<style scoped>
.page{padding:24px}
.stat-card{padding:16px;background:#f7f9fc;border-radius:10px;margin-bottom:16px;border:1px solid #e4e7ed}
.stat-card h3{margin:0 0 8px}
.stat-card p{margin:4px 0;color:#333}
.tabs{margin:10px 0}
.tabs button{margin:0 8px;padding:8px 12px}
.sorts{margin:10px 0}
.sorts select{padding:6px;margin:0 6px}
.sorts button{padding:6px 10px}
.item{padding:12px;border:1px solid #ddd;margin:8px 0}
</style>