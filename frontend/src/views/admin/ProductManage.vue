<template>
  <div class="manage-page">
    <div class="page-card">
      <div class="card-header">
        <h3>商品管理</h3>
        <button class="add-btn" @click="showDialog()">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M12 5V19M5 12H19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          新增商品
        </button>
      </div>

      <div class="table-wrapper">
        <el-table :data="products" v-loading="loading" style="width: 100%">
          <el-table-column label="商品" min-width="280">
            <template #default="{ row }">
              <div class="product-cell">
                <img :src="row.image" class="product-thumb" />
                <div class="product-info">
                  <span class="product-name">{{ row.name }}</span>
                  <span class="product-category">{{ row.categoryName || '未分类' }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="价格" width="120">
            <template #default="{ row }">
              <span class="price">¥{{ row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column label="库存" prop="stock" width="100" />
          <el-table-column label="销量" prop="sales" width="100" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <span class="status-badge" :class="row.status === 1 ? 'active' : 'inactive'">
                {{ row.status === 1 ? '上架' : '下架' }}
              </span>
            </template>
          </el-table-column>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '新增商品'" width="560px" class="custom-dialog">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <div class="form-row">
          <el-form-item label="分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
              <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="价格" prop="price">
            <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="库存" prop="stock">
            <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
          </el-form-item>
          <el-form-item label="状态">
            <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" />
          </el-form-item>
        </div>
        <el-form-item label="商品图片" prop="image">
          <div class="image-upload-area">
            <div class="image-preview" v-if="form.image">
              <img :src="form.image" alt="商品图片" />
              <div class="image-overlay">
                <button type="button" class="remove-btn" @click="form.image = ''">
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
            </div>
            <div class="upload-box" v-else>
              <input type="file" ref="fileInputRef" accept="image/*" @change="handleFileChange" class="file-input" />
              <div class="upload-content" @click="triggerFileInput">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M21 15V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M17 8L12 3L7 8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M12 3V15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>点击上传图片</span>
                <p>支持 JPG、PNG 格式</p>
              </div>
            </div>
            <div class="url-input">
              <span class="divider-text">或输入图片URL</span>
              <el-input v-model="form.image" placeholder="https://example.com/image.jpg" />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
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
import { productApi, categoryApi } from '../../api'

const products = ref([])
const categories = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const fileInputRef = ref()

const form = reactive({ id: null, name: '', categoryId: null, price: 0, stock: 0, image: '', description: '', status: 1 })
const rules = {
  name: [{ required: true, message: '请输入名称' }],
  categoryId: [{ required: true, message: '请选择分类' }],
  price: [{ required: true, message: '请输入价格' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const [productRes, categoryRes] = await Promise.all([productApi.all(), categoryApi.list()])
    products.value = productRes.data || []
    categories.value = categoryRes.data || []
  } finally {
    loading.value = false
  }
}

const showDialog = (row = null) => {
  isEdit.value = !!row
  Object.assign(form, row || { id: null, name: '', categoryId: null, price: 0, stock: 0, image: '', description: '', status: 1 })
  dialogVisible.value = true
}

const triggerFileInput = () => {
  fileInputRef.value?.click()
}

const handleFileChange = (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }
  
  // 验证文件大小 (最大 5MB)
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 5MB')
    return
  }
  
  // 转换为 Base64
  const reader = new FileReader()
  reader.onload = (event) => {
    form.image = event.target?.result
  }
  reader.readAsDataURL(file)
  
  // 清空 input 以便重复选择同一文件
  e.target.value = ''
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await productApi.update(form)
  } else {
    await productApi.add(form)
  }
  ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
  dialogVisible.value = false
  fetchData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该商品吗？', '提示', { type: 'warning' })
  await productApi.delete(row.id)
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

/* Product Cell */
.product-cell {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.product-thumb {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-md);
  object-fit: cover;
  background: var(--color-light-gray);
}

.product-info {
  display: flex;
  flex-direction: column;
}

.product-name {
  font-weight: 500;
  margin-bottom: 2px;
}

.product-category {
  font-size: 0.8125rem;
  color: var(--color-medium-gray);
}

.price {
  font-weight: 600;
  color: var(--color-primary);
}

/* Status Badge */
.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: var(--radius-full);
  font-size: 0.75rem;
  font-weight: 500;
}

.status-badge.active {
  background: rgba(52, 199, 89, 0.1);
  color: var(--color-success);
}

.status-badge.inactive {
  background: var(--color-light-gray);
  color: var(--color-medium-gray);
}

/* Action Buttons */
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

/* Form */
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-lg);
}

/* Image Upload */
.image-upload-area {
  width: 100%;
}

.image-preview {
  position: relative;
  width: 200px;
  height: 200px;
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: var(--color-light-gray);
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.image-preview:hover .image-overlay {
  opacity: 1;
}

.remove-btn {
  width: 40px;
  height: 40px;
  background: var(--color-white);
  border: none;
  border-radius: var(--radius-full);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-error);
  transition: all var(--transition-fast);
}

.remove-btn:hover {
  background: var(--color-error);
  color: var(--color-white);
}

.remove-btn svg {
  width: 20px;
  height: 20px;
}

.upload-box {
  position: relative;
  width: 200px;
  height: 200px;
}

.file-input {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
  z-index: -1;
}

.upload-content {
  width: 100%;
  height: 100%;
  border: 2px dashed var(--color-medium-gray);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
  color: var(--color-medium-gray);
}

.upload-content:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: var(--color-light-gray);
}

.upload-content svg {
  width: 40px;
  height: 40px;
}

.upload-content span {
  font-size: 0.9375rem;
  font-weight: 500;
}

.upload-content p {
  font-size: 0.75rem;
  margin: 0;
}

.url-input {
  margin-top: var(--space-md);
}

.divider-text {
  display: block;
  font-size: 0.8125rem;
  color: var(--color-medium-gray);
  margin-bottom: var(--space-sm);
}

/* Dialog Buttons */
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
