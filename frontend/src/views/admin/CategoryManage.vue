<template>
  <div class="manage-page">
    <div class="page-card">
      <div class="card-header">
        <h3>分类管理</h3>
        <button class="add-btn" @click="showDialog()">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M12 5V19M5 12H19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          新增分类
        </button>
      </div>

      <div class="table-wrapper">
        <el-table :data="categories" v-loading="loading" style="width: 100%">
          <el-table-column label="ID" prop="id" width="80" />
          <el-table-column label="名称" prop="name" min-width="150" />
          <el-table-column label="描述" prop="description" min-width="250" />
          <el-table-column label="排序" prop="sort" width="100" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <div class="action-btns">
                <button class="action-btn edit" @click="showDialog(row)">编辑</button>
                <button class="action-btn delete" @click="handleDelete(row)">删除</button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="450px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" placeholder="请输入分类描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <button class="dialog-btn cancel" @click="dialogVisible = false">取消</button>
        <button class="dialog-btn confirm" @click="handleSubmit">确定</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { categoryApi } from '../../api'

const categories = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = reactive({ id: null, name: '', description: '', sort: 0 })
const rules = { name: [{ required: true, message: '请输入名称' }] }

const fetchData = async () => {
  loading.value = true
  try {
    const res = await categoryApi.list()
    categories.value = res.data || []
  } finally {
    loading.value = false
  }
}

const showDialog = (row = null) => {
  isEdit.value = !!row
  Object.assign(form, row || { id: null, name: '', description: '', sort: 0 })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await categoryApi.update(form)
  } else {
    await categoryApi.add(form)
  }
  ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
  dialogVisible.value = false
  fetchData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该分类吗？', '提示', { type: 'warning' })
  await categoryApi.delete(row.id)
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

.add-btn {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: 12px 24px;
  background: var(--color-primary);
  color: var(--color-white);
  border: none;
  border-radius: var(--radius-full);
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.add-btn:hover {
  background: var(--color-secondary);
  transform: translateY(-1px);
}

.add-btn svg {
  width: 18px;
  height: 18px;
}

.table-wrapper {
  padding: var(--space-lg);
}

.action-btns {
  display: flex;
  gap: var(--space-sm);
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

.action-btn.edit {
  background: var(--color-light-gray);
  color: var(--color-dark-gray);
}

.action-btn.edit:hover {
  background: var(--color-primary);
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

.dialog-btn {
  padding: 12px 28px;
  border-radius: var(--radius-full);
  font-size: 0.9375rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.dialog-btn.cancel {
  background: var(--color-light-gray);
  border: none;
  color: var(--color-dark-gray);
  margin-right: var(--space-sm);
}

.dialog-btn.confirm {
  background: var(--color-primary);
  border: none;
  color: var(--color-white);
}

.dialog-btn.confirm:hover {
  background: var(--color-secondary);
}
</style>
