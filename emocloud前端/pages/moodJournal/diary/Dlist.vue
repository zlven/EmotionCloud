<template>
  <view class="diary-list-container">
    <!-- 顶部搜索栏 -->
    <view class="search-bar">
      <text class="search-icon" @click="toggleSearch">🔍</text>
      <input 
        v-show="searchVisible" 
        v-model="searchQuery" 
        class="search-input" 
        placeholder="查找日记（日期，内容关键词）..." 
        @confirm="handleSearch"
      />
    </view>

    <!-- 左侧列表区域 -->
    <view class="left-section">
      <!-- 新建按钮移至顶部 -->
      <view class="create-button-container">
        <uni-button @click="createTodayDiary" type="primary" size="mini">
          <text class="create-icon">+</text> 新建日记  
        </uni-button>
      </view>
      
      <scroll-view class="diary-scroll" scroll-y>
        <!-- 今天的日记项 -->
        <view v-if="hasTodayDiary" class="diary-item today-diary" @click="selectDiary(todayDiary)">
          <view class="emotion-icon">{{ getEmotionIcon(todayDiary.emotionTag) }}</view>
          <view class="date-vertical">
            <text class="date-month-day">{{ formatMonthDay(todayDiary.cratetime) }}</text>
            <text class="date-year">{{ formatYear(todayDiary.cratetime) }}</text>
          </view>
        </view>
        <view v-else class="diary-item today-diary create-entry" @click="selectTodayDiary">
          <view class="emotion-icon">+</view>
          <view class="date-vertical">
            <text class="date-month-day">{{ formatMonthDayToday() }}</text>
            <text class="date-year">{{ new Date().getFullYear() }}年</text>
          </view>
        </view>
        
        <view v-if="diaryList.length === 0 && !hasTodayDiary" class="empty-list">
          <text class="empty-tip">暂无日记记录</text>
        </view>
        <view v-else-if="diaryList.length > 0" v-for="diary in otherDiaries" :key="diary.id" class="diary-item" @click="selectDiary(diary)">
          <view class="emotion-icon">{{ getEmotionIcon(diary.emotionTag) }}</view>
          <view class="date-vertical">
            <text class="date-month-day">{{ formatMonthDay(diary.cratetime) }}</text>
            <text class="date-year">{{ formatYear(diary.cratetime) }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 右侧内容区域 -->
    <view class="right-section">
      <!-- 空状态 -->
      <view v-if="diaryList.length === 0 && !hasTodayDiary" class="empty-content">
        <view class="empty-illustration">📔</view>
        <text class="empty-title">还没有任何日记</text>
        <text class="empty-subtitle">点击左侧"新建日记"开始记录</text>
        <uni-button @click="createDiary" type="primary" size="large">创建第一篇日记</uni-button>
      </view>
      
      <!-- 日记预览 -->
      <view v-else-if="hasTodayDiary && (selectedDiary === todayDiary || isTodaySelected)" class="diary-preview">
        <view class="diary-header">
          <text class="diary-title">{{ todayDiary.title || '无标题' }}</text>
        </view>
        <view class="button-group">
          <uni-button @click="editDiary" type="primary" size="mini">编辑</uni-button>
          <uni-button @click="deleteDiary(todayDiary.id)" type="error" size="mini">删除</uni-button>
        </view>
        <view class="diary-meta">
          <text>{{ formatDate(todayDiary.cratetime) }}</text>
          <text>{{ todayDiary.emotionTag || '无情绪标签' }}</text>
        </view>
        <view class="diary-content-wrapper">
          <view class="diary-content" v-html="todayDiary.content"></view>
        </view>
      </view>
      
      <!-- 今天的空白日记预览 -->
      <view v-else-if="isTodaySelected && !hasTodayDiary" class="diary-preview">
        <view class="diary-header">
          <text class="diary-title">创建今天的日记</text>
        </view>
        <view class="button-group">
          <uni-button @click="createTodayDiary" type="primary" size="mini">立即创建</uni-button>
        </view>
        <view class="diary-meta">
          <text>{{ todayDate }}</text>
          <text>无情绪标签</text>
        </view>
        <view class="diary-content empty-hint">
          <text>今天还没有记录，点击"立即创建"开始写下你的心情吧～</text>
        </view>
      </view>
      
      <!-- 其他日记的预览 -->
      <view v-else-if="selectedDiary && !isTodaySelected" class="diary-preview">
        <view class="diary-header">
          <text class="diary-title">{{ selectedDiary.title || '无标题' }}</text>
        </view>
        <view class="button-group">
          <uni-button @click="editDiary" type="primary" size="mini">编辑</uni-button>
          <uni-button @click="deleteDiary(selectedDiary.id)" type="error" size="mini">删除</uni-button>
        </view>
        <view class="diary-meta">
          <text>{{ formatDate(selectedDiary.cratetime) }}</text>
          <text>{{ selectedDiary.emotionTag || '无情绪标签' }}</text>
        </view>
        <view class="diary-content-wrapper">
          <view class="diary-content" v-html="selectedDiary.content"></view>
        </view>
      </view>
    </view>
  </view>
   <GlobalPet />
</template>

<script>
export default {
  data() {
    return {
      searchVisible: false,
      searchQuery: '',
      diaryList: [],
      selectedDiary: null,
      isTodaySelected: false,
      todayDate: '',
      todayDateISO: '',
      todayDiary: null,
      isNewDiaryCreated: false,
      userId: null
    };
  },
  onLoad() {
    const userInfo = uni.getStorageSync('userInfo');
    this.userId = userInfo && userInfo.id ? userInfo.id : null;
    console.log('获取的用户ID:', this.userId);
    if (this.userId) {
      this.setTodayDate();
      this.fetchDiaries();
    } else {
      uni.showToast({ title: '请先登录', icon: 'none' });
      uni.navigateTo({ url: '/pages/auth/login/index' });
    }
  },
  onShow() {
    this.fetchDiaries();
    
    // 检查是否有新创建的日记（从编辑页返回时）
    const pages = getCurrentPages();
    const prevPage = pages[pages.length - 2];
    if (prevPage && prevPage.route.includes('Ddetail') && prevPage.isNewDiaryCreated) {
      this.isNewDiaryCreated = true;
      this.fetchDiaries(); // 重新加载日记列表
    }
  },
  onUnload() {
    // 重置标记，避免重复触发
    this.isNewDiaryCreated = false;
  },
  computed: {
    hasTodayDiary() {
      if (!this.diaryList.length) return false;
      
      this.todayDiary = this.diaryList.find(diary => {
        const diaryDate = new Date(diary.cratetime).toISOString().split('T')[0];
        return diaryDate === this.todayDateISO;
      });
      
      return !!this.todayDiary;
    },
    otherDiaries() {
      return this.diaryList.filter(diary => {
        const diaryDate = new Date(diary.cratetime).toISOString().split('T')[0];
        return diaryDate !== this.todayDateISO;
      });
    }
  },
  methods: {
    setTodayDate() {
      const today = new Date();
      this.todayDate = `${today.getFullYear()}年${today.getMonth() + 1}月${today.getDate()}日`;
      this.todayDateISO = today.toISOString().split('T')[0];
    },
    fetchDiaries() {
      uni.request({
        url: `http://localhost:8080/emotion-log/diary/user/${this.userId}/sorted`,
        method: 'GET',
        success: (res) => {
          if (res.statusCode === 200) {
            this.diaryList = res.data.sort((a, b) => {
              return new Date(b.cratetime) - new Date(a.cratetime);
            });
            
            // 自动选中今天的日记（如果有）或今天的空白项
            if (this.hasTodayDiary) {
              this.selectedDiary = this.todayDiary;
              this.isTodaySelected = true;
            } else if (this.diaryList.length > 0) {
              this.selectedDiary = this.diaryList[0];
              this.isTodaySelected = false;
            } else {
              this.isTodaySelected = false;
              this.selectedDiary = null;
            }
          }
        },
        fail: () => {
          uni.showToast({
            title: '获取日记列表失败',
            icon: 'none'
          });
        }
      });
    },
    toggleSearch() {
      this.searchVisible = !this.searchVisible;
      if (!this.searchVisible) this.searchQuery = '';
    },
    handleSearch() {
      if (!this.searchQuery.trim()) return;
      
      uni.request({
        url: 'http://localhost:8080/emotion-log/diary/find/keyword',
        method: 'GET',
        data: { keyword: this.searchQuery },
        success: (res) => {
          if (res.statusCode === 200) {
            this.diaryList = res.data;
            if (this.diaryList.length > 0) {
              this.hasTodayDiary; // 触发计算属性更新todayDiary
              this.selectedDiary = this.todayDiary || this.diaryList[0];
              this.isTodaySelected = !!this.todayDiary;
            }
          }
        }
      });
    },
    selectDiary(diary) {
      this.selectedDiary = diary;
      this.isTodaySelected = false;
    },
    selectTodayDiary() {
      this.isTodaySelected = true;
      if (this.hasTodayDiary) {
        this.selectedDiary = this.todayDiary;
      } else {
        this.selectedDiary = null;
      }
    },
    createTodayDiary() {
      uni.navigateTo({
        url: '/pages/moodJournal/diary/Ddetail'
      });
    },
    createDiary() {
      uni.navigateTo({
        url: '/pages/moodJournal/diary/Ddetail'
      });
    },
    editDiary() {
      if (!this.selectedDiary) {
        uni.showToast({ title: '请先选择日记', icon: 'none' });
        return;
      }
      
      uni.navigateTo({
        url: `/pages/moodJournal/diary/Ddetail?id=${this.selectedDiary.id}`
      });
    },
    deleteDiary(diaryId) {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这篇日记吗？此操作不可恢复。',
        success: (res) => {
          if (res.confirm) {
            // 显示加载状态
            uni.showLoading({
              title: '删除中...'
            });
            
            uni.request({
              url: `http://localhost:8080/emotion-log/diary/delete/${diaryId}`,
              method: 'DELETE',
              success: (res) => {
                uni.hideLoading();
                if (res.statusCode === 200) {
                  uni.showToast({
                    title: '删除成功',
                    icon: 'success'
                  });
                  
                  // 重新加载日记列表
                  this.fetchDiaries();
                  
                  // 如果删除的是当前选中的日记，重置选中状态
                  if (this.selectedDiary && this.selectedDiary.id === diaryId) {
                    this.selectedDiary = null;
                    this.isTodaySelected = false;
                  }
                } else {
                  uni.showToast({
                    title: '删除失败',
                    icon: 'none'
                  });
                }
              },
              fail: () => {
                uni.hideLoading();
                uni.showToast({
                  title: '网络错误，删除失败',
                  icon: 'none'
                });
              }
            });
          }
        }
      });
    },
    formatDate(dateStr) {
      const date = new Date(dateStr);
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${date.getFullYear()}年${month}月${day}日`;
    },
    formatMonthDay(dateStr) {
      const date = new Date(dateStr);
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${month}月${day}日`;
    },
    formatYear(dateStr) {
      const date = new Date(dateStr);
      return `${date.getFullYear()}年`;
    },
    formatMonthDayToday() {
      const today = new Date();
      const month = String(today.getMonth() + 1).padStart(2, '0');
      const day = String(today.getDate()).padStart(2, '0');
      return `${month}月${day}日`;
    },
    getEmotionIcon(tag) {
      const icons = {
        '快乐': '😀',
        '悲伤': '😢',
        '平静': '😌',
        '愤怒': '😠',
        '惊喜': '😮'
      };
      return icons[tag] || '📔';
    }
  }
};
</script>
<style scoped>
/* 整体容器 - 调整为更柔和的暖色系背景 */
.diary-list-container {
  display: flex;
  height: 100vh;
  background: linear-gradient(to right, #ffdedf, #FFE4B5, #feffef); /* 更浅的暖底色 */
  overflow: hidden;
}

/* 搜索栏 - 降低饱和度 */
.search-bar {
  position: fixed;
  top: 50px;
  left: 20px;
  z-index: 100;
  background: rgba(255,255,255,0.95);
  border-radius: 20px;
  padding: 5px 10px;
  box-shadow: 0 2px 8px rgba(218, 165, 32, 0.08); /* 更浅的阴影 */
  transition: all 0.3s ease;
}
.search-icon {
  font-size: 18px;
  color: #d4a06a; /* 降低饱和度的暖棕色 */
  padding: 4px;
  cursor: pointer;
}
.search-input {
  margin-left: 8px;
  padding: 6px 12px;
  border-radius: 16px;
  border: none;
  background: transparent;
  width: 180px;
  font-size: 14px;
  outline: none;
  transition: width 0.3s ease;
}
.search-input:focus {
  width: 220px;
}

/* 左侧列表区域 - 柔和化处理 */
.left-section {
  width: 20%;
  max-width: 240px;
  padding-top: 60px;
  border-right: 1px solid #f1e0cc; /* 更浅的边框 */
  background: rgba(255,255,255,0.9); /* 更通透的背景 */
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

/* 新建按钮容器 - 降低按钮饱和度 */
.create-button-container {
  padding: 0px 10px 0;
}
.create-button-container uni-button {
  width: 100%;
  background-color: #d4a06a; /* 柔和的暖棕色按钮 */
  color: white;
  border-radius: 5px;
  padding: 8px;
  font-size: 10px;
}
.create-icon {
  margin-right: 13px;
}

.diary-scroll {
  flex: 1;
  height: calc(100vh - 100px);
  padding: 10px;
  overflow-y: auto;
}

/* 日记项样式 - 添加边框并优化阴影 */
.diary-item {
  padding: 5px 5px;
  margin: 8px 0;
  border-radius: 8px;
  display: flex;
  align-items: center;
  background: rgba(255,255,255,0.95);
  border: 1px solid #f1e0cc; /* 新增：暖色系边框 */
  box-shadow: 0 2px 6px rgba(218, 165, 32, 0.06); /* 更淡的阴影 */
  cursor: pointer;
  transition: all 0.2s;
}
.diary-item:active {
  transform: scale(0.98);
  box-shadow: 0 1px 3px rgba(218, 165, 32, 0.1);
}
.diary-item:hover {
  border-color: #e6c39e; /* 悬停时边框加深 */
  box-shadow: 0 3px 8px rgba(218, 165, 32, 0.08);
}

/* 竖排日期样式 - 降低颜色强度 */
.date-vertical {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  padding: 5px 0;
}
.date-month-day {
  font-size: 16px;
  font-weight: 600;
  color: #9b7c58; /* 柔和的棕色 */
  line-height: 1.2;
}
.date-year {
  font-size: 13px;
  color: #b89d7e; /* 更浅的棕色 */
  margin-top: 3px;
}

.emotion-icon {
  font-size: 20px;
  margin-right: 10px;
  width: 24px;
  text-align: center;
  color: #d4a06a; /* 柔和的暖色调 */
}

/* 今天的日记项 - 优化边框样式 */
.today-diary {
  background: linear-gradient(135deg, #fff5ea, #ffffff); /* 更淡的渐变 */
  border-left: 4px solid #d4a06a; /* 左侧强调边框 */
  border-top: 1px solid #f1e0cc; /* 统一边框 */
  border-right: 1px solid #f1e0cc;
  border-bottom: 1px solid #f1e0cc;
  margin-bottom: 10px;
}
.today-diary:hover {
  border-color: #e6c39e;
  border-left-color: #d4a06a; /* 保持左侧强调色 */
}

/* 新建日记项 - 调整虚线边框 */
.create-entry {
  background: linear-gradient(135deg, #fff7f0, #ffffff); /* 更淡的渐变 */
  position: relative;
  border: 1px dashed #d4a06a; /* 虚线边框 */
}
.create-entry:hover {
  border-color: #c78d59; /* 悬停时虚线加深 */
}

.create-tag {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: #d4a06a; /* 柔和的标签色 */
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.empty-list {
  padding: 30px 20px;
  text-align: center;
}

.empty-tip {
  color: #b89d7e; /* 柔和的提示文字 */
  font-size: 14px;
}

/* 右侧内容区域 */
.right-section {
  width: 80%;
  flex: 1;
  padding: 60px 15px 15px;
  overflow-y: auto;
  box-sizing: border-box;
}

/* 日记预览区域 - 柔和化处理 */
.diary-preview {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  background: rgba(255,255,255,0.95);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(218, 165, 32, 0.08); /* 更淡的阴影 */
  border: 1px solid #f8ecd8; /* 更浅的边框 */
  box-sizing: border-box;
}

/* 标题样式 - 降低饱和度 */
.diary-header {
  margin-bottom: 10px;
}
.diary-title {
  font-size: 22px;
  font-weight: 600;
  color: #c78d59; /* 柔和的暖棕色标题 */
  line-height: 1.4;
  word-wrap: break-word;
  white-space: normal;
}

/* 操作按钮组 */
.button-group {
  margin-bottom: 15px;
  display: flex;
  gap: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f5efe3; /* 更浅的分割线 */
}

.diary-meta {
  display: flex;
  color: #9b7c58; /* 柔和的元数据色 */
  font-size: 13px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.diary-meta text:first-child {
  margin-right: 20px;
  color: #d4a06a; /* 柔和的强调色 */
}

/* 内容容器 */
.diary-content-wrapper {
  width: 100%;
  overflow-x: auto;
  padding: 5px 0;
}

.diary-content {
  font-size: 15px;
  line-height: 1.8;
  color: #705844; /* 柔和的深棕色内容 */
  word-wrap: break-word;
  word-break: break-word;
  white-space: pre-wrap;
}

.empty-content {
  text-align: center;
  max-width: 500px;
  margin: 0 auto;
  padding: 60px 0;
}

.empty-illustration {
  font-size: 80px;
  color: #f1e0cc; /* 更淡的插图色 */
  margin-bottom: 25px;
}

.empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #d4a06a; /* 柔和的标题色 */
  margin-bottom: 12px;
}

.empty-subtitle {
  font-size: 14px;
  color: #b89d7e; /* 柔和的副标题色 */
  margin-bottom: 30px;
  line-height: 1.6;
}

.empty-hint {
  color: #9b7c58; /* 柔和的提示色 */
  font-style: italic;
  text-align: center;
  padding: 40px 20px;
  background: rgba(255, 248, 235, 0.5); /* 更淡的背景 */
  border-radius: 8px;
  margin-top: 20px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .left-section {
    width: 25%;
    max-width: none;
  }
  .right-section {
    width: 75%;
  }
  .diary-title {
    font-size: 19px;
  }
  .date-month-day {
    font-size: 15px;
  }
}

@media (max-width: 480px) {
  .left-section {
    width: 30%;
  }
  .right-section {
    width: 70%;
  }
  .date-month-day {
    font-size: 14px;
  }
  .date-year {
    font-size: 12px;
  }
  .search-input {
    width: 300px;
  }
}
</style>