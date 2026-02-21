<template>
  <div class="ai-chat-page">
    <div class="chat-container">
      <!-- Header -->
      <div class="chat-header">
        <div class="header-content">
          <div class="ai-avatar">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2Z" stroke="currentColor" stroke-width="1.5"/>
              <path d="M8 14C8 14 9.5 16 12 16C14.5 16 16 14 16 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              <path d="M9 9H9.01M15 9H15.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <div class="header-info">
            <h1>智能购物顾问</h1>
            <p>随时为您提供专业的购物建议</p>
          </div>
        </div>
      </div>

      <!-- Messages -->
      <div class="chat-messages" ref="messagesRef">
        <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role]">
          <div class="message-avatar" v-if="msg.role === 'ai'">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2Z" stroke="currentColor" stroke-width="1.5"/>
              <path d="M8 14C8 14 9.5 16 12 16C14.5 16 16 14 16 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              <path d="M9 9H9.01M15 9H15.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <div class="message-content">
            <div class="message-bubble">{{ msg.content }}</div>
          </div>
        </div>

        <!-- Typing Indicator -->
        <div v-if="loading" class="message ai">
          <div class="message-avatar">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2Z" stroke="currentColor" stroke-width="1.5"/>
              <path d="M8 14C8 14 9.5 16 12 16C14.5 16 16 14 16 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              <path d="M9 9H9.01M15 9H15.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <div class="message-content">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- Input -->
      <div class="chat-input">
        <div class="input-wrapper">
          <input 
            v-model="inputMessage" 
            type="text"
            placeholder="输入您的问题..."
            @keyup.enter="sendMessage"
            :disabled="loading"
          />
          <button class="send-btn" @click="sendMessage" :disabled="!inputMessage.trim() || loading">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M22 2L11 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M22 2L15 22L11 13L2 9L22 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { aiApi } from '../api'

const messages = ref([
  { role: 'ai', content: '您好！我是您的智能购物顾问，很高兴为您服务。无论是商品推荐、尺码建议还是搭配方案，我都可以帮助您。请问有什么可以帮您的吗？' }
])
const inputMessage = ref('')
const loading = ref(false)
const messagesRef = ref()

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return
  
  const userMessage = inputMessage.value.trim()
  messages.value.push({ role: 'user', content: userMessage })
  inputMessage.value = ''
  scrollToBottom()
  
  loading.value = true
  try {
    const res = await aiApi.chat(userMessage)
    messages.value.push({ role: 'ai', content: res.data })
  } catch {
    messages.value.push({ role: 'ai', content: '抱歉，服务暂时不可用，请稍后再试。' })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

onMounted(scrollToBottom)
</script>

<style scoped>
.ai-chat-page {
  height: calc(100vh - 80px);
  background: var(--color-off-white);
  padding: var(--space-xl);
  display: flex;
  justify-content: center;
  box-sizing: border-box;
}

.chat-container {
  width: 100%;
  max-width: 1200px;
  height: 100%;
  background: var(--color-white);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* Header */
.chat-header {
  background: linear-gradient(135deg, var(--color-primary) 0%, #2a2a2a 100%);
  padding: var(--space-xl);
}

.header-content {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.ai-avatar {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-accent);
}

.ai-avatar svg {
  width: 32px;
  height: 32px;
}

.header-info h1 {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-white);
  margin-bottom: 2px;
}

.header-info p {
  font-size: 0.875rem;
  color: rgba(255, 255, 255, 0.7);
}

/* Messages */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-xl);
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

.message {
  display: flex;
  gap: var(--space-md);
  animation: messageSlide 0.3s ease-out;
}

@keyframes messageSlide {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  background: var(--color-light-gray);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-accent);
  flex-shrink: 0;
}

.message-avatar svg {
  width: 24px;
  height: 24px;
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: var(--space-md) var(--space-lg);
  border-radius: var(--radius-lg);
  font-size: 0.9375rem;
  line-height: 1.6;
}

.message.ai .message-bubble {
  background: var(--color-light-gray);
  color: var(--color-charcoal);
  border-bottom-left-radius: var(--space-xs);
}

.message.user .message-bubble {
  background: var(--color-primary);
  color: var(--color-white);
  border-bottom-right-radius: var(--space-xs);
}

/* Typing Indicator */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: var(--space-md) var(--space-lg);
  background: var(--color-light-gray);
  border-radius: var(--radius-lg);
  border-bottom-left-radius: var(--space-xs);
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: var(--color-medium-gray);
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) { animation-delay: 0s; }
.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-8px); }
}

/* Input */
.chat-input {
  padding: var(--space-lg) var(--space-xl);
  border-top: 1px solid var(--color-light-gray);
  background: var(--color-white);
  flex-shrink: 0;
  width: 100%;
  box-sizing: border-box;
}

.input-wrapper {
  display: flex;
  gap: var(--space-md);
  background: var(--color-light-gray);
  border-radius: var(--radius-full);
  padding: 6px 6px 6px 20px;
  width: 100%;
  box-sizing: border-box;
}

.input-wrapper input {
  flex: 1;
  min-width: 0;
  width: 100%;
  border: none;
  background: transparent;
  font-size: 0.9375rem;
  outline: none;
  color: var(--color-charcoal);
}

.input-wrapper input::placeholder {
  color: var(--color-medium-gray);
}

.send-btn {
  width: 48px;
  height: 48px;
  background: var(--color-primary);
  border: none;
  border-radius: var(--radius-full);
  color: var(--color-white);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.send-btn:hover:not(:disabled) {
  background: var(--color-secondary);
  transform: scale(1.05);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.send-btn svg {
  width: 20px;
  height: 20px;
}

.input-hint {
  text-align: center;
  font-size: 0.75rem;
  color: var(--color-medium-gray);
  margin-top: var(--space-sm);
}
</style>
