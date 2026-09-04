<template>
  <div class="home-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
      <div class="grid-overlay"></div>
    </div>

    <!-- 顶部区域 -->
    <header class="header">
      <div class="brand-tag">
        <span class="status-dot"></span>
        AI INTELLIGENT PLATFORM
      </div>

      <h1 class="main-title">
        <span>AI</span> 旅游超级智能体
      </h1>

      <p class="subtitle">
        让智能，重新定义你的每一次旅程
      </p>

      <div class="title-line">
        <span></span>
        <i></i>
        <span></span>
      </div>
    </header>

    <!-- 应用区域 -->
    <main class="apps-container">

      <!-- AI旅游助手 -->
      <div
          class="app-card travel-card"
          @click="navigateTo('/trip-master')"
      >
        <div class="card-background"></div>

        <div class="card-header">
          <div class="app-icon travel-icon">
            <span>✈</span>
          </div>

          <div class="card-index">01</div>
        </div>

        <div class="app-info">
          <div class="app-label">TRAVEL ASSISTANT</div>
          <h2 class="app-title">AI旅游助手</h2>
          <p class="app-desc">
            智能解答旅游问题，提供目的地信息、
            行程建议与出行灵感。
          </p>
        </div>

        <div class="card-footer">
          <span>开始探索</span>
          <div class="arrow">→</div>
        </div>
      </div>

      <!-- AI超级智能体 -->
      <div
          class="app-card agent-card"
          @click="navigateTo('/super-agent')"
      >
        <div class="card-background"></div>

        <div class="card-header">
          <div class="app-icon agent-icon">
            <span>✦</span>
          </div>

          <div class="card-index">02</div>
        </div>

        <div class="app-info">
          <div class="app-label">AI SUPER AGENT</div>
          <h2 class="app-title">AI超级智能体</h2>
          <p class="app-desc">
            面向复杂任务的智能助手，
            连接知识、工具与模型，解决专业问题。
          </p>
        </div>

        <div class="card-footer">
          <span>立即体验</span>
          <div class="arrow">→</div>
        </div>
      </div>

    </main>

    <!-- 底部提示 -->
    <div class="bottom-hint">
      <span class="hint-line"></span>
      <span>CHOOSE YOUR AI EXPERIENCE</span>
      <span class="hint-line"></span>
    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useHead } from '@vueuse/head'
import AppFooter from '../components/AppFooter.vue'

useHead({
  title: 'AI超级智能体应用平台 - 首页',
  meta: [
    {
      name: 'description',
      content: 'AI超级智能体应用平台，提供AI旅游助手和AI超级智能体服务'
    },
    {
      name: 'keywords',
      content: 'AI智能体,AI应用,AI旅游助手,AI助手,AI超级智能体'
    }
  ]
})

const router = useRouter()

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
/* =========================
   基础变量
========================= */
:root {
  --bg: #070b14;
  --card: rgba(16, 23, 38, 0.72);
  --text: #f8fafc;
  --muted: #94a3b8;
  --border: rgba(255, 255, 255, 0.09);
  --blue: #38bdf8;
  --cyan: #22d3ee;
  --purple: #8b5cf6;
}

/* =========================
   页面
========================= */
.home-container {
  /* 核心：强制占满整个视口 */
  width: 100%;
  min-width: 100%;
  max-width: none;

  min-height: 100vh;
  height: 100vh;

  position: relative;
  overflow: hidden;

  display: flex;
  flex-direction: column;

  box-sizing: border-box;

  color: var(--text);

  background:
      radial-gradient(
          circle at 50% -20%,
          rgba(56, 189, 248, 0.12),
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

/* =========================
   背景
========================= */
.background-decoration {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.18;
  animation: orbFloat 12s ease-in-out infinite alternate;
}

.orb-1 {
  width: 420px;
  height: 420px;
  background: #0ea5e9;
  top: -180px;
  left: -100px;
}

.orb-2 {
  width: 360px;
  height: 360px;
  background: #7c3aed;
  right: -100px;
  top: 30%;
  animation-delay: -4s;
}

.orb-3 {
  width: 320px;
  height: 320px;
  background: #06b6d4;
  bottom: -180px;
  left: 35%;
  animation-delay: -7s;
}

.grid-overlay {
  position: absolute;
  inset: 0;

  background-image:
      linear-gradient(
          rgba(255, 255, 255, 0.025) 1px,
          transparent 1px
      ),
      linear-gradient(
          90deg,
          rgba(255, 255, 255, 0.025) 1px,
          transparent 1px
      );

  background-size: 60px 60px;

  mask-image: linear-gradient(
      to bottom,
      rgba(0, 0, 0, 0.8),
      transparent 85%
  );
}

/* =========================
   Header
========================= */
.header {
  position: relative;
  z-index: 2;

  text-align: center;

  padding: 82px 20px 40px;
}

.brand-tag {
  display: inline-flex;
  align-items: center;
  gap: 8px;

  padding: 7px 14px;

  border: 1px solid rgba(56, 189, 248, 0.18);
  border-radius: 999px;

  background: rgba(56, 189, 248, 0.05);

  color: #7dd3fc;

  font-size: 11px;
  letter-spacing: 2px;
  font-weight: 600;

  backdrop-filter: blur(12px);
}

.status-dot {
  width: 6px;
  height: 6px;

  border-radius: 50%;

  background: #22c55e;

  box-shadow:
      0 0 8px rgba(34, 197, 94, 0.8);

  animation: pulse 2s infinite;
}

.main-title {
  margin: 24px 0 12px;

  font-size: clamp(2.5rem, 5vw, 4.2rem);
  line-height: 1.15;
  font-weight: 700;

  letter-spacing: -2px;

  color: #f8fafc;

  text-shadow:
      0 4px 30px rgba(0, 0, 0, 0.35);
}

.main-title span {
  background: linear-gradient(
      135deg,
      #38bdf8,
      #22d3ee,
      #818cf8
  );

  -webkit-background-clip: text;
  background-clip: text;

  color: transparent;
}

.subtitle {
  margin: 0;

  color: #94a3b8;

  font-size: 15px;
  letter-spacing: 1px;
}

.title-line {
  display: flex;
  align-items: center;
  justify-content: center;

  gap: 10px;

  margin: 30px auto 0;

  width: min(360px, 70%);
}

.title-line span {
  height: 1px;
  flex: 1;

  background: linear-gradient(
      90deg,
      transparent,
      rgba(56, 189, 248, 0.45)
  );
}

.title-line span:last-child {
  background: linear-gradient(
      90deg,
      rgba(56, 189, 248, 0.45),
      transparent
  );
}

.title-line i {
  width: 5px;
  height: 5px;

  border-radius: 50%;

  background: #38bdf8;

  box-shadow:
      0 0 12px rgba(56, 189, 248, 0.8);
}

/* =========================
   Cards
========================= */
.apps-container {
  position: relative;
  z-index: 2;

  /* 真正占满屏幕 */
  width: 100%;
  max-width: none;

  /* 只保留左右响应式内边距 */
  padding: 0 clamp(20px, 4vw, 64px);

  margin: 45px 0 30px;

  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));

  gap: clamp(20px, 2vw, 40px);

  flex: 1;

  box-sizing: border-box;
}

.app-card {
  position: relative;

  min-height: clamp(320px, 35vh, 460px);
  padding: clamp(28px, 2.5vw, 48px);

  display: flex;
  flex-direction: column;

  border-radius: clamp(20px, 1.5vw, 28px);

  overflow: hidden;
  cursor: pointer;

  border: 1px solid var(--border);

  background:
      linear-gradient(
          145deg,
          rgba(255, 255, 255, 0.075),
          rgba(255, 255, 255, 0.025)
      ),
      rgba(10, 15, 27, 0.72);

  box-shadow:
      0 20px 60px rgba(0, 0, 0, 0.25),
      inset 0 1px 0 rgba(255, 255, 255, 0.06);

  backdrop-filter: blur(20px);

  transition:
      transform 0.45s cubic-bezier(0.2, 0.8, 0.2, 1),
      border-color 0.35s,
      box-shadow 0.35s;
}

.app-card::before {
  content: "";

  position: absolute;

  inset: 0;

  border-radius: inherit;

  background:
      linear-gradient(
          135deg,
          rgba(56, 189, 248, 0.08),
          transparent 40%
      );

  opacity: 0;

  transition: opacity 0.4s;
}

.app-card:hover {
  transform: translateY(-8px);

  border-color: rgba(56, 189, 248, 0.28);

  box-shadow:
      0 30px 80px rgba(0, 0, 0, 0.4),
      0 0 40px rgba(56, 189, 248, 0.08);
}

.app-card:hover::before {
  opacity: 1;
}

.travel-card:hover {
  border-color: rgba(34, 211, 238, 0.35);
}

.agent-card:hover {
  border-color: rgba(139, 92, 246, 0.35);
}

.card-background {
  position: absolute;

  width: 220px;
  height: 220px;

  right: -80px;
  top: -90px;

  border-radius: 50%;

  background: rgba(56, 189, 248, 0.08);

  filter: blur(20px);

  transition: transform 0.5s;
}

.agent-card .card-background {
  background: rgba(139, 92, 246, 0.09);
}

.app-card:hover .card-background {
  transform: scale(1.4);
}

/* =========================
   Card Header
========================= */
.card-header {
  position: relative;

  display: flex;
  align-items: center;
  justify-content: space-between;
}

.app-icon {
  width: 64px;
  height: 64px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 18px;

  font-size: 28px;

  color: white;

  box-shadow:
      0 12px 30px rgba(0, 0, 0, 0.25);

  transition: transform 0.4s;
}

.travel-icon {
  background:
      linear-gradient(
          135deg,
          #0891b2,
          #2563eb
      );
}

.agent-icon {
  background:
      linear-gradient(
          135deg,
          #7c3aed,
          #4f46e5
      );
}

.app-card:hover .app-icon {
  transform: translateY(-3px) rotate(-3deg);
}

.card-index {
  color: rgba(255, 255, 255, 0.22);

  font-size: 12px;
  font-weight: 600;

  letter-spacing: 2px;
}

/* =========================
   Card Content
========================= */
.app-info {
  position: relative;

  margin-top: 42px;
}

.app-label {
  margin-bottom: 10px;

  color: #64748b;

  font-size: 10px;
  font-weight: 700;

  letter-spacing: 2px;
}

.app-title {
  margin: 0 0 14px;

  color: #f8fafc;

  font-size: 26px;
  font-weight: 650;

  letter-spacing: -0.5px;
}

.app-desc {
  max-width: 360px;

  margin: 0;

  color: #94a3b8;

  font-size: 14px;

  line-height: 1.8;
}

/* =========================
   Footer Button
========================= */
.card-footer {
  position: relative;

  margin-top: auto;
  padding-top: 30px;

  display: flex;
  align-items: center;
  justify-content: space-between;

  color: #cbd5e1;

  font-size: 13px;
  font-weight: 600;
}

.arrow {
  width: 38px;
  height: 38px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 50%;

  border: 1px solid rgba(255, 255, 255, 0.1);

  background: rgba(255, 255, 255, 0.04);

  color: #e2e8f0;

  font-size: 18px;

  transition:
      transform 0.35s,
      background 0.35s;
}

.app-card:hover .arrow {
  transform: translateX(5px);

  background: rgba(56, 189, 248, 0.12);
}

.agent-card:hover .arrow {
  background: rgba(139, 92, 246, 0.14);
}

/* =========================
   Bottom Hint
========================= */
.bottom-hint {
  position: relative;
  z-index: 2;

  display: flex;
  align-items: center;
  justify-content: center;

  gap: 14px;

  margin: 5px auto 24px;

  color: #475569;

  font-size: 9px;

  letter-spacing: 2px;
}

.hint-line {
  width: 35px;
  height: 1px;

  background: rgba(148, 163, 184, 0.15);
}

/* =========================
   Animation
========================= */
@keyframes orbFloat {
  0% {
    transform: translate3d(0, 0, 0);
  }

  100% {
    transform: translate3d(30px, 25px, 0);
  }
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0.4;
  }
}

/* =========================
   Responsive
========================= */
@media (max-width: 768px) {
  .header {
    position: relative;
    z-index: 2;

    text-align: center;

    padding:
        clamp(55px, 7vh, 100px)
        clamp(20px, 5vw, 80px)
        clamp(30px, 4vh, 50px);
  }

  .main-title {
    margin: clamp(18px, 2.5vh, 28px) 0 12px;

    font-size: clamp(2.4rem, 4vw, 4.5rem);
    line-height: 1.15;
    font-weight: 700;

    letter-spacing: -2px;

    color: #f8fafc;

    text-shadow:
        0 4px 30px rgba(0, 0, 0, 0.35);
  }

  .app-card {
    min-height: 320px;
  }
}

@media (max-width: 480px) {
  .header {
    padding-top: 48px;
  }

  .brand-tag {
    font-size: 9px;
    letter-spacing: 1.5px;
  }

  .main-title {
    font-size: 2.15rem;
  }

  .subtitle {
    font-size: 13px;
  }

  .apps-container {
    width: calc(100% - 28px);
    gap: 16px;
  }

  .app-card {
    min-height: 300px;
    padding: 25px;
    border-radius: 20px;
  }

  .app-icon {
    width: 56px;
    height: 56px;
    border-radius: 15px;
    font-size: 24px;
  }

  .app-info {
    margin-top: 32px;
  }

  .app-title {
    font-size: 23px;
  }

  .app-desc {
    max-width: 520px;

    margin: 0;

    color: #94a3b8;

    font-size: clamp(13px, 0.9vw, 15px);

    line-height: 1.8;
  }
}
</style>
