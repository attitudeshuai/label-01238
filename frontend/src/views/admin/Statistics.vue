<template>
  <div class="statistics-page">
    <!-- 时间范围选择 -->
    <div class="filter-bar">
      <div class="filter-group">
        <span class="filter-label">数据周期</span>
        <el-radio-group v-model="dateRange" @change="handleDateRangeChange">
          <el-radio-button label="7">近7天</el-radio-button>
          <el-radio-button label="14">近14天</el-radio-button>
          <el-radio-button label="30">近30天</el-radio-button>
        </el-radio-group>
      </div>
      <div class="filter-group">
        <span class="filter-label">月度统计</span>
        <el-radio-group v-model="monthRange" @change="handleMonthRangeChange">
          <el-radio-button label="6">近6月</el-radio-button>
          <el-radio-button label="12">近12月</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-grid">
      <div class="metric-card">
        <div class="metric-header">
          <span class="metric-title">总销售额</span>
          <span class="metric-badge success">已支付</span>
        </div>
        <div class="metric-value">¥{{ formatNumber(overview.salesAmount?.paid || 0) }}</div>
        <div class="metric-sub">
          <span>待支付: ¥{{ formatNumber(overview.salesAmount?.pending || 0) }}</span>
        </div>
      </div>
      <div class="metric-card">
        <div class="metric-header">
          <span class="metric-title">订单总数</span>
        </div>
        <div class="metric-value">{{ overview.orderCount || 0 }}</div>
        <div class="metric-sub">
          <span>完成率: {{ completionRate }}%</span>
        </div>
      </div>
      <div class="metric-card">
        <div class="metric-header">
          <span class="metric-title">客单价</span>
        </div>
        <div class="metric-value">¥{{ avgOrderAmount }}</div>
        <div class="metric-sub">
          <span>基于已支付订单</span>
        </div>
      </div>
      <div class="metric-card">
        <div class="metric-header">
          <span class="metric-title">用户数</span>
        </div>
        <div class="metric-value">{{ overview.userCount || 0 }}</div>
        <div class="metric-sub">
          <span>商品数: {{ overview.productCount || 0 }}</span>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <!-- 订单趋势 -->
      <div class="chart-card large">
        <div class="chart-header">
          <h3>订单趋势分析</h3>
          <div class="chart-legend">
            <span class="legend-item"><i class="dot orders"></i>订单数</span>
          </div>
        </div>
        <div ref="orderTrendChart" class="chart-body"></div>
      </div>

      <!-- 月度销售 -->
      <div class="chart-card large">
        <div class="chart-header">
          <h3>月度销售趋势</h3>
          <div class="chart-legend">
            <span class="legend-item"><i class="dot sales"></i>销售额</span>
          </div>
        </div>
        <div ref="monthlySalesChart" class="chart-body"></div>
      </div>
    </div>

    <!-- 订单状态与热销商品 -->
    <div class="analysis-section">
      <!-- 订单状态饼图 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>订单状态分布</h3>
        </div>
        <div ref="orderStatusChart" class="chart-body"></div>
      </div>

      <!-- 热销商品排行 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>热销商品TOP10</h3>
        </div>
        <div ref="hotProductsChart" class="chart-body"></div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <div class="table-card">
        <div class="table-header">
          <h3>热销商品明细</h3>
        </div>
        <el-table :data="hotProducts" stripe>
          <el-table-column type="index" label="排名" width="70" />
          <el-table-column prop="name" label="商品名称" min-width="200" />
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column prop="price" label="单价" width="120">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="salesCount" label="销量" width="100" sortable />
          <el-table-column prop="salesAmount" label="销售额" width="140" sortable>
            <template #default="{ row }">¥{{ formatNumber(row.salesAmount || 0) }}</template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { statisticsApi } from '../../api'

// 数据
const overview = ref({})
const recentOrders = ref([])
const monthlySales = ref([])
const orderStatus = ref([])
const hotProducts = ref([])

// 筛选条件
const dateRange = ref('7')
const monthRange = ref('6')

// 图表实例
const orderTrendChart = ref()
const monthlySalesChart = ref()
const orderStatusChart = ref()
const hotProductsChart = ref()
let charts = []

// 计算属性
const completionRate = computed(() => {
  const completed = orderStatus.value.find(s => s.status === 3)?.count || 0
  const total = orderStatus.value.reduce((sum, s) => sum + s.count, 0)
  return total > 0 ? ((completed / total) * 100).toFixed(1) : 0
})

const avgOrderAmount = computed(() => {
  const paid = overview.value.salesAmount?.paid || 0
  const paidOrders = orderStatus.value
    .filter(s => [1, 2, 3].includes(s.status))
    .reduce((sum, s) => sum + s.count, 0)
  return paidOrders > 0 ? formatNumber((paid / paidOrders).toFixed(2)) : 0
})

// 格式化数字
const formatNumber = (num) => {
  return Number(num).toLocaleString('zh-CN')
}

// 初始化订单趋势图
const initOrderTrendChart = () => {
  const chart = echarts.init(orderTrendChart.value)
  charts.push(chart)
  
  const dates = recentOrders.value.map(d => {
    const date = new Date(d.date)
    return `${date.getMonth() + 1}/${date.getDate()}`
  })
  const counts = recentOrders.value.map(d => d.count)
  
  chart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#eee',
      textStyle: { color: '#333' }
    },
    grid: { left: 50, right: 30, top: 30, bottom: 30 },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: '#e5e5e5' } },
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } },
      axisLabel: { color: '#666' }
    },
    series: [{
      type: 'line',
      data: counts,
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { color: '#c9a962', width: 3 },
      itemStyle: { color: '#c9a962' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(201,169,98,0.3)' },
          { offset: 1, color: 'rgba(201,169,98,0.05)' }
        ])
      }
    }]
  })
}

// 初始化月度销售图
const initMonthlySalesChart = () => {
  const chart = echarts.init(monthlySalesChart.value)
  charts.push(chart)
  
  const months = monthlySales.value.map(d => d.month)
  const amounts = monthlySales.value.map(d => d.amount || 0)
  
  chart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#eee',
      textStyle: { color: '#333' },
      formatter: (params) => {
        const d = params[0]
        return `${d.name}<br/>销售额: ¥${formatNumber(d.value)}`
      }
    },
    grid: { left: 70, right: 30, top: 30, bottom: 30 },
    xAxis: {
      type: 'category',
      data: months,
      axisLine: { lineStyle: { color: '#e5e5e5' } },
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } },
      axisLabel: { color: '#666', formatter: (v) => `¥${v >= 10000 ? (v/10000) + 'w' : v}` }
    },
    series: [{
      type: 'bar',
      data: amounts,
      barMaxWidth: 50,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#34c759' },
          { offset: 1, color: '#2a9d4a' }
        ]),
        borderRadius: [6, 6, 0, 0]
      }
    }]
  })
}

// 初始化订单状态饼图
const initOrderStatusChart = () => {
  const chart = echarts.init(orderStatusChart.value)
  charts.push(chart)
  
  const statusText = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已完成', 4: '已取消' }
  const statusColors = ['#ff9500', '#007aff', '#86868b', '#34c759', '#ff3b30']
  
  const data = orderStatus.value.map(s => ({
    name: statusText[s.status],
    value: s.count
  }))
  
  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 20,
      top: 'center',
      textStyle: { color: '#666' }
    },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold' }
      },
      data: data,
      color: statusColors
    }]
  })
}

// 初始化热销商品图
const initHotProductsChart = () => {
  const chart = echarts.init(hotProductsChart.value)
  charts.push(chart)
  
  const products = hotProducts.value.slice(0, 10).reverse()
  const names = products.map(p => p.name.length > 10 ? p.name.slice(0, 10) + '...' : p.name)
  const sales = products.map(p => p.salesCount || 0)
  
  chart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: { left: 120, right: 30, top: 20, bottom: 20 },
    xAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } },
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'category',
      data: names,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#333' }
    },
    series: [{
      type: 'bar',
      data: sales,
      barMaxWidth: 20,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#007aff' },
          { offset: 1, color: '#5ac8fa' }
        ]),
        borderRadius: [0, 4, 4, 0]
      }
    }]
  })
}

// 获取数据
const fetchData = async () => {
  const [overviewRes, recentRes, monthlyRes, statusRes, hotRes] = await Promise.all([
    statisticsApi.overview(),
    statisticsApi.recentOrders(Number(dateRange.value)),
    statisticsApi.monthlySales(Number(monthRange.value)),
    statisticsApi.orderStatus(),
    statisticsApi.hotProducts(10)
  ])
  
  overview.value = overviewRes.data || {}
  recentOrders.value = recentRes.data || []
  monthlySales.value = monthlyRes.data || []
  orderStatus.value = statusRes.data || []
  hotProducts.value = hotRes.data || []
  
  // 初始化图表
  setTimeout(() => {
    initOrderTrendChart()
    initMonthlySalesChart()
    initOrderStatusChart()
    initHotProductsChart()
  }, 100)
}

// 处理日期范围变化
const handleDateRangeChange = async () => {
  const res = await statisticsApi.recentOrders(Number(dateRange.value))
  recentOrders.value = res.data || []
  charts[0]?.dispose()
  charts[0] = null
  setTimeout(initOrderTrendChart, 50)
}

// 处理月份范围变化
const handleMonthRangeChange = async () => {
  const res = await statisticsApi.monthlySales(Number(monthRange.value))
  monthlySales.value = res.data || []
  charts[1]?.dispose()
  charts[1] = null
  setTimeout(initMonthlySalesChart, 50)
}

// 窗口大小变化
const handleResize = () => {
  charts.forEach(c => c?.resize())
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  charts.forEach(c => c?.dispose())
})
</script>


<style scoped>
.statistics-page {
  animation: fadeIn 0.4s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  gap: 32px;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  color: #666;
}

/* 指标卡片 */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.metric-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}

.metric-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.metric-title {
  font-size: 14px;
  color: #666;
}

.metric-badge {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.metric-badge.success {
  background: rgba(52,199,89,0.1);
  color: #34c759;
}

.metric-value {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.metric-sub {
  font-size: 13px;
  color: #86868b;
}

/* 图表区域 */
.charts-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.analysis-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chart-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
}

.chart-legend {
  display: flex;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.legend-item .dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot.orders { background: #c9a962; }
.dot.sales { background: #34c759; }

.chart-body {
  height: 300px;
}

/* 表格区域 */
.table-section {
  margin-bottom: 24px;
}

.table-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}

.table-header {
  margin-bottom: 16px;
}

.table-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
}

/* 响应式 */
@media (max-width: 1200px) {
  .metrics-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .charts-section,
  .analysis-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    gap: 16px;
  }
  .metrics-grid {
    grid-template-columns: 1fr;
  }
}
</style>
