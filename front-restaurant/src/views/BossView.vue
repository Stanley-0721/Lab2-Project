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

    <!-- ================== 菜品管理 ================== -->
    <div class="collapse-card">
      <div class="collapse-header" @click="toggleDish">
        {{ showDish ? '▼' : '▶' }} 菜品管理
      </div>
      <div v-if="showDish" class="collapse-body">
        <button class="add-btn" @click="showAddModal = true">＋ 添加菜品</button>

        <div class="dish-table">
          <div class="dish-header">
            <div>菜名</div>
            <div>分类</div>
            <div>价格</div>
            <div>销量</div>
            <div>操作</div>
          </div>
          <div class="dish-row" v-for="d in dishList" :key="d.id">
            <div>{{ d.name }}</div>
            <div>{{ d.category }}</div>
            <div :class="{ off: d.price < 0 }">
              {{ d.price > 0 ? '¥' + d.price : '已下架' }}
            </div>
            <div>{{ d.sales }}</div>
            <div class="btns">
              <button @click="edit(d)">修改</button>
              <button v-if="d.price>0" class="red" @click="down(d.id)">下架</button>
              <button v-else class="green" @click="up(d.id)">上架</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加菜品弹窗 -->
    <div class="modal" v-if="showAddModal">
      <div class="modal-box">
        <h3>添加菜品</h3>
        <input v-model="addForm.name" placeholder="菜名" />
        <select v-model="addForm.category">
          <option value="热菜">热菜</option>
          <option value="凉菜">凉菜</option>
          <option value="主食">主食</option>
          <option value="饮品">饮品</option>
        </select>
        <input v-model="addForm.price" type="number" placeholder="价格" />
        <button @click="addDish">提交</button>
        <button @click="showAddModal = false">关闭</button>
      </div>
    </div>

    <!-- 修改菜品弹窗 -->
    <div class="modal" v-if="showEditModal">
      <div class="modal-box">
        <h3>修改菜品</h3>
        <input v-model="editForm.name" placeholder="菜名" />
        <select v-model="editForm.category">
          <option value="热菜">热菜</option>
          <option value="凉菜">凉菜</option>
          <option value="主食">主食</option>
          <option value="饮品">饮品</option>
        </select>
        <input v-model="editForm.price" type="number" placeholder="价格" />
        <button @click="updateDish">提交</button>
        <button @click="showEditModal = false">关闭</button>
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
            <button @click="setSort('month')">月份 {{ sortBy === 'month' ? (sortDir === 'asc' ? '↑' : '↓') : '' }}</button>
            <button @click="setSort('money')">营收 {{ sortBy === 'money' ? (sortDir === 'asc' ? '↑' : '↓') : '' }}</button>
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

const API = 'http://localhost:8080'

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
      yearChart: null, dishChart: null, monthChart: null, pieChart: null,

      showDish: false,
      dishList: [],
      showAddModal: false,
      showEditModal: false,
      addForm: { name: '', category: '热菜', price: 0 },
      editForm: { id: null, name: '', category: '', price: 0 }
    }
  },
  mounted() {
    this.loadStat()
    this.loadYear()
    this.loadMonth()
    this.loadAllOrders()
    this.loadDish()
  },
  methods: {
    async loadDish() {
      let r = await fetch(API + '/dish/all')
      this.dishList = await r.json()
    },
    toggleStat() { this.showStat = !this.showStat; if (this.showStat) this.$nextTick(() => this.initAllCharts()) },
    toggleDish() { this.showDish = !this.showDish },

    async addDish() {
      await fetch(API + '/dish/add', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.addForm)
      })
      this.showAddModal = false
      this.loadDish()
    },
    edit(d) {
      this.editForm = { ...d }
      this.showEditModal = true
    },
    async updateDish() {
      await fetch(API + `/dish/update/${this.editForm.id}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.editForm)
      })
      this.showEditModal = false
      this.loadDish()
    },
    async down(id) {
      await fetch(API + `/dish/down/${id}`)
      this.loadDish()
    },
    async up(id) {
      await fetch(API + `/dish/up/${id}`)
      this.loadDish()
    },

    // 图表
    initAllCharts() {
      this.yearChart = echarts.init(this.$refs.yearChart)
      this.dishChart = echarts.init(this.$refs.dishChart)
      this.monthChart = echarts.init(this.$refs.monthChart)
      this.pieChart = echarts.init(this.$refs.pieChart)

      let yearData = [...this.yearList].sort((a, b) => a.year - b.year)
      this.yearChart.setOption({ tooltip: { trigger: 'item' }, xAxis: { data: yearData.map(y => y.year) }, yAxis: {}, series: [{ type: 'line', data: yearData.map(y => y.total) }] })

      const dishMap = {}
      this.allOrders.forEach(o => { dishMap[o.dishName] = (dishMap[o.dishName] || 0) + o.quantity })
      let dishArr = Object.entries(dishMap).sort((a, b) => b[1] - a[1]).slice(0, 10)
      this.dishChart.setOption({ tooltip: { trigger: 'item' }, xAxis: { data: dishArr.map(d => d[0]), axisLabel: { rotate: 30 } }, yAxis: {}, series: [{ type: 'bar', data: dishArr.map(d => d[1]) }] })

      const curYear = new Date().getFullYear()
      let monthData = this.monthList.filter(m => m.month.startsWith(curYear)).sort((a, b) => a.month.localeCompare(b.month))
      this.monthChart.setOption({ tooltip: { trigger: 'item' }, xAxis: { data: monthData.map(m => m.month) }, yAxis: {}, series: [{ type: 'line', data: monthData.map(m => m.total) }] })

      let pieData = monthData.filter(m => m.total > 0).map(m => ({ name: m.month, value: m.total }))
      this.pieChart.setOption({ tooltip: { trigger: 'item' }, series: [{ type: 'pie', data: pieData }] })
    },

    // 原有方法
    async loadStat() { let r = await fetch(API+'/order/boss/stat'); this.stat = await r.json() },
    async loadYear() { let r = await fetch(API+'/order/boss/yearly'); this.yearList = await r.json() },
    async loadMonth() { let r = await fetch(API+'/order/boss/monthly'); this.monthList = await r.json() },
    async loadAllOrders() { let r = await fetch(API+'/order/boss/all'); this.allOrders = await r.json() },
    getMonthsByYear(year) { return this.monthList.filter(m => m.month.startsWith(year)) },
    sortedMonths(year) {
      let list = [...this.getMonthsByYear(year)]
      if (this.sortBy === 'month') list.sort((a, b) => this.sortDir === 'asc' ? a.month.localeCompare(b.month) : b.month.localeCompare(a.month))
      if (this.sortBy === 'money') list.sort((a, b) => this.sortDir === 'asc' ? a.total - b.total : b.total - a.total)
      return list
    },
    getGroupedOrders(month) {
      const orders = this.allOrders.filter(o => o.createTime?.slice(0,7) === month)
      const g = {}
      orders.forEach(o => {
        const no = o.orderNo || '_' + o.id
        if (!g[no]) g[no] = { key: no, orderNo: no, tableNum: o.tableNum, createTime: o.createTime, status: o.status, totalMoney:0, dishList:[] }
        g[no].dishList.push(o)
        g[no].totalMoney += o.price * o.quantity
      })
      Object.values(g).forEach(x => x.dishes = x.dishList.map(i => i.dishName + '×' + i.quantity).join('，'))
      return Object.values(g)
    },
    toggleYear(y) { const i = this.expandedYears.indexOf(y); i>-1 ? this.expandedYears.splice(i,1) : this.expandedYears.push(y) },
    toggleMonth(m) { const i = this.expandedMonths.indexOf(m); i>-1 ? this.expandedMonths.splice(i,1) : this.expandedMonths.push(m) },
    setSort(t) { this.sortBy === t ? this.sortDir = this.sortDir==='asc'?'desc':'asc' : (this.sortBy = t, this.sortDir='desc') }
  }
}
</script>

<style scoped>
.page { padding: 24px; background: #f7f8fa; min-height: 100vh; }
h2 { margin-bottom: 20px; }
.collapse-card { background: #fff; border-radius: 12px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
.collapse-header { padding: 16px 20px; font-weight: bold; cursor: pointer; background: #eef4ff; border-radius: 12px; }
.collapse-body { padding: 20px; }
.chart-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.chart-box { background: #fff; border-radius: 10px; padding: 14px; box-shadow: 0 2px 6px rgba(0,0,0,0.05); }
.chart-title { font-weight: bold; margin-bottom: 10px; }
.chart { width: 100%; height: 220px; }

.add-btn { background: #0d6efd; color: white; border: none; padding: 8px 14px; border-radius: 6px; margin-bottom: 12px; }
.dish-table { width: 100%; }
.dish-header { display: grid; grid-template-columns: 2fr 1fr 1fr 1fr 2fr; font-weight: bold; padding: 10px 0; }
.dish-row { display: grid; grid-template-columns: 2fr 1fr 1fr 1fr 2fr; padding: 10px 0; border-bottom: 1px solid #eee; }
.off { color: #999; }
.btns button { margin-right: 6px; padding: 4px 8px; border-radius: 4px; border: none; }
.red { background: #dc3545; color: white; }
.green { background: #28a745; color: white; }

.modal { position: fixed; top:0;left:0;width:100vw;height:100vh;background:rgba(0,0,0,0.5);display:flex;align-items:center;justify-content:center;z-index:999; }
.modal-box { background:white;padding:24px;border-radius:12px;width:90%;max-width:360px; }
.modal-box input, .modal-box select { width:100%;margin:8px 0;padding:10px;border:1px solid #ddd;border-radius:6px; }
.modal-box button { margin-top:8px;width:100%;padding:10px;background:#0d6efd;color:white;border:none;border-radius:6px; }

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