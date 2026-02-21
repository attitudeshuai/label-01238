<template>
  <div class="dashboard-page">
    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon users">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21" stroke="currentColor" stroke-width="1.5"/>
            <path d="M9 11C11.2091 11 13 9.20914 13 7C13 4.79086 11.2091 3 9 3C6.79086 3 5 4.79086 5 7C5 9.20914 6.79086 11 9 11Z" stroke="currentColor" stroke-width="1.5"/>
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ overview.userCount || 0 }}</span>
          <span class="stat-label">用户总数</span>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon products">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M6 2L3 6V20C3 20.5304 3.21071 21.0391 3.58579 21.4142C3.96086 21.7893 4.46957 22 5 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6L18 2H6Z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M3 6H21" stroke="currentColor" stroke-width="1.5"/>
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ overview.productCount || 0 }}</span>
          <span class="stat-label">商品总数</span>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon orders">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M9 5H7C5.89543 5 5 5.89543 5 7V19C5 20.1046 5.89543 21 7 21H17C18.1046 21 19 20.1046 19 19V7C19 5.89543 18.1046 5 17 5H15" stroke="currentColor" stroke-width="1.5"/>
            <path d="M9 5C9 3.89543 9.89543 3 11 3H13C14.1046 3 15 3.89543 15 5C15 6.10457 14.1046 7 13 7H11C9.89543 7 9 6.10457 9 5Z" stroke="currentColor" stroke-width="1.5"/>
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ overview.orderCount || 0 }}</span>
          <span class="stat-label">订单总数</span>
        </div>
      </div>

      <div class="stat-card highlight">
        <div class="stat-icon sales">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M12 1V23" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            <path d="M17 5H9.5C8.57174 5 7.6815 5.36875 7.02513 6.02513C6.36875 6.6815 6 7.57174 6 8.5C6 9.42826 6.36875 10.3185 7.02513 10.9749C7.6815 11.6313 8.57174 12 9.5 12H14.5C15.4283 12 16.3185 12.3687 16.9749 13.0251C17.6313 13.6815 18 14.5717 18 15.5C18 16.4283 17.6313 17.3185 16.9749 17.9749C16.3185 18.6313 15.4283 19 14.5 19H6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-value">¥{{ overview.salesAmount?.paid || 0 }}</span>
          <span class="stat-label">销售总额</span>
        </div>
      </div>
    </div>

    <!-- Charts & Progress -->
    <div class="charts-grid">
      <!-- Order Trend Chart -->
      <div class="chart-card">
        <div class="card-header">
          <h3>近7天订单趋势</h3>
          <span class="total-count">共 {{ totalRecentOrders }} 单</span>
        </div>
        <div ref="orderChartRef" class="chart-container"></div>
      </div>

      <!-- Order Status Progress -->
      <div class="progress-card">
        <div class="card-header">
          <h3>订单状态分布</h3>
          <span class="total-count">共 {{ totalStatusOrders }} 单</span>
        </div>
        <div class="progress-list">
          <div v-for="item in orderStatus" :key="item.status" class="progress-item">
            <div class="progress-label">
              <span class="status-dot" :class="statusClass[item.status]"></span>
              <span class="status-name">{{ statusText[item.status] }}</span>
              <span class="count">{{ item.count }} 单</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :class="statusClass[item.status]" :style="{ width: getStatusPercent(item.count) + '%' }"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import * as echarts from 'echarts'
import { statisticsApi } from '../../api'

const overview = ref({})
const recentOrders = ref([])
const orderStatus = ref([])
const orderChartRef = ref()

const statusText = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已完成', 4: '已取消' }
const statusClass = { 0: 'pending', 1: 'paid', 2: 'shipped', 3: 'completed', 4: 'cancelled' }

const totalRecentOrders = computed(() => recentOrders.value.reduce((sum, item) => sum + item.count, 0))
const totalStatusOrders = computed(() => orderStatus.value.reduce((sum, item) => sum + item.count, 0))

const getStatusPercent = (count) => {
  if (totalStatusOrders.value === 0) return 0
  return (count / totalStatusOrders.value) * 100
}

const initChart = () => {
  const chart = echarts.init(orderChartRef.value)
  chart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e5e5e5',
      borderWidth: 1,
      textStyle: { color: '#1d1d1f', fontSize: 13 },
      axisPointer: { type: 'none' },
      formatter: (params) => {
        const data = params[0]
        return `<div style="font-weight:500">${data.name}</div><div style="color:#c9a962;font-size:16px;font-weight:600;margin-top:4px">${data.value} 单</div>`
      }
    },
    grid: { left: 0, right: 0, bottom: 0, top: 10, containLabel: true },
    xAxis: {
      type: 'category',
      data: recentOrders.value.map(d => {
        const date = new Date(d.date)
        return `${date.getMonth() + 1}/${date.getDate()}`
      }),
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#86868b', fontSize: 12, margin: 12 }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#f5f5f7', type: 'dashed' } },
      axisLabel: { color: '#86868b', fontSize: 12 }
    },
    series: [{
      type: 'bar',
      data: recentOrders.value.map(d => d.count),
      barMaxWidth: 40,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#c9a962' },
          { offset: 1, color: '#a08339' }
        ]),
        borderRadius: [6, 6, 0, 0]
      },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#e8d5a3' },
            { offset: 1, color: '#c9a962' }
          ])
        }
      }
    }]
  })

  window.addEventListener('resize', () => chart.resize())
}

const fetchData = async () => {
  const [overviewRes, recentRes, statusRes] = await Promise.all([
    statisticsApi.overview(),
    statisticsApi.recentOrders(7),
    statisticsApi.orderStatus()
  ])
  overview.value = overviewRes.data || {}
  recentOrders.value = recentRes.data || []
  orderStatus.value = statusRes.data || []
  
  // 初始化图表
  if (recentOrders.value.length) {
    setTimeout(initChart, 100)
  }
}

onMounted(fetchData)
</script>

<style scoped>
.dashboard-page {
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-lg);
  margin-bottom: var(--space-xl);
}

.stat-card {
  background: var(--color-white);
  border-radius: var(--radius-xl);
  padding: var(--space-xl);
  display: flex;
  align-items: center;
  gap: var(--space-lg);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.stat-card.highlight {
  background: linear-gradient(135deg, var(--color-primary) 0%, #2a2a2a 100%);
}

.stat-card.highlight .stat-content .stat-value,
.stat-card.highlight .stat-content .stat-label {
  color: var(--color-white);
}

.stat-card.highlight .stat-label {
  opacity: 0.8;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon svg {
  width: 28px;
  height: 28px;
}

.stat-icon.users { background: rgba(0, 122, 255, 0.1); color: #007aff; }
.stat-icon.products { background: rgba(52, 199, 89, 0.1); color: #34c759; }
.stat-icon.orders { background: rgba(255, 149, 0, 0.1); color: #ff9500; }
.stat-icon.sales { background: rgba(201, 169, 98, 0.2); color: var(--color-accent); }

.stat-content {
  flex: 1;
}

.stat-value {
  display: block;
  font-size: 1.75rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  margin-bottom: 2px;
}

.stat-label {
  font-size: 0.875rem;
  color: var(--color-medium-gray);
}

/* Charts Grid */
.charts-grid {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: var(--space-lg);
}

.chart-card, .progress-card {
  background: var(--color-white);
  border-radius: var(--radius-xl);
  padding: var(--space-xl);
  box-shadow: var(--shadow-sm);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-xl);
}

.card-header h3 {
  font-size: 1rem;
  font-weight: 600;
}

.total-count {
  font-size: 0.875rem;
  color: var(--color-medium-gray);
}

.chart-container {
  height: 280px;
}

/* Progress List */
.progress-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

.progress-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.progress-label {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  font-size: 0.875rem;
}

.progress-label .count {
  margin-left: auto;
  color: var(--color-medium-gray);
}

.progress-label .status-name {
  color: var(--color-charcoal);
}

/* Status Dot */
.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.pending { background: #ff9500; }
.status-dot.paid { background: #007aff; }
.status-dot.shipped { background: #86868b; }
.status-dot.completed { background: #34c759; }
.status-dot.cancelled { background: #ff3b30; }

/* Progress Bar */
.progress-bar {
  height: 8px;
  background: var(--color-light-gray);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: var(--radius-full);
  transition: width 0.6s ease;
}

.progress-fill.pending { background: linear-gradient(90deg, #ffb84d 0%, #ff9500 100%); }
.progress-fill.paid { background: linear-gradient(90deg, #4da3ff 0%, #007aff 100%); }
.progress-fill.shipped { background: linear-gradient(90deg, #a8a8ad 0%, #86868b 100%); }
.progress-fill.completed { background: linear-gradient(90deg, #5cd679 0%, #34c759 100%); }
.progress-fill.cancelled { background: linear-gradient(90deg, #ff6961 0%, #ff3b30 100%); }

/* Responsive */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
