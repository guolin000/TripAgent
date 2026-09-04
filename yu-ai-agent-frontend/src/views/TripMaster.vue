<template>
  <div class="love-master-container">
    <div class="header">
      <div class="back-button" @click="goBack">返回</div>
      <h1 class="title">AI旅游助手</h1>
      <div class="chat-id">会话ID: {{ chatId }}</div>
    </div>
    
    <div class="content-wrapper">
      <div class="chat-area">
        <ChatRoom 
          :messages="messages" 
          :connection-status="connectionStatus"
          ai-type="love"
          @send-message="sendMessage"
        />
      </div>
    </div>
    
    <div class="footer-container">
      <AppFooter />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useHead } from '@vueuse/head'
import ChatRoom from '../components/ChatRoom.vue'
import AppFooter from '../components/AppFooter.vue'
import { chatWithLoveApp } from '../api'

// 设置页面标题和元数据
useHead({
  title: 'AI旅游助手 - AI超级智能体应用平台',
  meta: [
    {
      name: 'description',
      content: 'AI旅游助手是AI超级智能体应用平台的专业旅游顾问，帮你解答各种旅游问题，提供旅游建议'
    },
    {
      name: 'keywords',
      content: 'AI旅游助手,旅游顾问,旅游咨询,AI聊天,旅游问题,AI智能体'
    }
  ]
})

const router = useRouter()
const messages = ref([])
const chatId = ref('')
const connectionStatus = ref('disconnected')
let eventSource = null

// 生成随机会话ID
const generateChatId = () => {
  return 'love_' + Math.random().toString(36).substring(2, 10)
}

// 添加消息到列表
const addMessage = (content, isUser) => {
  messages.value.push({
    content,
    isUser,
    time: new Date().getTime()
  })
}

// 发送消息
const sendMessage = (message) => {
  addMessage(message, true)
  
  // 连接SSE
  if (eventSource) {
    eventSource.close()
  }
  
  // 创建一个空的AI回复消息
  const aiMessageIndex = messages.value.length
  addMessage('', false)
  
  connectionStatus.value = 'connecting'
  eventSource = chatWithLoveApp(message, chatId.value)
  
  // 监听SSE消息
  eventSource.onmessage = (event) => {
    const data = event.data
    if (data && data !== '[DONE]') {
      // 更新最新的AI消息内容，而不是创建新消息
      if (aiMessageIndex < messages.value.length) {
        messages.value[aiMessageIndex].content += data
      }
    }
    
    if (data === '[DONE]') {
      connectionStatus.value = 'disconnected'
      eventSource.close()
    }
  }
  
  // 监听SSE错误
  eventSource.onerror = (error) => {
    console.error('SSE Error:', error)
    connectionStatus.value = 'error'
    eventSource.close()
  }
}

// 返回主页
const goBack = () => {
  router.push('/')
}

// 页面加载时添加欢迎消息
onMounted(() => {
  // 生成聊天ID
  chatId.value = generateChatId()
  
  // 添加欢迎消息
  addMessage('欢迎来到AI旅游助手，请告诉我你的旅游问题，我会尽力给予帮助和建议。', false)
})

// 组件销毁前关闭SSE连接
onBeforeUnmount(() => {
  if (eventSource) {
    eventSource.close()
  }
})
</script>

<style scoped>
/* =========================================
   页面整体
========================================= */
.love-master-container {
  --bg-primary: #070b14;
  --bg-secondary: #0b1120;
  --panel: rgba(15, 23, 42, 0.72);
  --panel-light: rgba(255, 255, 255, 0.045);

  --text-primary: #f8fafc;
  --text-secondary: #94a3b8;
  --text-muted: #64748b;

  --border: rgba(255, 255, 255, 0.09);
  --border-hover: rgba(56, 189, 248, 0.25);

  --blue: #38bdf8;
  --cyan: #22d3ee;
  --purple: #818cf8;

  position: relative;

  display: flex;
  flex-direction: column;

  width: 100%;
  min-height: 100vh;

  overflow: hidden;

  color: var(--text-primary);

  background:
      radial-gradient(
          circle at 50% -20%,
          rgba(56, 189, 248, 0.10),
          transparent 40%
      ),
      linear-gradient(
          135deg,
          #050811 0%,
          #080d18 50%,
          #050812 100%
      );

  font-family:
      Inter,
      "PingFang SC",
      "Microsoft YaHei",
      sans-serif;
}


/* =========================================
   顶部导航
========================================= */
.header {
  position: relative;
  z-index: 10;

  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;

  height: 72px;
  padding: 0 28px;

  border-bottom: 1px solid rgba(255, 255, 255, 0.07);

  background: rgba(7, 11, 20, 0.78);

  backdrop-filter: blur(22px);
  -webkit-backdrop-filter: blur(22px);

  box-shadow:
      0 10px 40px rgba(0, 0, 0, 0.16);
}


/* =========================================
   返回按钮
========================================= */
.back-button {
  justify-self: start;

  display: inline-flex;
  align-items: center;
  gap: 8px;

  height: 36px;
  padding: 0 13px;

  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 10px;

  background: rgba(255, 255, 255, 0.035);

  color: #cbd5e1;

  font-size: 13px;
  font-weight: 500;

  cursor: pointer;

  transition:
      background 0.25s ease,
      border-color 0.25s ease,
      color 0.25s ease,
      transform 0.25s ease;
}

.back-button:before {
  content: "←";

  font-size: 16px;

  color: #94a3b8;

  transition: transform 0.25s ease;
}

.back-button:hover {
  color: #f8fafc;

  background: rgba(56, 189, 248, 0.07);

  border-color: rgba(56, 189, 248, 0.2);

  transform: translateX(-2px);
}

.back-button:hover:before {
  transform: translateX(-3px);

  color: var(--blue);
}


/* =========================================
   标题
========================================= */
.title {
  margin: 0;

  color: #f8fafc;

  font-size: 17px;
  font-weight: 600;

  letter-spacing: -0.3px;

  white-space: nowrap;
}

.title::before {
  content: "✦";

  margin-right: 9px;

  color: var(--cyan);

  font-size: 14px;

  text-shadow:
      0 0 12px rgba(34, 211, 238, 0.7);
}


/* =========================================
   会话 ID
========================================= */
.chat-id {
  justify-self: end;

  padding: 7px 11px;

  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 8px;

  background: rgba(255, 255, 255, 0.025);

  color: #64748b;

  font-size: 11px;

  font-family:
      "SFMono-Regular",
      Consolas,
      monospace;

  letter-spacing: 0.3px;
}


/* =========================================
   主体
========================================= */
.content-wrapper {
  position: relative;
  z-index: 2;

  display: flex;
  flex-direction: column;

  flex: 1;

  min-height: 0;

  background:
      radial-gradient(
          circle at 50% 0%,
          rgba(56, 189, 248, 0.035),
          transparent 50%
      );
}


/* =========================================
   聊天区域
========================================= */
.chat-area {
  position: relative;

  flex: 1;

  width: min(1100px, calc(100% - 40px));

  margin: 24px auto;

  padding: 0;

  min-height: calc(100vh - 72px - 150px);

  overflow: hidden;

  border: 1px solid var(--border);
  border-radius: 22px;

  background:
      linear-gradient(
          145deg,
          rgba(255, 255, 255, 0.045),
          rgba(255, 255, 255, 0.015)
      ),
      rgba(9, 14, 26, 0.62);

  box-shadow:
      0 25px 80px rgba(0, 0, 0, 0.28),
      inset 0 1px 0 rgba(255, 255, 255, 0.035);

  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
}


/* =========================================
   聊天区域顶部光线
========================================= */
.chat-area::before {
  content: "";

  position: absolute;

  top: 0;
  left: 15%;

  width: 70%;
  height: 1px;

  background: linear-gradient(
      90deg,
      transparent,
      rgba(56, 189, 248, 0.35),
      transparent
  );

  pointer-events: none;

  z-index: 5;
}


/* =========================================
   背景科技光晕
========================================= */
.content-wrapper::before,
.content-wrapper::after {
  content: "";

  position: absolute;

  border-radius: 50%;

  filter: blur(100px);

  pointer-events: none;
}

.content-wrapper::before {
  width: 320px;
  height: 320px;

  top: -180px;
  left: -100px;

  background: rgba(14, 165, 233, 0.08);
}

.content-wrapper::after {
  width: 300px;
  height: 300px;

  right: -120px;
  bottom: -120px;

  background: rgba(99, 102, 241, 0.08);
}


/* =========================================
   Footer
========================================= */
.footer-container {
  position: relative;
  z-index: 5;

  margin-top: auto;

  background: rgba(5, 8, 16, 0.5);
}


/* =========================================
   滚动条
========================================= */
.chat-area :deep(::-webkit-scrollbar) {
  width: 6px;
  height: 6px;
}

.chat-area :deep(::-webkit-scrollbar-track) {
  background: transparent;
}

.chat-area :deep(::-webkit-scrollbar-thumb) {
  border-radius: 10px;

  background: rgba(148, 163, 184, 0.18);
}

.chat-area :deep(::-webkit-scrollbar-thumb:hover) {
  background: rgba(56, 189, 248, 0.3);
}


/* =========================================
   响应式：平板
========================================= */
@media (max-width: 768px) {
  .header {
    grid-template-columns: auto 1fr auto;

    height: 64px;

    padding: 0 16px;
  }

  .title {
    justify-self: center;

    font-size: 16px;
  }

  .back-button {
    height: 34px;

    padding: 0 10px;

    font-size: 12px;
  }

  .chat-id {
    max-width: 145px;

    overflow: hidden;

    text-overflow: ellipsis;

    white-space: nowrap;

    font-size: 10px;
  }

  .chat-area {
    width: calc(100% - 24px);

    margin: 12px auto;

    min-height: calc(100vh - 64px - 130px);

    border-radius: 18px;
  }
}


/* =========================================
   响应式：手机
========================================= */
@media (max-width: 480px) {
  .header {
    height: 58px;

    padding: 0 10px;
  }

  .back-button {
    width: 34px;
    height: 34px;

    padding: 0;

    justify-content: center;

    border-radius: 10px;

    font-size: 0;
  }

  .back-button:before {
    margin: 0;

    font-size: 17px;
  }

  .title {
    font-size: 15px;
  }

  .title::before {
    margin-right: 6px;

    font-size: 12px;
  }

  .chat-id {
    display: none;
  }

  .chat-area {
    width: calc(100% - 12px);

    margin: 6px auto;

    min-height: calc(100vh - 58px - 115px);

    border-radius: 16px;
  }
}


/* =========================================
   减少动画模式
========================================= */
@media (prefers-reduced-motion: reduce) {
  .back-button,
  .back-button:before {
    transition: none;
  }
}
</style>

