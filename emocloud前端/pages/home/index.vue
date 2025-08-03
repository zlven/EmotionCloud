<template>
  <view class="bg-gray-100 min-h-screen">
    <view class="container">
      <!-- 介绍文字 -->
      <view class="intro-text">
        <text>开始今天的情绪之旅~</text>
      </view>
      
      <!-- 情绪日志功能滑动入口（无新增功能） -->
      <swiper class="image-slider" :indicator-dots="true" :autoplay="false" :duration="500" @change="onSwiperChange">
        <!-- 滑动项1：查看日志列表 -->
        <swiper-item>
          <view class="log-entry-box" @click="gotoLogList">
            <view class="log-icon">📋</view>
            <view class="log-text">
              <text class="log-title">日志列表</text>
              <text class="log-desc">浏览所有情绪记录</text>
            </view>
          </view>
        </swiper-item>
        
        <!-- 滑动项2：情绪统计分析 -->
        <swiper-item>
          <view class="log-entry-box" @click="gotoLogAnalysis">
            <view class="log-icon">📊</view>
            <view class="log-text">
              <text class="log-title">情绪趋势</text>
              <text class="log-desc">查看情绪变化规律</text>
            </view>
          </view>
        </swiper-item>
        
        <!-- 滑动项3：日志搜索 -->
        <swiper-item>
          <view class="log-entry-box" @click="gotoLogSearch">
            <view class="log-icon">🔍</view>
            <view class="log-text">
              <text class="log-title">日志搜索</text>
              <text class="log-desc">查找特定时期记录</text>
            </view>
          </view>
        </swiper-item>
        
        <!-- 滑动索引 -->
        <view class="image-position-info">
          <text>{{ currentIndex + 1 }} / 3</text>
        </view>
      </swiper>
      
      <!-- 聊天模式选择区域（保持不变） -->
      <view class="chat-mode-container">
        <view class="chat-mode-box" @click="startChat">
          <text class="chat-mode-title">AI聊天</text>
          <text class="chat-mode-desc">沉浸式AI对话体验</text>
        </view>
        <view class="chat-mode-box" @click="startSceneChat">
          <text class="chat-mode-title">场景聊天</text>
          <text class="chat-mode-desc">在不同场景中与AI畅聊（敬请期待）</text>
        </view>
      </view>
      
      <!-- 情绪广场区域（保持不变） -->
      <view class="emotion-square" @click="goToSocial">
        <text class="square-title">情绪广场</text>
        <view class="post-container">
          <view class="post-item" v-for="(post, index) in postList" :key="index">
            <text class="post-title">{{ post.title }}</text>
            <text class="post-content">{{ post.content }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <GlobalPet />
  </view>
</template>

<script>
export default {
  data() {
    return {
      postList: [
        { title: "今天心情不错", content: "和朋友出去逛街，超级开心" },
        { title: "有点小烦恼", content: "工作上遇到了点小问题，希望能顺利解决" }
      ],
      currentIndex: 0 // 当前滑动索引
    };
  },
  onShow() {
    uni.showTabBar();
  },
  methods: {
    // 跳转到日志列表（核心功能）
    gotoLogList() {
      uni.navigateTo({
        url: '/pages/moodJournal/journal/Jlist' // 指向现有日志列表页
      });
    },
    
    // 跳转到情绪分析（统计功能）
    gotoLogAnalysis() {
      // 若没有单独分析页，可指向列表页并默认显示统计面板
      uni.navigateTo({
        url: '/pages/moodJournal/journal/Jlist?showAnalysis=true' 
      });
    },
    
    // 跳转到日志搜索（筛选功能）
    gotoLogSearch() {
      // 若没有单独搜索页，可指向列表页并默认显示搜索框
      uni.navigateTo({
        url: '/pages/moodJournal/journal/Jlist?showSearch=true'
      });
    },
    
    // 原有聊天和广场功能（保持不变）
    startChat() {
      uni.hideTabBar();
      uni.switchTab({ url: '/pages/moodCompanion/chat/chat' });
    },
    startSceneChat() {
      uni.hideTabBar();
      uni.switchTab({ url: '/pages/moodCompanion/chat/chat' });
    },
    goToSocial() {
      uni.switchTab({ url: '/pages/empathySocial/index' });
    },
    onSwiperChange(e) {
      this.currentIndex = e.detail.current;
    }
  }
};
</script>
<style scoped>
/* 基础样式保持不变，仅调整滑动项相关样式 */
.container {
  padding: 10px;
  min-height:100vh;
  background:linear-gradient(to right, #ffe4b4, #CCE5FF); 
}

.intro-text {
  text-align: center;
  font-size: 18px;
  margin: 20px 0;
  font-weight: 500;
  color: #5d4037; /* 深棕色文本 */
}

/* 滑动区域样式 */
.image-slider {
  height: 150px;
  width: 100%;
  margin: 10px 0 20px;
  position: relative;
}

/* 日志入口框样式 - 调整为暖色系渐变 */
.log-entry-box {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fff8e1 0%, #f3e5f5 100%);
  border-radius: 12px;
  margin: 0 10px;
  box-shadow: 0 3px 10px rgba(121, 85, 72, 0.1); /* 暖棕色阴影 */
  cursor: pointer;
}
.log-entry-box:active {
  transform: scale(0.98); /* 点击反馈 */
}

.log-icon {
  font-size: 40px;
  margin-right: 20px;
  color: #ff9800; /* 橙色图标 */
}

.log-text {
  display: flex;
  flex-direction: column;
}
.log-title {
  font-size: 18px;
  font-weight: 500;
  color: #5d4037; /* 深棕色标题 */
}
.log-desc {
  font-size: 14px;
  color: #795548; /* 棕色描述 */
  margin-top: 5px;
}

/* 滑动索引指示器 */
.image-position-info {
  position: absolute;
  top: 10px;
  right: 10px;
  color: #795548; /* 棕色文本 */
  background-color: rgba(255, 250, 240, 0.8); /* 暖白色背景 */
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 12px;
}

/* 以下为原有样式（聊天模式、广场等，调整为暖色系） */
.chat-mode-container {
  display: flex;
  justify-content: space-between;
  gap: 15px;
  margin: 25px 0;
  padding: 0 10px;
}
.chat-mode-box {
  flex: 1;
  padding: 20px 15px;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  background: linear-gradient(145deg, #fff8e1, #f3e5f5); /* 暖色系背景 */
  box-shadow: 0 3px 10px rgba(121, 85, 72, 0.1); /* 暖棕色阴影 */
}
.chat-mode-title {
  font-size: 15px;
  margin-bottom: 8px;
  color: #5d4037; /* 深棕色标题 */
  font-weight: 450;
}
.chat-mode-desc {
  font-size: 13px;
  color: #795548; /* 棕色描述 */
  text-align: center;
}
.emotion-square {
  background: linear-gradient(145deg, #fff8e1, #ede7f6); /* 暖色系背景 */
  padding: 25px 20px;
  border-radius: 18px;
  box-shadow: 0 4px 15px rgba(121, 85, 72, 0.08); /* 暖棕色阴影 */
  margin-top: -10px;
}
.square-title {
  font-size: 10px;
  margin-bottom: -2px;
  color: #795548; /* 棕色标题 */
  display: flex;
  align-items: center;
  justify-content: center;
}
.square-title::before, .square-title::after {
  content: "";
  flex: 1;
  height: 1px;
  background: linear-gradient(to right, transparent, rgba(121, 85, 72, 0.1), transparent); /* 暖色系分隔线 */
  margin: 0 15px;
}
.post-item {
  padding: 15px 0;
  border-bottom: 1px solid rgba(121, 85, 72, 0.05); /* 暖色系分隔线 */
}
.post-title {
  font-size: 15px;
  color: #5d4037; /* 深棕色标题 */
  margin-bottom: 6px;
  display: flex;
  align-items: center;
}
.post-title::before {
  content: "•";
  color: #ffccbc; /* 暖粉色标记 */
  margin-right: 8px;
  font-size: 24px;
}
.post-content {
  font-size: 13px;
  color: #795548; /* 棕色内容 */
  padding-left: 15px;
  border-left: 2px dotted #ffccbc; /* 暖粉色分隔线 */
}
</style>吗