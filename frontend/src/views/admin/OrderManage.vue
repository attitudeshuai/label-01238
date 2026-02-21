<template>
  <div class="manage-page">
    <div class="page-card">
      <div class="card-header">
        <h3>订单管理</h3>
        <div class="header-stats">
          <span class="stat-item">共 {{ orders.length }} 个订单</span>
        </div>
      </div>

      <div class="table-wrapper">
        <el-table :data="orders" v-loading="loading" style="width: 100%">
          <el-table-column label="订单编号" prop="orderNo" width="200" />
          <el-table-column label="用户" prop="username" width="120" />
          <el-table-column label="商品" min-width="250">
            <template #default="{ row }">
              <div class="order-items">
                <div v-for="item in row.items" :key="item.id" class="order-item">
                  <span class="item-name">{{ item.productName }}</span>
                  <span class="item-qty">×{{ item.quantity }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="金额" width="120">
            <template #default="{ row }">
              <span class="price">¥{{ row.totalAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <span class="status-badge" :class="statusClass[row.status]">
                {{ statusText[row.status] }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="下单时间" prop="createTime" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <div class="action-btns">
                <button v-if="row.status === 1" class="action-btn ship" @click="updateStatus(row, 2)">发货</button>
                <button v-if="row.status === 2" class="action-btn complete" @click="updateStatus(row, 3)">完成</button>
                <button v-if="row.status === 0" class="action-btn cancel" @click="updateStatus(row, 4)">取消</button>
                <button class="action-btn delete" @click="handleDelete(row)">删除</button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '../../api'

const orders = ref([])
const loading = ref(false)

const statusText = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已完成', 4: '已取消' }
const statusClass = { 0: 'pending', 1: 'paid', 2: 'shipped', 3: 'completed', 4: 'cancelled' }

const fetchData = async () => {
  loading.value = true
  try {
    const res = await orderApi.all()
    orders.value = res.data || []
  } finally {
    loading.value = false
  }
}

const updateStatus = async (row, status) => {
  await orderApi.updateStatus(row.id, status)
  ElMessage.success('操作成功')
  fetchData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该订单吗？', '提示', { type: 'warning' })
  await orderApi.delete(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.manage-page {
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.page-card {
  background: var(--color-white);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-xl);
  border-bottom: 1px solid var(--color-light-gray);
}

.card-header h3 {
  font-size: 1.125rem;
  font-weight: 600;
}

.header-stats {
  color: var(--color-medium-gray);
  font-size: 0.875rem;
}

.table-wrapper {
  padding: var(--space-lg);
}

.order-items {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.item-name {
  font-size: 0.875rem;
}

.item-qty {
  font-size: 0.75rem;
  color: var(--color-medium-gray);
}

.price {
  font-weight: 600;
  color: var(--color-primary);
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: var(--radius-full);
  font-size: 0.75rem;
  font-weight: 500;
}

.status-badge.pending { background: rgba(255, 149, 0, 0.1); color: var(--color-warning); }
.status-badge.paid { background: rgba(0, 122, 255, 0.1); color: var(--color-info); }
.status-badge.shipped { background: var(--color-light-gray); color: var(--color-medium-gray); }
.status-badge.completed { background: rgba(52, 199, 89, 0.1); color: var(--color-success); }
.status-badge.cancelled { background: rgba(255, 59, 48, 0.1); color: var(--color-error); }

.action-btns {
  display: flex;
  gap: var(--space-sm);
  flex-wrap: wrap;
}

.action-btn {
  padding: 6px 14px;
  border: none;
  border-radius: var(--radius-full);
  font-size: 0.8125rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.action-btn.ship {
  background: rgba(0, 122, 255, 0.1);
  color: var(--color-info);
}

.action-btn.ship:hover {
  background: var(--color-info);
  color: var(--color-white);
}

.action-btn.complete {
  background: rgba(52, 199, 89, 0.1);
  color: var(--color-success);
}

.action-btn.complete:hover {
  background: var(--color-success);
  color: var(--color-white);
}

.action-btn.cancel {
  background: rgba(255, 149, 0, 0.1);
  color: var(--color-warning);
}

.action-btn.cancel:hover {
  background: var(--color-warning);
  color: var(--color-white);
}

.action-btn.delete {
  background: rgba(255, 59, 48, 0.1);
  color: var(--color-error);
}

.action-btn.delete:hover {
  background: var(--color-error);
  color: var(--color-white);
}
</style>
