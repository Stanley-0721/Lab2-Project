<template>
  <div class="page">
    <h2>📊 老板数据中心</h2>

    <div class="stat-card" v-if="stat.totalCount >= 0">
      <h3>🏪 餐厅总统计</h3>
      <p>总订单数：{{ stat.totalCount }} 单 &nbsp;&nbsp; 总营收：¥ {{ stat.totalMoney.toFixed(2) }}</p>
      <p>最近消费时间：{{ stat.lastTime }}</p>
    </div>

    <div class="year-list">
      <div class="year-item" v-for="year in yearList" :key="year.year">
        <div class="year-header" @click="toggleYear(year.year)">
          <span>{{ expandedYears.includes(year.year) ? '▼' : '▶' }}</span>
          {{ year.year }} 年 | 订单：{{ year.count }} 单 | 营收：¥{{ year.total.toFixed(2) }}
        </div>

        <div class="month-list" v-if="expandedYears.includes(year.year)">
          <div class="sort-bar">
            <button @click="setSort('month')">
              月份 {{ sortBy === 'month' ? (sortDir === 'asc' ? '↑' : '↓') : '' }}
            </button>
            <button @click="setSort('money')">
              营收 {{ sortBy === 'money' ? (sortDir === 'asc' ? '↑' : '↓') : '' }}
            </button>
          </div>

          <div class="month-item" v-for="month in sortedMonths(year.year)" :key="month.month">
            <div class="month-header" @click="toggleMonth(month.month)">
              <span>{{ expandedMonths.includes(month.month) ? '▼' : '▶' }}</span>
              {{ month.month }} | 订单：{{ month.count }} 单 | 营收：¥{{ month.total.toFixed(2) }}
            </div>

            <div class="order-cards" v-if="expandedMonths.includes(month.month)">
              <div class="order-card" v-for="o in getOrdersByMonth(month.month)" :key="o.id">
                <div class="card-top">
                  <span>桌号：{{ o.tableNum }}</span>
                  <span>{{ o.createTime }}</span>
                </div>
                <div class="card-body">菜品：{{ o.dishName }} × {{ o.quantity }}</div>
                <div class="card-bottom">
                  <span>金额：¥{{ (o.price * o.quantity).toFixed(2) }}</span>
                  <span class="status">{{ o.status }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      stat: { totalCount: 0, totalMoney: 0, lastTime: '' },
      yearList: [],
      monthList: [],
      allOrders: [],
      expandedYears: [],
      expandedMonths: [],
      sortBy: 'month',
      sortDir: 'desc'
    }
  },
  mounted() {
    this.loadStat()
    this.loadYear()
    this.loadMonth()
    this.loadAllOrders()
  },
  methods: {
    async loadStat() {
      let r = await fetch('http://localhost:8080/order/boss/stat')
      this.stat = await r.json()
    },
    async loadYear() {
      let r = await fetch('http://localhost:8080/order/boss/yearly')
      this.yearList = await r.json()
    },
    async loadMonth() {
      let r = await fetch('http://localhost:8080/order/boss/monthly')
      this.monthList = await r.json()
    },
    async loadAllOrders() {
      let r = await fetch('http://localhost:8080/order/boss/all')
      this.allOrders = await r.json()
    },
    getMonthsByYear(year) {
      return this.monthList.filter(m => m.month.startsWith(year))
    },
    sortedMonths(year) {
      let list = [...this.getMonthsByYear(year)]
      if (this.sortBy === 'month') {
        list.sort((a, b) => this.sortDir === 'asc' ? a.month.localeCompare(b.month) : b.month.localeCompare(a.month))
      }
      if (this.sortBy === 'money') {
        list.sort((a, b) => this.sortDir === 'asc' ? a.total - b.total : b.total - a.total)
      }
      return list
    },

    // ====================== 修复完成 ======================
    getOrdersByMonth(month) {
      return this.allOrders.filter(o => {
        if (!o.createTime) return false
        return o.createTime.slice(0,7) == month
      })
    },

    toggleYear(year) {
      const i = this.expandedYears.indexOf(year)
      i > -1 ? this.expandedYears.splice(i, 1) : this.expandedYears.push(year)
    },
    toggleMonth(month) {
      const i = this.expandedMonths.indexOf(month)
      i > -1 ? this.expandedMonths.splice(i, 1) : this.expandedMonths.push(month)
    },
    setSort(type) {
      if (this.sortBy === type) {
        this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc'
      } else {
        this.sortBy = type
        this.sortDir = 'desc'
      }
    }
  }
}
</script>

<style scoped>
.page { padding: 24px; }
.stat-card { padding: 16px; background: #f8f9fa; border-radius: 12px; margin-bottom: 20px; }
.year-list { margin-top: 10px; }
.year-item { margin: 8px 0; }
.year-header { padding: 14px 16px; background: #eef4ff; border-radius: 8px; cursor: pointer; font-weight: 500; }
.month-list { margin-left: 20px; margin-top: 4px; }
.sort-bar { margin: 8px 0; }
.sort-bar button { margin-right: 8px; padding: 6px 10px; }
.month-header { padding: 12px 16px; background: #f7f9fc; border-radius: 8px; margin: 6px 0; cursor: pointer; }
.order-cards { margin-left: 40px; margin-top: 6px; }
.order-card { padding: 14px; border: 1px solid #e4e7ed; border-radius: 10px; margin: 8px 0; background: #fff; }
.card-top { display: flex; justify-content: space-between; font-weight: 500; }
.card-body { margin: 8px 0; color: #666; }
.card-bottom { display: flex; justify-content: space-between; color: #333; }
.status { color: #009955; font-weight: 500; }
</style>