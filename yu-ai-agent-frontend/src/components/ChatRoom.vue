<template>
  <div class="chat-container">
    <!-- 聊天记录区域 -->
    <div class="chat-messages" ref="messagesContainer">
      <div v-for="(msg, index) in messages" :key="index" class="message-wrapper">
        <!-- AI消息 -->
        <div v-if="!msg.isUser" 
             class="message ai-message" 
             :class="[msg.type]">
          <div class="avatar ai-avatar">
            <AiAvatarFallback :type="aiType" />
          </div>
          <div class="message-bubble">
            <div class="message-content">
              {{ msg.content }}
              <span v-if="connectionStatus === 'connecting' && index === messages.length - 1" class="typing-indicator">▋</span>
            </div>
            <div class="message-time">{{ formatTime(msg.time) }}</div>
          </div>
        </div>
        
        <!-- 用户消息 -->
        <div v-else class="message user-message" :class="[msg.type]">
          <div class="message-bubble">
            <div class="message-content">{{ msg.content }}</div>
            <div class="message-time">{{ formatTime(msg.time) }}</div>
          </div>
          <div class="avatar user-avatar">
            <div class="avatar-placeholder">我</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input-container">
      <div class="chat-input">
        <textarea 
          v-model="inputMessage" 
          @keydown.enter.prevent="sendMessage"
          placeholder="请输入消息..." 
          class="input-box"
          :disabled="connectionStatus === 'connecting'"
        ></textarea>
        <button 
          @click="sendMessage" 
          class="send-button"
          :disabled="connectionStatus === 'connecting' || !inputMessage.trim()"
        >发送</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch, computed } from 'vue'
import AiAvatarFallback from './AiAvatarFallback.vue'

const props = defineProps({
  messages: {
    type: Array,
    default: () => []
  },
  connectionStatus: {
    type: String,
    default: 'disconnected'
  },
  aiType: {
    type: String,
    default: 'default'  // 'love' 或 'super'
  }
})

const emit = defineEmits(['send-message'])

const inputMessage = ref('')
const messagesContainer = ref(null)

// 根据AI类型选择不同头像
const aiAvatar = computed(() => {
  return props.aiType === 'love' 
    ? '/ai-love-avatar.png'  // 恋爱大师头像
    : '/ai-super-avatar.png' // 超级智能体头像
})

// 发送消息
const sendMessage = () => {
  if (!inputMessage.value.trim()) return
  
  emit('send-message', inputMessage.value)
  inputMessage.value = ''
}

// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 自动滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 监听消息变化与内容变化，自动滚动
watch(() => props.messages.length, () => {
  scrollToBottom()
})

watch(() => props.messages.map(m => m.content).join(''), () => {
  scrollToBottom()
})

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
/* =========================================================
   ChatRoom —— 清新自然 / AI 旅行助手
========================================================= */

.chat-container {
  --primary: #3b82f6;
  --primary-light: #60a5fa;
  --cyan: #38bdf8;
  --mint: #34d399;

  --text-main: #1e293b;
  --text-secondary: #64748b;
  --text-light: #94a3b8;

  --bg: #f5f9fc;
  --surface: rgba(255, 255, 255, 0.88);
  --surface-solid: #ffffff;

  --border: rgba(148, 163, 184, 0.18);

  position: relative;

  display: flex;
  flex-direction: column;

  width: 100%;
  height: 70vh;
  min-height: 600px;

  overflow: hidden;

  color: var(--text-main);

  background:
      radial-gradient(
          circle at 10% 0%,
          rgba(125, 211, 252, 0.18),
          transparent 32%
      ),
      radial-gradient(
          circle at 95% 100%,
          rgba(110, 231, 183, 0.14),
          transparent 30%
      ),
      linear-gradient(
          145deg,
          #f7fbff 0%,
          #f4f9fb 48%,
          #f7fbf9 100%
      );

  border-radius: 22px;
}


/* =========================================================
   背景装饰
========================================================= */

.chat-container::before {
  content: "";

  position: absolute;

  width: 260px;
  height: 260px;

  top: -150px;
  right: -80px;

  border-radius: 50%;

  background:
      radial-gradient(
          circle,
          rgba(56, 189, 248, 0.16),
          transparent 70%
      );

  pointer-events: none;
}

.chat-container::after {
  content: "";

  position: absolute;

  width: 220px;
  height: 220px;

  left: -100px;
  bottom: -100px;

  border-radius: 50%;

  background:
      radial-gradient(
          circle,
          rgba(52, 211, 153, 0.12),
          transparent 70%
      );

  pointer-events: none;
}


/* =========================================================
   聊天记录区域
========================================================= */

.chat-messages {
  position: absolute;

  top: 0;
  left: 0;
  right: 0;
  bottom: 88px;

  overflow-y: auto;

  padding: 30px 34px 30px;

  display: flex;
  flex-direction: column;

  scroll-behavior: smooth;

  z-index: 2;
}


/* 滚动条 */

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  border-radius: 20px;

  background: rgba(100, 116, 139, 0.18);
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: rgba(59, 130, 246, 0.3);
}


/* Firefox */

.chat-messages {
  scrollbar-width: thin;
  scrollbar-color:
      rgba(100, 116, 139, 0.18)
      transparent;
}


/* =========================================================
   消息
========================================================= */

.message-wrapper {
  width: 100%;

  margin-bottom: 20px;

  display: flex;
  flex-direction: column;
}

.message {
  display: flex;
  align-items: flex-start;

  max-width: 82%;

  animation:
      messageAppear
      0.28s
      ease-out;
}


/* =========================================================
   AI 消息
========================================================= */

.ai-message {
  margin-right: auto;
}

.ai-avatar {
  margin-right: 11px;

  width: 38px;
  height: 38px;

  flex-shrink: 0;

  border-radius: 13px;

  background:
      linear-gradient(
          135deg,
          #38bdf8,
          #60a5fa 55%,
          #34d399
      );

  box-shadow:
      0 5px 16px rgba(59, 130, 246, 0.18);

  overflow: hidden;
}


/* AI 气泡 */

.ai-message .message-bubble {
  position: relative;

  max-width: 100%;

  padding: 13px 16px 10px;

  color: #334155;

  background:
      rgba(255, 255, 255, 0.94);

  border: 1px solid rgba(148, 163, 184, 0.15);

  border-radius: 6px 18px 18px 18px;

  box-shadow:
      0 4px 16px rgba(15, 23, 42, 0.055);
}


/* AI 气泡小装饰 */

.ai-message .message-bubble::before {
  content: "";

  position: absolute;

  top: -1px;
  left: 15px;

  width: 24px;
  height: 2px;

  border-radius: 10px;

  background:
      linear-gradient(
          90deg,
          #38bdf8,
          #34d399
      );

  opacity: 0.6;
}


/* =========================================================
   用户消息
========================================================= */

.user-message {
  margin-left: auto;

  flex-direction: row;
}

.user-avatar {
  margin-left: 11px;

  width: 38px;
  height: 38px;

  flex-shrink: 0;

  border-radius: 13px;

  overflow: hidden;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;

  display: flex;
  align-items: center;
  justify-content: center;

  color: white;

  font-size: 13px;
  font-weight: 600;

  background:
      linear-gradient(
          135deg,
          #60a5fa,
          #3b82f6
      );

  box-shadow:
      0 5px 16px rgba(59, 130, 246, 0.18);
}


/* 用户气泡 */

.user-message .message-bubble {
  max-width: 100%;

  padding: 13px 16px 10px;

  color: #ffffff;

  background:
      linear-gradient(
          135deg,
          #60a5fa,
          #3b82f6
      );

  border-radius: 18px 6px 18px 18px;

  box-shadow:
      0 5px 18px rgba(59, 130, 246, 0.18);
}


/* =========================================================
   消息内容
========================================================= */

.message-content {
  font-size: 15px;

  line-height: 1.75;

  white-space: pre-wrap;

  word-break: break-word;
}


/* AI文字 */

.ai-message .message-content {
  color: #334155;
}


/* 用户文字 */

.user-message .message-content {
  color: #ffffff;
}


/* =========================================================
   时间
========================================================= */

.message-time {
  margin-top: 5px;

  color: #94a3b8;

  font-size: 10px;

  line-height: 1;

  text-align: right;

  opacity: 0.85;
}

.user-message .message-time {
  color: rgba(255, 255, 255, 0.68);
}


/* =========================================================
   输入区域
========================================================= */

.chat-input-container {
  position: absolute;

  left: 0;
  right: 0;
  bottom: 0;

  height: 88px;

  padding: 15px 24px;

  z-index: 10;

  background:
      linear-gradient(
          to top,
          rgba(247, 251, 253, 0.98),
          rgba(247, 251, 253, 0.88)
      );

  border-top: 1px solid rgba(148, 163, 184, 0.12);

  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);

  box-sizing: border-box;
}


/* =========================================================
   输入框主体
========================================================= */

.chat-input {
  display: flex;
  align-items: center;

  width: min(900px, 100%);

  height: 58px;

  margin: 0 auto;

  padding: 6px 7px 6px 17px;

  box-sizing: border-box;

  border:
      1px solid
      rgba(148, 163, 184, 0.2);

  border-radius: 17px;

  background:
      rgba(255, 255, 255, 0.94);

  box-shadow:
      0 5px 25px rgba(15, 23, 42, 0.07),
      inset 0 1px 0 rgba(255, 255, 255, 0.8);

  transition:
      border-color 0.25s ease,
      box-shadow 0.25s ease;
}

.chat-input:focus-within {
  border-color:
      rgba(59, 130, 246, 0.35);

  box-shadow:
      0 7px 30px rgba(59, 130, 246, 0.10),
      0 0 0 3px rgba(59, 130, 246, 0.05);
}


/* =========================================================
   输入框
========================================================= */

.input-box {
  flex: 1;

  width: 100%;
  height: 42px;

  padding: 10px 5px;

  box-sizing: border-box;

  resize: none;

  border: none;
  outline: none;

  background: transparent;

  color: #334155;

  font-family:
      Inter,
      "PingFang SC",
      "Microsoft YaHei",
      sans-serif;

  font-size: 14px;

  line-height: 22px;

  overflow-y: auto;

  scrollbar-width: none;
}

.input-box::-webkit-scrollbar {
  display: none;
}

.input-box::placeholder {
  color: #a8b4c3;

  transition: color 0.2s;
}

.input-box:focus::placeholder {
  color: #c0cad5;
}


/* =========================================================
   发送按钮
========================================================= */

.send-button {
  flex-shrink: 0;

  width: 43px;
  height: 43px;

  margin-left: 8px;

  padding: 0;

  display: flex;
  align-items: center;
  justify-content: center;

  border: none;

  border-radius: 13px;

  color: white;

  font-size: 0;

  cursor: pointer;

  background:
      linear-gradient(
          135deg,
          #60a5fa,
          #3b82f6
      );

  box-shadow:
      0 5px 14px rgba(59, 130, 246, 0.22);

  transition:
      transform 0.2s ease,
      box-shadow 0.2s ease,
      opacity 0.2s ease;
}


/* 使用箭头代替“发送” */

.send-button::after {
  content: "↑";

  font-size: 20px;

  font-weight: 500;

  line-height: 1;

  transform:
      translateY(-1px);
}

.send-button:hover:not(:disabled) {
  transform:
      translateY(-2px);

  box-shadow:
      0 8px 20px rgba(59, 130, 246, 0.28);
}

.send-button:active:not(:disabled) {
  transform:
      translateY(0);
}


/* 禁用 */

.send-button:disabled {
  opacity: 0.45;

  cursor: not-allowed;

  box-shadow: none;
}


/* =========================================================
   正在输入
========================================================= */

.typing-indicator {
  display: inline-block;

  margin-left: 3px;

  color: #3b82f6;

  font-weight: 600;

  animation:
      typingBlink
      0.9s
      ease-in-out
      infinite;
}


/* =========================================================
   消息动画
========================================================= */

@keyframes messageAppear {
  from {
    opacity: 0;

    transform:
        translateY(7px);
  }

  to {
    opacity: 1;

    transform:
        translateY(0);
  }
}

@keyframes typingBlink {
  0%,
  100% {
    opacity: 0.25;
  }

  50% {
    opacity: 1;
  }
}


/* =========================================================
   连续 AI 消息
========================================================= */

.ai-message + .ai-message {
  margin-top: -7px;
}

.ai-message + .ai-message .avatar {
  visibility: hidden;
}

.ai-message + .ai-message .message-bubble {
  border-top-left-radius: 10px;
}


/* =========================================================
   不同类型消息
========================================================= */

.ai-answer {
  animation:
      messageAppear
      0.3s
      ease-out;
}

.ai-final {
  /* 最终回答保持自然白色卡片 */
}

.ai-error {
  opacity: 0.78;
}

.user-question {
  /* 用户问题保持蓝色气泡 */
}


/* =========================================================
   移动端：768px
========================================================= */

@media (max-width: 768px) {

  .chat-container {
    min-height: 560px;

    border-radius: 18px;
  }

  .chat-messages {
    padding: 22px 18px 24px;

    bottom: 82px;
  }

  .message {
    max-width: 90%;
  }

  .message-content {
    font-size: 14px;

    line-height: 1.7;
  }

  .ai-avatar,
  .user-avatar {
    width: 35px;
    height: 35px;

    border-radius: 11px;
  }

  .ai-avatar {
    margin-right: 8px;
  }

  .user-avatar {
    margin-left: 8px;
  }

  .message-bubble {
    padding: 11px 13px 9px;
  }

  .chat-input-container {
    height: 82px;

    padding:
        11px 14px;
  }

  .chat-input {
    height: 58px;

    padding-left: 14px;

    border-radius: 16px;
  }

  .input-box {
    font-size: 14px;
  }

  .send-button {
    width: 41px;
    height: 41px;

    border-radius: 12px;
  }
}


/* =========================================================
   移动端：480px
========================================================= */

@media (max-width: 480px) {

  .chat-container {
    min-height: 500px;

    border-radius: 15px;
  }

  .chat-messages {
    padding:
        16px 10px 20px;

    bottom: 75px;
  }

  .message {
    max-width: 94%;
  }

  .message-wrapper {
    margin-bottom: 15px;
  }

  .ai-avatar,
  .user-avatar {
    width: 31px;
    height: 31px;

    border-radius: 9px;
  }

  .ai-avatar {
    margin-right: 7px;
  }

  .user-avatar {
    margin-left: 7px;
  }

  .message-bubble {
    padding:
        9px 11px 8px;

    border-radius: 5px 15px 15px 15px;
  }

  .user-message .message-bubble {
    border-radius:
        15px 5px 15px 15px;
  }

  .message-content {
    font-size: 13px;

    line-height: 1.65;
  }

  .message-time {
    font-size: 9px;
  }

  .chat-input-container {
    height: 75px;

    padding:
        8px 9px;
  }

  .chat-input {
    height: 56px;

    padding-left: 12px;

    border-radius: 15px;
  }

  .input-box {
    height: 40px;

    font-size: 13px;
  }

  .send-button {
    width: 39px;
    height: 39px;

    border-radius: 11px;
  }

  .send-button::after {
    font-size: 18px;
  }
}


/* =========================================================
   减少动画
========================================================= */

@media (prefers-reduced-motion: reduce) {

  .message {
    animation: none;
  }

  .typing-indicator {
    animation: none;
  }

  .send-button,
  .chat-input {
    transition: none;
  }
}
</style>
