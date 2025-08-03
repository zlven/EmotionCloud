<template>
  <view class="jdetail-page">
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <view class="nav-back" @click="navigateBack">←</view>
      <view class="nav-title">每周情绪日志</view>
      <view class="nav-actions">
        <text class="action-icon" @click="triggerShare">📤</text>
        <text class="action-icon" @click="showMoreMenu">⋮</text>
      </view>
    </view>

    <!-- 加载中状态 -->
    <view class="loading-state" v-if="isLoading">
      <view class="loading-icon">
        <text class="uni-loading"></text>
      </view>
      <text class="loading-text">加载日志详情中...</text>
    </view>

    <!-- 错误状态 -->
    <view class="error-state" v-if="isError">
      <text class="error-text">日志加载失败</text>
      <button class="retry-btn" @click="fetchLogDetail">重新加载</button>
    </view>

    <!-- 有日志时显示内容 -->
    <view v-else-if="logData.id">
      <!-- 情绪可视化区域 -->
      <view class="emotion-chart-area">
        <view class="chart-header">
          <text class="chart-title">本周情绪波动</text>
          <text class="chart-subtitle">{{ formatDateRange() }}</text>
        </view>
        
        <!-- AIGC生成的情绪曲线 -->
        <view class="chart-container">
          <image 
            :src="logData.aigcchart" 
            mode="widthFix" 
            class="emotion-chart"
            v-if="logData.aigcchart"
          ></image>
          <view class="chart-placeholder" v-else>
            <text>暂无情绪图表</text>
          </view>
        </view>
        
        <!-- 情绪值标签 -->
        <view class="emotion-value-tag">
          <view class="value-badge" :style="{ backgroundColor: getEmotionColor(logData.emotionvalue) }">
            <text class="value-text">{{ logData.emotionvalue }}</text>
            <text class="value-label">今日情绪值</text>
          </view>
        </view>
      </view>

      <!-- 手账内容区 -->
      <view class="journal-content">
        <!-- 情绪标签组 -->
        <view class="emotion-tags">
          <view class="tag" v-for="(tag, index) in logTags" :key="index">{{ tag }}</view>
        </view>

        <!-- 日志正文 -->
        <view class="journal-text">
          <text>{{ logData.conclusion || '暂无情绪记录...' }}</text>
        </view>

        <!-- 富媒体附件区 -->
        <view class="media-attachments">
          <view class="media-title" v-if="attachments.length > 0">
            <text>添加的素材</text>
          </view>
          <view class="media-grid">
            <view class="media-item" v-for="(item, index) in attachments" :key="index">
              <image 
                :src="item.url" 
                mode="aspectFill" 
                class="media-thumb"
                v-if="item.type === 'image'"
              ></image>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 无日志时提示 -->
    <view v-else class="empty-state">
      <image src="/static/empty-detail.png" mode="widthFix" class="empty-img"></image>
      <text class="empty-text">暂无日志详情</text>
      <button class="back-btn" @click="navigateBack">返回列表</button>
    </view>

    <!-- 分享弹窗 -->
    <view class="share-popup" v-if="showSharePopup">
      <view class="popup-content">
        <view class="popup-title">分享至</view>
        <view class="share-options">
          <view class="share-option" @click="shareToSquare">
            <text class="option-icon">🏛️</text>
            <text class="option-text">情绪广场（匿名）</text>
          </view>
          <view class="share-option" @click="shareToFriend">
            <text class="option-icon">👫</text>
            <text class="option-text">好友</text>
          </view>
          <view class="share-option" @click="saveAsImage">
            <text class="option-icon">🖼️</text>
            <text class="option-text">保存为图片</text>
          </view>
        </view>
        <button class="popup-close" @click="showSharePopup = false">取消</button>
      </view>
    </view>
  </view>
   <GlobalPet />
</template>

<script>
export default {
  data() {
    return {
      logId: '', // 从路由参数获取
      logData: { id: '' }, // 初始化带id，避免undefined判断
      logTags: [], // 情绪标签
      attachments: [], // 富媒体附件
      showSharePopup: false,
      isLoading: true, // 加载状态
      isError: false, // 错误状态
      apiBaseUrl: 'http://localhost:8080' // 后端API基础地址（与列表页一致）
    };
  },
  onLoad(options) {
    this.logId = options.id;
    this.fetchLogDetail();
  },
  methods: {
    // 调用后端接口获取日志详情
    fetchLogDetail() {
      this.isLoading = true;
      this.isError = false;
      
      // 拼接完整的后端接口URL
      const requestUrl = `${this.apiBaseUrl}/emotion-log/detail/${this.logId}`;
      console.log('[详情页] 请求日志详情URL:', requestUrl);
      
      uni.request({
        url: requestUrl,
        method: 'GET',
        header: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${uni.getStorageSync('token')}` // 携带认证token
        },
        success: (res) => {
          console.log('[详情页] 响应状态码:', res.statusCode);
          console.log('[详情页] 响应数据:', res.data);
          console.log('[详情页] 响应头:', res.header);
          
          this.isLoading = false;
          
          // 检查响应是否为JSON格式
          const contentType = res.header['Content-Type'] || res.header['content-type'];
          if (res.statusCode === 200 && contentType && contentType.includes('application/json')) {
            if (res.data) {
              this.logData = res.data;
              // 处理标签
              this.logTags = res.data.tags || [];
              if (!this.logTags.length) {
                this.generateTags(res.data.emotionvalue);
              }
              // 处理附件（根据后端实际字段调整）
              this.attachments = res.data.attachments || [];
            } else {
              this.logData = { id: '' }; // 无数据
            }
          } else {
            // 响应不是JSON（如HTML页面），视为错误
            console.error('[详情页] 后端返回非JSON数据，无法解析');
            this.isError = true;
            this.logData = { id: '' };
          }
        },
        fail: (err) => {
          console.error('[详情页] 请求失败:', err);
          this.isLoading = false;
          this.isError = true;
          this.logData = { id: '' };
        }
      });
    },
    // 生成情绪标签（若后端未返回）
    generateTags(emotionValue) {
      if (emotionValue >= 80) {
        this.logTags = ['愉悦', '轻松', '满足'];
      } else if (emotionValue >= 60) {
        this.logTags = ['平静', '稳定', '日常'];
      } else if (emotionValue >= 40) {
        this.logTags = ['低落', '疲惫', '需要休息'];
      } else {
        this.logTags = ['焦虑', '压力大', '需要倾诉'];
      }
    },
    // 分享至情绪广场
    shareToSquare() {
      this.showSharePopup = false;
      uni.showModal({
        title: '匿名分享',
        content: '分享后将以匿名形式展示在情绪广场，是否确认？',
        confirmText: '确认分享',
        success: (res) => {
          if (res.confirm) {
            // 调用分享接口
            uni.request({
              url: `${this.apiBaseUrl}/emotion-log/share/${this.logId}`,
              method: 'POST',
              header: {
                'Authorization': `Bearer ${uni.getStorageSync('token')}`
              },
              success: () => {
                uni.showToast({ title: '分享成功', icon: 'success' });
              }
            });
          }
        }
      });
    },
    // 格式化日期范围
    formatDateRange() {
      const date = this.parseDate(this.logData.cratetime);
      if (!date) return '';
      
      const start = new Date(date);
      start.setDate(date.getDate() - 6);
      return `${start.getMonth() + 1}月${start.getDate()}日 - ${date.getMonth() + 1}月${date.getDate()}日`;
    },
    // 根据情绪值获取颜色
    getEmotionColor(value) {
      if (value >= 80) return '#52c41a'; // 绿色（愉悦）
      if (value >= 60) return '#faad14'; // 黄色（平静）
      if (value >= 40) return '#fa8c16'; // 橙色（低落）
      return '#e64340'; // 红色（焦虑）
    },
    // 导航返回
    navigateBack() {
      uni.navigateBack();
    },
    // 显示更多菜单（删除等操作）
    showMoreMenu() {
      uni.showActionSheet({
        itemList: ['删除日志', '举报问题'],
        success: (res) => {
          if (res.tapIndex === 0) {
            // 删除日志逻辑
            uni.showModal({
              title: '确认删除',
              content: '此操作将永久删除该日志，是否确认？',
              confirmText: '删除',
              success: (res) => {
                if (res.confirm) {
                  const deleteUrl = `${this.apiBaseUrl}/emotion-log/delete/${this.logId}`;
                  console.log('[详情页] 删除日志URL:', deleteUrl);
                  
                  uni.request({
                    url: deleteUrl,
                    method: 'DELETE',
                    header: {
                      'Authorization': `Bearer ${uni.getStorageSync('token')}`
                    },
                    success: (res) => {
                      if (res.statusCode === 200) {
                        uni.navigateBack();
                        uni.showToast({ title: '删除成功', icon: 'success' });
                      } else {
                        uni.showToast({ title: '删除失败', icon: 'none' });
                      }
                    },
                    fail: () => {
                      uni.showToast({ title: '删除请求失败', icon: 'none' });
                    }
                  });
                }
              }
            });
          }
        }
      });
    },
    // 保存为图片
    saveAsImage() {
      this.showSharePopup = false;
      uni.showToast({ title: '保存图片功能开发中', icon: 'none' });
    },
    // 分享给好友
    shareToFriend() {
      this.showSharePopup = false;
      uni.showToast({ title: '分享给好友功能开发中', icon: 'none' });
    },
    // 触发分享弹窗
    triggerShare() {
      this.showSharePopup = true;
    },
    // 安全解析日期
    parseDate(timeStr) {
      if (!timeStr) return null;
      
      // 兼容带时区的ISO格式和自定义格式
      let date;
      if (timeStr.includes('T')) {
        date = new Date(timeStr);
      } else {
        // 处理 YYYY-MM-DD HH:mm:ss 格式
        const [datePart, timePart] = timeStr.split(' ');
        if (!datePart) return null;
        
        const [year, month, day] = datePart.split('-');
        if (!year || !month || !day) return null;
        
        const time = timePart ? timePart.split(':') : [0, 0, 0];
        date = new Date(year, month - 1, day, time[0], time[1], time[2]);
      }
      
      return isNaN(date.getTime()) ? null : date;
    }
  }
};
</script>

<style scoped>
.jdetail-page {
  background-color: #f9f9f9;
  min-height: 100vh;
}

.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100rpx;
  background-color: #fff;
  border-bottom: 1px solid #f1f1f1;
  padding: 0 30rpx;
}

.nav-back {
  font-size: 36rpx;
  color: #333;
}

.nav-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.nav-actions {
  display: flex;
}

.action-icon {
  font-size: 32rpx;
  color: #666;
  margin-left: 30rpx;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
}

.loading-icon {
  width: 48rpx;
  height: 48rpx;
  position: relative;
}

.uni-loading {
  display: inline-block;
  width: 48rpx;
  height: 48rpx;
  border: 4rpx solid #1890ff;
  border-radius: 50%;
  border-right-color: transparent;
  animation: uni-rotate 1s linear infinite;
}

@keyframes uni-rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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

.emotion-chart-area {
  background-color: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.chart-header {
  margin-bottom: 30rpx;
}

.chart-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.chart-subtitle {
  font-size: 28rpx;
  color: #999;
}

.chart-container {
  width: 100%;
  height: 400rpx;
  background-color: #f9f9f9;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30rpx;
}

.emotion-chart {
  width: 100%;
  height: 100%;
  border-radius: 20rpx;
}

.chart-placeholder {
  font-size: 32rpx;
  color: #999;
}

.emotion-value-tag {
  display: flex;
  justify-content: center;
}

.value-badge {
  padding: 20rpx 40rpx;
  border-radius: 40rpx;
  text-align: center;
}

.value-text {
  font-size: 48rpx;
  font-weight: bold;
  color: #fff;
}

.value-label {
  font-size: 28rpx;
  color: #fff;
  opacity: 0.8;
}

.journal-content {
  background-color: #fff;
  padding: 30rpx;
}

.emotion-tags {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 30rpx;
}

.emotion-tags .tag {
  padding: 10rpx 20rpx;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
  background-color: #f5f5f5;
  border-radius: 25rpx;
  font-size: 28rpx;
  color: #666;
}

.journal-text {
  margin-bottom: 30rpx;
}

.journal-text text {
  font-size: 32rpx;
  color: #333;
  line-height: 1.6;
}

.media-attachments {
  margin-bottom: 30rpx;
}

.media-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.media-grid {
  display: flex;
  flex-wrap: wrap;
  margin: -10rpx;
}

.media-item {
  width: calc(33.333% - 20rpx);
  height: 200rpx;
  margin: 10rpx;
  border-radius: 12rpx;
  overflow: hidden;
  background-color: #f5f5f5;
}

.media-thumb {
  width: 100%;
  height: 100%;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 200rpx;
}

.empty-img {
  width: 300rpx;
  height: 300rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 32rpx;
  color: #999;
}

.back-btn {
  margin-top: 30rpx;
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

.share-popup {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  top: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: flex-end;
  flex-direction: column;
  z-index: 999;
}

.popup-content {
  background-color: #fff;
  border-radius: 30rpx 30rpx 0 0;
  padding: 40rpx 30rpx;
}

.popup-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 40rpx;
}

.share-options {
  display: flex;
  justify-content: space-around;
  margin-bottom: 60rpx;
}

.share-option {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.option-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50rpx;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  margin-bottom: 20rpx;
}

.option-text {
  font-size: 28rpx;
  color: #666;
}

.popup-close {
  width: 100%;
  height: 90rpx;
  background-color: #f5f5f5;
  border-radius: 45rpx;
  font-size: 32rpx;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>