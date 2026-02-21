<template>
  <div class="manage-page">
    <div class="page-card">
      <div class="card-header">
        <h3>用户管理</h3>
        <button class="add-btn" @click="openDialog()">
          <svg viewBox="0 0 24 24" fill="none" width="18" height="18">
            <path d="M12 5V19M5 12H19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          新增用户
        </button>
      </div>

      <div class="table-wrapper">
        <el-table :data="users" v-loading="loading" style="width: 100%">
          <el-table-column label="用户" min-width="200">
            <template #default="{ row }">
              <div class="user-cell">
                <div class="user-avatar" :class="row.role">
                  {{ row.username.charAt(0).toUpperCase() }}
                </div>
                <div class="user-info">
                  <span class="user-name">{{ row.nickname || row.username }}</span>
                  <span class="user-username">@{{ row.username }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="邮箱" prop="email" min-width="180" />
          <el-table-column label="手机" prop="phone" width="140" />
          <el-table-column label="角色" width="100">
            <template #default="{ row }">
              <span class="role-badge" :class="row.role">
                {{ row.role === 'admin' ? '管理员' : '用户' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-switch 
                v-model="row.status" 
                :active-value="1" 
                :inactive-value="0" 
                @change="updateStatus(row)"
                :disabled="row.role === 'admin'"
              />
            </template>
          </el-table-column>
          <el-table-column label="注册时间" prop="createTime" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <button class="action-btn edit" @click="openDialog(row)">编辑</button>
              <button 
                class="action-btn delete" 
                @click="handleDelete(row)"
                :disabled="row.role === 'admin'"
              >
                删除
              </button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- User Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
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
import { userApi } from '../../api'

const users = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const defaultForm = { username: '', password: '', nickname: '', email: '', phone: '', role: 'user' }
const form = reactive({ ...defaultForm })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await userApi.list()
    users.value = res.data || []
  } finally {
    loading.value = false
  }
}

const openDialog = (row = null) => {
  isEdit.value = !!row
  Object.assign(form, row ? { ...row } : { ...defaultForm })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await userApi.update(form)
    ElMessage.success('更新成功')
  } else {
    await userApi.add(form)
    ElMessage.success('添加成功')
  }
  dialogVisible.value = false
  fetchData()
}

const updateStatus = async (row) => {
  await userApi.updateStatus(row.id, row.status)
  ElMessage.success('状态更新成功')
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该用户吗？', '提示', { type: 'warning' })
  await userApi.delete(row.id)
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
  gap: var(--space-xs);
  padding: 10px 20px;
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

.table-wrapper {
  padding: var(--space-lg);
}

/* User Cell */
.user-cell {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 1rem;
  color: var(--color-white);
  background: linear-gradient(135deg, var(--color-medium-gray) 0%, var(--color-dark-gray) 100%);
}

.user-avatar.admin {
  background: linear-gradient(135deg, var(--color-accent) 0%, var(--color-accent-dark) 100%);
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 500;
  margin-bottom: 2px;
}

.user-username {
  font-size: 0.8125rem;
  color: var(--color-medium-gray);
}

/* Role Badge */
.role-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: var(--radius-full);
  font-size: 0.75rem;
  font-weight: 500;
}

.role-badge.admin {
  background: rgba(201, 169, 98, 0.15);
  color: var(--color-accent-dark);
}

.role-badge.user {
  background: var(--color-light-gray);
  color: var(--color-medium-gray);
}

/* Action Button */
.action-btn {
  padding: 6px 14px;
  border: none;
  border-radius: var(--radius-full);
  font-size: 0.8125rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
  margin-right: 6px;
}

.action-btn.edit {
  background: rgba(0, 122, 255, 0.1);
  color: var(--color-info);
}

.action-btn.edit:hover {
  background: var(--color-info);
  color: var(--color-white);
}

.action-btn.delete {
  background: rgba(255, 59, 48, 0.1);
  color: var(--color-error);
}

.action-btn.delete:hover:not(:disabled) {
  background: var(--color-error);
  color: var(--color-white);
}

.action-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* Dialog */
.dialog-btn {
  padding: 10px 24px;
  border-radius: var(--radius-full);
  font-size: 0.875rem;
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
