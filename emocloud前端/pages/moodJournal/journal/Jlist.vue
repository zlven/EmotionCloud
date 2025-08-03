<template>
  <view class="journal-list-page">
    <!-- 顶部搜索栏 -->
    <view class="search-bar">
      <view class="search-input-container">
        <text class="search-icon">🔍</text>
        <input 
          type="text" 
          placeholder="搜索关键词..." 
          :value="searchKeyword"
          @input="handleSearchInput"
          placeholder-class="search-placeholder"
        />
      </view>
      <view class="filter-tabs">
        <view 
          class="filter-tab" 
          :class="{ 'active': filterType === 'all' }"
          @click="changeFilter('all')"
        >
          全部
        </view>
        <view 
          class="filter-tab" 
          :class="{ 'active': filterType === 'week' }"
          @click="changeFilter('week')"
        >
          本周
        </view>
        <view 
          class="filter-tab" 
          :class="{ 'active': filterType === 'month' }"
          @click="changeFilter('month')"
        >
          本月
        </view>
      </view>
    </view>

    <!-- 登录提示 -->
    <view class="login-tip" v-if="showLoginTip">
      <text class="tip-text">请先登录查看情绪日志</text>
      <button class="login-btn" @click="gotoLogin">登录</button>
    </view>

    <!-- 内容区域 -->
    <view class="content-area">
      <!-- 加载中状态 -->
      <view class="loading-state" v-if="isLoading && allLogs.length === 0">
        <uni-loading-icon type="circle" size="24"></uni-loading-icon>
        <text class="loading-text">加载日志中...</text>
      </view>

      <!-- 错误状态 -->
      <view class="error-state" v-if="isError">
        <text class="error-text">日志加载失败</text>
        <button class="retry-btn" @click="loadEmotionLogs(true)">重新加载</button>
      </view>

      <!-- 日志列表 -->
      <view class="journal-list" v-else>
        <view 
          class="journal-item" 
          v-for="(item, index) in allLogs" 
          :key="index"
          @click="gotoDetail(item.id)"
        >
          <!-- 左侧情绪值 -->
          <view class="emotion-value" :style="{ backgroundColor: getEmotionColor(item.emotionvalue) }">
            <text>{{ item.emotionvalue }}</text>
          </view>
          
          <!-- 中间内容 -->
          <view class="journal-content">
            <view class="journal-title">
              <text>{{ item.cratetime ? formatDate(item.cratetime) : '未知日期' }}</text>
            </view>
            <view class="journal-summary">
              <text>{{ item.conclusion || '无日志内容' }}</text>
            </view>
            <view class="journal-tags">
              <view class="tag" v-for="(tag, idx) in item.tags || []" :key="idx">
                <text>{{ tag }}</text>
              </view>
            </view>
          </view>
          
          <!-- 右侧箭头 -->
          <view class="nav-arrow">
            <text>→</text>
          </view>
        </view>

        <!-- 加载更多提示 -->
        <view class="load-more" v-if="hasMore && !isLoading">
          <text>上拉加载更多</text>
        </view>

        <!-- 没有更多数据 -->
        <view class="no-more-data" v-else-if="!hasMore && allLogs.length > 0">
          <text>已经到底了</text>
        </view>
      </view>
    </view>

    <!-- 底部添加按钮 -->
    <view class="add-btn-container">
      <button class="add-btn" @click="gotoAddJournal">+</button>
    </view>
  </view>
   <GlobalPet />
</template>

<script>
export default {
  data() {
    return {
      apiBaseUrl: 'http://localhost:8080', // 后端API基础地址
      filterType: 'all',
      searchKeyword: '',
      allLogs: [],
      isLoading: true,
      isError: false,
      currentPage: 0,
      pageSize: 10,
      hasMore: true,
      searchTimer: null,
      isDebugMode: true,
      currentUserId: null,
      showLoginTip: false
    };
  },
  onShow() {
    console.log('[DEBUG] 页面加载开始');
    const userInfo = uni.getStorageSync('userInfo');
    this.currentUserId = userInfo && userInfo.id ? userInfo.id : null;
    console.log(`[用户状态] 当前用户ID: ${this.currentUserId || '未登录'}`);
    
    if (!this.currentUserId) {
      this.showLoginTip = true;
      this.allLogs = [];
      this.isLoading = false;
      return;
    }
    
    this.showLoginTip = false;
    this.loadEmotionLogs(true);
  },
  methods: {
    loadEmotionLogs(isReset = false) {
      console.log(`[DEBUG] 开始加载日志，是否重置: ${isReset}`);
      
      if (isReset) {
        this.currentPage = 0;
        this.allLogs = [];
        this.hasMore = true;
      }

      if (!this.hasMore && !isReset) {
        console.log('[DEBUG] 没有更多数据，终止加载');
        return;
      }

      this.isLoading = true;
      this.isError = false;

      const userId = this.currentUserId;
      if (!userId) {
        this.isLoading = false;
        this.showLoginTip = true;
        return;
      }

      // 拼接完整的后端接口URL
      const requestUrl = `${this.apiBaseUrl}/emotion-log/user/${userId}/list`;
      console.log(`[DEBUG] 请求完整URL: ${requestUrl}`);

      uni.request({
        url: requestUrl,
        method: 'GET',
        data: {
          page: this.currentPage,
          size: this.pageSize,
          filterType: this.filterType,
          keyword: this.searchKeyword
        },
        header: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${uni.getStorageSync('token')}`
        },
        success: (res) => {
          console.log('[DEBUG] 响应状态码:', res.statusCode);
          console.log('[DEBUG] 响应头:', res.header);
          console.log('[DEBUG] 响应数据:', res.data);

          // 检查响应是否为JSON
          const contentType = res.header['Content-Type'] || res.header['content-type'];
          if (contentType && contentType.includes('application/json')) {
            try {
              let newLogs = [];
              if (res.data && res.data.content) { // 后端分页对象格式
                newLogs = res.data.content.map(log => ({
                  id: log.id || '',
                  cratetime: log.cratetime || '',
                  emotionvalue: log.emotionvalue || 0,
                  conclusion: log.conclusion || '',
                  tags: log.tags || []
                }));
                this.hasMore = this.currentPage + 1 < res.data.totalPages;
              } else if (Array.isArray(res.data)) { // 数组格式
                newLogs = res.data.map(log => ({
                  id: log.id || '',
                  cratetime: log.cratetime || '',
                  emotionvalue: log.emotionvalue || 0,
                  conclusion: log.conclusion || '',
                  tags: log.tags || []
                }));
                this.hasMore = newLogs.length >= this.pageSize;
              } else {
                throw new Error('后端返回格式不符合预期');
              }

              this.allLogs = isReset ? newLogs : [...this.allLogs, ...newLogs];
              this.currentPage++;
              this.isError = false;
            } catch (e) {
              console.error('[ERROR] 解析JSON数据失败:', e);
              this.isError = true;
            }
          } else {
            console.error('[ERROR] 后端返回非JSON数据，可能请求地址错误');
            this.isError = true;
          }
        },
        fail: (err) => {
          console.error('[ERROR] 请求失败（网络问题或地址错误）:', err);
          this.isError = true;
        },
        complete: () => {
          this.isLoading = false;
          console.log('[DEBUG] 请求完成');
        }
      });
    },
    changeFilter(type) {
      if (this.filterType === type) return;
      this.filterType = type;
      this.loadEmotionLogs(true);
    },
    handleSearchInput(e) {
      this.searchKeyword = e.detail.value;
      
      // 防抖处理
      clearTimeout(this.searchTimer);
      this.searchTimer = setTimeout(() => {
        this.loadEmotionLogs(true);
      }, 300);
    },
    gotoDetail(id) {
      uni.navigateTo({
        url: `/pages/moodJournal/journal/Jdetail?id=${id}`
      });
    },
    gotoAddJournal() {
      if (!this.currentUserId) {
        uni.showModal({
          title: '提示',
          content: '请先登录再记录情绪日志',
          confirmText: '去登录',
          success: (res) => {
            if (res.confirm) {
              this.gotoLogin();
            }
          }
        });
        return;
      }
      
      uni.navigateTo({
        url: '/pages/moodJournal/journal/Jedit'
      });
    },
    gotoLogin() {
      uni.navigateTo({
        url: '/pages/user/Login'
      });
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      
      // 处理 YYYY-MM-DD HH:mm:ss 格式
      const date = new Date(dateStr);
      if (isNaN(date.getTime())) return dateStr;
      
      const year = date.getFullYear();
      const month = date.getMonth() + 1;
      const day = date.getDate();
      
      return `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
    },
    getEmotionColor(value) {
      if (value >= 80) return '#52c41a'; // 绿色（愉悦）
      if (value >= 60) return '#faad14'; // 黄色（平静）
      if (value >= 40) return '#fa8c16'; // 橙色（低落）
      return '#e64340'; // 红色（焦虑）
    }
  },
  // 下拉刷新
  onPullDownRefresh() {
    if (!this.currentUserId) {
      uni.stopPullDownRefresh();
      return;
    }
    
    this.loadEmotionLogs(true);
    uni.stopPullDownRefresh();
  },
  // 上拉加载更多
  onReachBottom() {
    if (!this.currentUserId || this.isLoading || !this.hasMore) return;
    this.loadEmotionLogs(false);
  }
};
</script>

<style scoped>
.journal-list-page {
  background-color: #f9f9f9;
  min-height: 100vh;
}

.search-bar {
  background-color: #fff;
  padding: 20rpx 30rpx;
  border-bottom: 1px solid #f1f1f1;
}

.search-input-container {
  background-color: #f5f5f5;
  border-radius: 40rpx;
  padding: 15rpx 25rpx;
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.search-icon {
  font-size: 28rpx;
  color: #999;
  margin-right: 15rpx;
}

.search-input {
  font-size: 28rpx;
  flex: 1;
}

.search-placeholder {
  font-size: 28rpx;
  color: #999;
}

.filter-tabs {
  display: flex;
  justify-content: space-around;
}

.filter-tab {
  font-size: 32rpx;
  color: #666;
  padding: 10rpx 20rpx;
  border-radius: 40rpx;
}

.filter-tab.active {
  background-color: #1890ff;
  color: #fff;
}

.login-tip {
  padding: 50rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.tip-text {
  font-size: 32rpx;
  color: #666;
  margin-bottom: 30rpx;
}

.login-btn {
  width: 200rpx;
  height: 70rpx;
  background-color: #1890ff;
  color: #fff;
  border-radius: 35rpx;
  font-size: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.content-area {
  padding: 20rpx 0;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
}

.loading-text {
  margin-top: 20rpx;
  font-size: 32rpx;
  color: #999;
}

.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
}

.error-text {
  font-size: 32rpx;
  color: #999;
  margin-bottom: 30rpx;
}

.retry-btn {
  width: 200rpx;
  height: 70rpx;
  background-color: #1890ff;
  color: #fff;
  border-radius: 35rpx;
  font-size: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.journal-list {
  padding: 0 30rpx;
}

.journal-item {
  background-color: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  display: flex;
  align-items: center;
}

.emotion-value {
  width: 80rpx;
  height: 80rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  margin-right: 20rpx;
}

.journal-content {
  flex: 1;
  margin-right: 20rpx;
}

.journal-title {
  font-size: 32rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.journal-summary {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 15rpx;
  line-height: 1.5;
  max-height: 84rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.journal-tags {
  display: flex;
  flex-wrap: wrap;
}

.journal-tags .tag {
  background-color: #f5f5f5;
  border-radius: 20rpx;
  padding: 5rpx 15rpx;
  font-size: 24rpx;
  color: #666;
  margin-right: 15rpx;
  margin-bottom: 10rpx;
}

.nav-arrow {
  color: #999;
  font-size: 32rpx;
}

.load-more, .no-more-data {
  text-align: center;
  padding: 30rpx 0;
  font-size: 28rpx;
  color: #999;
}

.add-btn-container {
  position: fixed;
  bottom: 60rpx;
  right: 60rpx;
}

.add-btn {
  width: 100rpx;
  height: 100rpx;
  background-color: #1890ff;
  color: #fff;
  border-radius: 50rpx;
  font-size: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 5rpx 20rpx rgba(24, 144, 255, 0.4);
}
</style>