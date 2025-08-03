<template>
  <view class="page">
    <!-- 浮动星星动画 -->
    <div class="floating-stars">
      <span 
        v-for="(star, index) in stars" 
        :key="index"
        class="star"
        :style="{
          left: star.x + 'vw',
          top: star.y + 'vh',
          fontSize: star.size + 'px',
          opacity: star.opacity,
          animation: `float ${star.duration}s linear infinite`,
          animationDelay: star.delay + 's',
        }"
      >
        {{ star.symbol }}
      </span>
    </div>
    
    <!-- 加载状态 -->
    <view v-if="loading || !isReady" class="loading-container">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>
    
    <!-- 瀑布流内容区域 -->
    <scroll-view 
      v-else 
      class="waterfall" 
      scroll-y 
      @scrolltolower="loadMore"
    >
      <view class="container">
        <!-- 左列 -->
        <view class="column left-column">
          <view 
            v-for="(item, index) in leftItems" 
            :key="item.id || index"
            class="post-item"
            @click="goToNoteDetail(item.id)"
          >
            <view class="post-image" :style="{ height: item.calculatedHeight + 'px' }">
              <image 
                v-for="(img, imgIndex) in item.images" 
                :key="imgIndex"
                :src="img" 
                mode="aspectFill" 
                class="item-image"
                @load="handleImageLoad(item)"
              />
            </view>
            <view class="post-content">
              <text class="post-title" v-if="item.title">{{ item.title }}</text>
              <text class="post-contents" v-if="item.contents">{{ item.contents }}</text>
            </view>
            <view class="interaction">
              <view class="interaction-item" @click.stop="likeItem(item)">
                <text class="icon">⭐</text>
                <text>{{ item.likes || 0 }}</text>
              </view>
              <view class="interaction-item" @click.stop="goToNoteDetail(item.id)">
                <text class="icon">💬</text>
                <text>{{ item.commentCount || 0 }}</text>
              </view>
              <view class="interaction-item" @click.stop="shareItem(item)">
                <text class="icon">📤</text>
                <text>{{ item.shares || 0 }}</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 右列 -->
        <view class="column right-column">
          <view 
            v-for="(item, index) in rightItems" 
            :key="item.id || index"
            class="post-item"
            @click="goToNoteDetail(item.id)"
          >
            <view class="post-image" :style="{ height: item.calculatedHeight + 'px' }">
              <image 
                v-for="(img, imgIndex) in item.images" 
                :key="imgIndex"
                :src="img" 
                mode="aspectFill" 
                class="item-image"
                @load="handleImageLoad(item)"
              />
            </view>
            <view class="post-content">
              <text class="post-title" v-if="item.title">{{ item.title }}</text>
              <text class="post-contents" v-if="item.contents">{{ item.contents }}</text>
            </view>
            <view class="interaction">
              <view class="interaction-item" @click.stop="likeItem(item)">
                <text class="icon">⭐</text>
                <text>{{ item.likes || 0 }}</text>
              </view>
              <view class="interaction-item" @click.stop="goToNoteDetail(item.id)">
                <text class="icon">💬</text>
                <text>{{ item.commentCount || 0 }}</text>
              </view>
              <view class="interaction-item" @click.stop="shareItem(item)">
                <text class="icon">📤</text>
                <text>{{ item.shares || 0 }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
    
    <!-- 加载更多提示 -->
    <view v-if="hasMore && !loading && isReady" class="load-more">
      <text>上拉加载更多</text>
    </view>
    
    <!-- 发布按钮 -->
    <view class="floating-button" @click="goToPostPage">
      <text class="button-icon">＋</text>
    </view>
  </view>
  
  <GlobalPet />
</template>

<script>
export default {
  data() {
    return {
      items: [],
      leftItems: [],
      rightItems: [],
      columnWidth: 0,
      totalHeightLeft: 0,
      totalHeightRight: 0,
      page: 0,
      pageSize: 10,
      hasMore: true,
      loading: false,
      defaultImage: 'https://picsum.photos/300/300',
      isReady: false,
      imageCount: 0,
      loadedImages: 0,
      serverUrl: 'http://localhost:8080',
      stars: Array(15).fill().map(() => ({
        symbol: ['⭐', '🌟', '✨', '★', '☁'][Math.floor(Math.random() * 5)],
        x: Math.random() * 100,
        y: Math.random() * 100,
        size: Math.random() * 10 + 10,
        opacity: Math.random() * 0.5 + 0.3,
        duration: Math.random() * 10 + 5,
        delay: Math.random() * 5,
      })),
      colors: {
        primary: '#fff8e9',       // 主色调：米黄色
        secondary: '#e6d6c2',     // 次要色调：浅米黄色
        accent: '#8b4513',        // 强调色：棕褐色
        darkAccent: '#5a3921',     // 深强调色：深棕褐色
        highlight: '#ffd700',     // 高亮色：金色
      }
    };
  },
  mounted() {
    this.getDeviceWidth();
    this.loadMore(); // 初始化加载
  },
  onShow() {
    // 从发帖页返回时触发，刷新数据
    this.refreshData();
  },
  methods: {
    // 刷新数据（核心：解决发帖后不更新问题）
    refreshData() {
      // 重置数据状态
      this.page = 0;
      this.items = [];
      this.leftItems = [];
      this.rightItems = [];
      this.hasMore = true;
      this.isReady = false;
      
      // 重新加载数据
      if (!this.loading) {
        this.fetchData();
      }
    },
    getDeviceWidth() {
      uni.getSystemInfo({
        success: (res) => {
          this.columnWidth = (res.windowWidth - 30) / 2;
        }
      });
    },
    
    fetchData() {
      if (this.loading || !this.hasMore) return;
      
      this.loading = true;
      console.log('请求数据，页码:', this.page);
      
      uni.request({
        url: `${this.serverUrl}/social/getposts`,
        method: 'GET',
        data: {
          page: this.page,
          size: this.pageSize,
          sort: 'createTime,desc' // 按创建时间倒序，确保新帖在前面
        },
        success: (res) => {
          if (res.statusCode === 200 && res.data) {
            const posts = res.data.content || [];
            this.hasMore = res.data.totalPages > this.page + 1;
            
            const newItems = posts.map(post => ({
              ...post,
              id: post.postid || post.id,
              images: [],
              calculatedHeight: 150
            })).filter(post => post.id);
            
            if (newItems.length > 0) {
              this.items = [...this.items, ...newItems];
              this.imageCount = newItems.length;
              this.loadedImages = 0;
              
              newItems.forEach(post => {
                this.getPostImages(post.id).then(images => {
                  post.images = images.length > 0 ? images : [this.defaultImage];
                  if (images.length > 0) {
                    const ratio = 300 / this.columnWidth;
                    post.calculatedHeight = Math.floor(400 / ratio);
                  }
                  this.loadedImages++;
                  this.checkImageLoading();
                });
              });
            }
          }
        },
        complete: () => {
          this.loading = false;
          this.page++;
        }
      });
    },
    
    getPostImages(postId) {
      return new Promise(resolve => {
        uni.request({
          url: `${this.serverUrl}/social/posts/${postId}/images/resources`,
          success: (res) => {
            console.log("获取的图片资源：", res);
            if (res.statusCode === 200 && Array.isArray(res.data)) {
              resolve(res.data);
            } else {
              console.error('图片接口返回异常数据', res);
              resolve([]);
            }
          },
          fail: (err) => {
            console.error('获取图片资源失败', err);
            resolve([]);
          }
        });
      });
    },
    
    handleImageLoad(item) {
      this.loadedImages++;
      this.checkImageLoading();
    },
    
    checkImageLoading() {
      if (this.loadedImages >= this.imageCount) {
        this.distributeItems();
        this.isReady = true;
      }
    },
    
    distributeItems() {
      this.leftItems = [];
      this.rightItems = [];
      this.totalHeightLeft = 0;
      this.totalHeightRight = 0;
      
      this.items.forEach(item => {
        if (this.totalHeightLeft <= this.totalHeightRight) {
          this.leftItems.push(item);
          this.totalHeightLeft += item.calculatedHeight;
        } else {
          this.rightItems.push(item);
          this.totalHeightRight += item.calculatedHeight;
        }
      });
    },
    
    loadMore() {
      if (!this.loading) {
        this.fetchData();
      }
    },
    
    goToNoteDetail(noteId) {
      uni.navigateTo({
        url: `/pages/empathySocial/note?id=${noteId}`
      });
    },
    
    goToPostPage() {
      uni.navigateTo({
        url: '/pages/empathySocial/post'
      });
    },
    
    likeItem(item) {
      uni.request({
        url: `${this.serverUrl}/social/posts/${item.id}/like`,
        method: 'POST',
        success: () => {
          item.likes = (item.likes || 0) + 1;
          uni.showToast({
            title: '点赞成功',
            icon: 'success'
          });
        },
        fail: () => {
          uni.showToast({
            title: '点赞失败',
            icon: 'none'
          });
        }
      });
    },
    
    shareItem(item) {
      uni.share({
        title: item.title || '分享帖子',
        content: item.contents || '',
        imageUrl: item.images && item.images.length > 0 ? item.images[0] : this.defaultImage,
        success: () => {
          uni.showToast({
            title: '分享成功',
            icon: 'success'
          });
          item.shares = (item.shares || 0) + 1;
        },
        fail: (err) => {
          console.log('分享失败:', err);
          uni.showToast({
            title: '分享失败',
            icon: 'none'
          });
        }
      });
    }
  }
};
</script>

<style scoped>
  /* 基础颜色定义 */
  :root {
    --primary: #fff8e9;       /* 主色调：米黄色 */
    --secondary: #e6d6c2;     /* 次要色调：浅米黄色 */
    --accent: #8b4513;        /* 强调色：棕褐色 */
    --dark-accent: #5a3921;   /* 深强调色：深棕褐色 */
    --highlight: #ffd700;     /* 高亮色：金色 */
    --gradient-start: #fff8e9;
    --gradient-middle: #ffe4b5;
    --gradient-end: #f0d9b5;
  }
/* 黄色/粉色背景 */
.page {
  min-height: 100vh;
  overflow: hidden;
  background: linear-gradient(to right, #ffc1cd, #FFE4B5, #CCE5FF);
  background-size: 200% 200%;
  animation: gradient 8s ease infinite;
  position: relative;
  z-index: 1;
}

/* 优化body样式 */
body {
  margin: 0;
  height: 100vh;
  background: inherit;
}

@keyframes gradient {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 星星动画（淡金色） */
.floating-stars {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: 9;
}

.star {
  position: absolute;
  color: rgba(255, 215, 0, 0.7); /* 淡金色半透明 */
  user-select: none;
}

@keyframes float {
  0% { transform: translateY(0) rotate(0deg); opacity: 0.4; }
  50% { opacity: 0.9; }
  100% { transform: translateY(-100vh) rotate(360deg); opacity: 0.4; }
}

img {
  max-width: 100%;
  height: auto;
  display: block;
}

/* 加载状态样式 */
.loading-container {
  height: calc(100vh - 100rpx);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: transparent;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 6rpx solid rgba(255, 215, 0, 0.3); /* 金色边框 */
  border-left-color: var(--accent); /* 棕褐色 */
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-text {
  margin-top: 20rpx;
  font-size: 28rpx;
  color: var(--dark-accent); /* 深棕褐色 */
  font-weight: 500;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 瀑布流内容区样式 */
.waterfall {
  height: calc(100vh - 160rpx);
  overflow-y: auto;
}

.container {
  display: flex;
  padding: 15rpx;
}

.column {
  flex: 1;
  padding: 0 7rpx;
}

.post-item {
  margin-bottom: 15rpx;
  background: rgba(255, 248, 233, 0.9); /* 浅黄色透明背景 */
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 20rpx rgba(90, 57, 33, 0.1); /* 棕褐色阴影 */
  transition: all 0.2s ease;
}

.post-item:active {
  transform: scale(0.98);
  box-shadow: 0 4rpx 10rpx rgba(85, 0, 0, 0.2);
}

.post-image {
  width: 100%;
  overflow: hidden;
  position: relative;
}

.item-image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s ease;
}

.post-item:hover .item-image {
  transform: scale(1.03);
}

.post-content {
  padding: 20rpx;
  background: #FFE4B5; /* 匹配渐变中段 */
}
.post-title {
  font-size: 28rpx;
  font-weight: 600;
  margin-bottom: 10rpx;
  color: var(--dark-accent); /* 深棕褐色 */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.post-contents {
  font-size: 24rpx;
  color: #5a3921; /* 暖棕色 */
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.interaction {
  display: flex;
  justify-content: space-around;
  padding: 15rpx 20rpx;
  background: #FFE4B5;
  border-top: 1rpx solid #550000; /* 深红色边框 */
}

.interaction-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.icon {
  font-size: 32rpx;
  margin-bottom: 5rpx;
  color: #550000; /* 深红色 */
}

.interaction-item text {
  font-size: 22rpx;
  color: #5a3921; /* 暖棕色 */
}

/* 加载更多提示样式 */
.load-more {
  padding: 25rpx;
  text-align: center;
  color: #550000; /* 深红色 */
  font-size: 24rpx;
}

/* 发布按钮样式 */
.floating-button {
  position: fixed;
  right: 30rpx;
  bottom: 120rpx;
  width: 90rpx;
  height: 90rpx;
  background: linear-gradient(to right, #550000, #8B4513); /* 深红色到棕色 */
  border-radius: 45rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 48rpx;
  box-shadow: 0 10rpx 25rpx rgba(85, 0, 0, 0.4); /* 深红色阴影 */
  z-index: 100;
  transition: transform 0.2s ease;
}

.floating-button:active {
  transform: scale(0.95);
  box-shadow: 0 6rpx 16rpx rgba(85, 0, 0, 0.5);
}
</style>