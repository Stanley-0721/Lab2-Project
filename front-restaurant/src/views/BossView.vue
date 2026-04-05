<template>
  <div class="page">
    <h2>📊 老板数据中心</h2>

    <!-- 可展开的统计卡片 -->
    <div class="collapse-card">
      <div class="collapse-header" @click="toggleStat">
        {{ showStat ? '▼' : '▶' }} 餐厅经营统计
      </div>

      <div v-if="showStat" class="collapse-body">
        <div class="chart-grid">
          <div class="chart-box">
            <div class="chart-title">年度营收对比</div>
            <div ref="yearChart" class="chart"></div>
          </div>
          <div class="chart-box">
            <div class="chart-title">菜品销量排行</div>
            <div ref="dishChart" class="chart"></div>
          </div>
          <div class="chart-box">
            <div class="chart-title">月度营收趋势</div>
            <div ref="monthChart" class="chart"></div>
          </div>
          <div class="chart-box">
            <div class="chart-title">年度营收占比</div>
            <div ref="pieChart" class="chart"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 原有年份月份列表 -->
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
              <div class="order-card" v-for="group in getGroupedOrders(month.month)" :key="group.key">
                <div class="card-top">
                  <span>订单号：{{ group.orderNo }}</span>
                  <span>{{ group.createTime }}</span>
                </div>
                <div class="card-body">
                  桌号：{{ group.tableNum }}<br>
                  菜品：{{ group.dishes }}
                </div>
                <div class="card-bottom">
                  <span>合计：¥{{ group.totalMoney.toFixed(2) }}</span>
                  <span class="status">{{ group.status }}</span>
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
import * as echarts from 'echarts'

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
      sortDir: 'desc',
      showStat: false,
      yearChart: null,
      dishChart: null,
      monthChart: null,
      pieChart: null
    }
  },
  mounted() {
    this.loadStat()
    this.loadYear()
    this.loadMonth()
    this.loadAllOrders()
  },
  methods: {
    toggleStat() {
      this.showStat = !this.showStat
      if (this.showStat) {
        this.$nextTick(() => this.initAllCharts())
      }
    },
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
    getGroupedOrders(month) {
      const orders = this.allOrders.filter(o => {
        if (!o.createTime) return false
        return o.createTime.slice(0, 7) === month
      })
      const groupMap = {}
      orders.forEach(o => {
        const orderNo = o.orderNo || `NO_${o.id}`
        if (!groupMap[orderNo]) {
          groupMap[orderNo] = {
            key: orderNo, orderNo, tableNum: o.tableNum, createTime: o.createTime,
            status: o.status, totalMoney: 0, dishList: []
          }
        }
        groupMap[orderNo].dishList.push(o)
        groupMap[orderNo].totalMoney += o.price * o.quantity
      })
      Object.values(groupMap).forEach(g => {
        g.dishes = g.dishList.map(i => `${i.dishName}×${i.quantity}`).join('，')
      })
      return Object.values(groupMap)
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
    },

    // 图表初始化
    initAllCharts() {
      this.yearChart = echarts.init(this.$refs.yearChart)
      this.dishChart = echarts.init(this.$refs.dishChart)
      this.monthChart = echarts.init(this.$refs.monthChart)
      this.pieChart = echarts.init(this.$refs.pieChart)

      // ================== 1. 年度营收折线图 ==================
      let yearData = [...this.yearList].sort((a, b) => a.year - b.year)
      const years = yearData.map(y => y.year)
      const yearMoney = yearData.map(y => y.total)

      this.yearChart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: "{b} 年<br>营收：¥{c}"
        },
        grid: { top: 10, right: 20, left: 30, bottom: 30 },
        xAxis: [{ type: 'category', data: years }],
        yAxis: [{ type: 'value' }],
        series: [{
          type: 'line',
          data: yearMoney,
          smooth: true
        }]
      })

      // ================== 2. 菜品销量 TOP10 柱状图 ==================
      const dishMap = {}
      this.allOrders.forEach(o => {
        dishMap[o.dishName] = (dishMap[o.dishName] || 0) + o.quantity
      })

      let dishArr = Object.entries(dishMap)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 10)

      const dishNames = dishArr.map(i => i[0])
      const dishCounts = dishArr.map(i => i[1])

      this.dishChart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: "{b}<br>销量：{c} 杯"
        },
        grid: { top: 10, right: 20, left: 30, bottom: 60 },
        xAxis: [{
          type: 'category',
          data: dishNames,
          axisLabel: { rotate: 30 }
        }],
        yAxis: [{ type: 'value' }],
        series: [{
          type: 'bar',
          data: dishCounts
        }]
      })

      // ================== 3. 月度营收折线图 ==================
      const curYear = new Date().getFullYear()
      let monthData = this.monthList
        .filter(m => m.month.startsWith(curYear))
        .sort((a, b) => a.month.localeCompare(b.month))

      const months = monthData.map(m => m.month)
      const monthMoney = monthData.map(m => m.total)

      this.monthChart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: "{b}<br>营收：¥{c}"
        },
        grid: { top: 10, right: 20, left: 30, bottom: 30 },
        xAxis: [{ type: 'category', data: months }],
        yAxis: [{ type: 'value' }],
        series: [{
          type: 'line',
          data: monthMoney,
          smooth: true
        }]
      })

      // ================== 4. 饼图：今年有营业额的月份 ==================
      let pieData = monthData
        .filter(m => m.total > 0)
        .map(m => ({
          name: m.month,
          value: m.total
        }))

      this.pieChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: "{b}<br>营收：¥{c}<br>占比：{d}%"
        },
        series: [{
          type: 'pie',
          data: pieData
        }]
      })
    }
  }
}
</script>

<style scoped>
.page { padding: 24px; background: #f7f8fa; min-height: 100vh; }
h2 { margin-bottom: 20px; }

/* 可展开卡片 */
.collapse-card {
  background: #fff;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  overflow: hidden;
}
.collapse-header {
  padding: 16px 20px;
  font-weight: bold;
  cursor: pointer;
  background: #eef4ff;
}
.collapse-body {
  padding: 20px;
}
.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.chart-box {
  background: #fff;
  border-radius: 10px;
  padding: 14px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
}
.chart-title {
  font-weight: bold;
  margin-bottom: 10px;
  font-size: 14px;
}
.chart {
  width: 100%;
  height: 220px;
}

/* 原有样式 */
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