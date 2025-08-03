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
    
    <!-- 返回按钮 -->
    <view class="back-button" @click="goBack">
      <text class="icon">←</text>
      <text>返回</text>
    </view>
    
    <!-- 内容区域 - 调整为flex布局 -->
    <scroll-view class="content-scroll" scroll-y>
      <!-- 贴文内容 -->
      <view class="post-card">
        <!-- 图片区域 -->
        <view class="post-image" :style="{ height: note.imageHeight + 'px' }" v-if="note.images.length">
          <image 
            v-for="(img, index) in note.images" 
            :key="index"
            :src="img" 
            mode="aspectFill" 
            class="item-image"
          />
        </view>
        
        <!-- 内容区域 -->
        <view class="post-content">
          <text class="contents-text">{{ note.contents || '暂无内容' }}</text>
        </view>
        
        <!-- 交互区域 -->
        <view class="interaction">
          <view class="interaction-item" @click.stop="likeNote">
            <text class="icon">⭐</text>
            <text>{{ note.likes || 0 }}</text>
          </view>
          <view class="interaction-item" @click.stop="focusComment">
            <text class="icon">💬</text>
            <text>{{ note.commentCount || 0 }}</text>
          </view>
          <view class="interaction-item" @click.stop="shareNote">
            <text class="icon">📤</text>
            <text>分享</text>
          </view>
        </view>
      </view>
      
      <!-- 评论区域 - 弹性填充剩余空间 -->
      <view class="comments-section">
        <view class="comments-header">
          <text class="section-title">评论</text>
          <text class="comment-count">{{ comments.length }}条</text>
        </view>
        
        <view class="comments-list">
          <view 
            v-for="(comment, index) in comments" 
            :key="index" 
            class="comment-item"
          >
            <text class="comment-text">{{ comment.content }}</text>
            <text class="comment-time">{{ formatTime(comment.createTime) }}</text>
          </view>
        </view>
      </view>
    </scroll-view>
    
    <!-- 评论输入框 -->
    <view class="input-section">
      <input 
        v-model="newComment" 
        placeholder="写下你的评论..." 
        class="comment-input"
        ref="commentInput"
      />
      <button class="submit-button" @click="submitComment">发送</button>
    </view>
  </view>
  
  <GlobalPet />
</template>

<script>
export default {
  data() {
    return {
      noteId: null,
      note: {
        id: null,
        title: '',
        contents: '',
        images: [],
        likes: 0,
        commentCount: 0,
        createTime: '',
        calculatedHeight: 150,
        imageHeight: 0
      },
      comments: [],
      newComment: '',
      loading: false,
      serverUrl: 'http://localhost:8080',
      defaultImage: 'https://picsum.photos/400/400',
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
	          secondary: '#e6d6c2',     // 次要背景：浅米黄
	          accent: '#d2b48c',        // 强调色：棕黄色
	          darkAccent: '#8b4513',     // 深强调色：棕褐色
	          highlight: '#ffd700',     // 高亮色：金色
	        }
    };
  },
  
  onLoad(options) {
    this.noteId = options.id;
    this.loadNoteData();
    this.loadComments();
  },
  methods: {
    loadNoteData() {
      this.loading = true;
      
      uni.request({
        url: `${this.serverUrl}/social/posts/${this.noteId}`,
        method: 'GET',
        success: (res) => {
          if (res.statusCode === 200 && res.data) {
            this.note = {
              ...res.data,
              id: res.data.postid || res.data.id,
              images: [],
              calculatedHeight: 150,
              imageHeight: 0
            };
            
            this.getPostImages(this.noteId).then(images => {
              this.note.images = images.length > 0 ? images : [this.defaultImage];
              
              if (images.length > 0) {
                uni.getSystemInfo({
                  success: (sysRes) => {
                    const screenHeight = sysRes.windowHeight;
                    this.note.imageHeight = screenHeight * 0.6; // 60% of screen height
                    const columnWidth = sysRes.windowWidth;
                    this.note.calculatedHeight = this.note.imageHeight; // Sync with imageHeight
                  }
                });
              }
            });
          }
        },
        complete: () => {
          this.loading = false;
        }
      });
    },
    
    getPostImages(postId) {
      return new Promise(resolve => {
        uni.request({
          url: `${this.serverUrl}/social/posts/${postId}/images/resources`,
          success: (res) => {
            if (res.statusCode === 200 && Array.isArray(res.data)) {
              resolve(res.data.map(img => 
                img.startsWith('http') ? img : `${this.serverUrl}${img.startsWith('/') ? '' : '/'}${img}`
              ));
            } else {
              resolve([]);
            }
          },
          fail: () => {
            resolve([]);
          }
        });
      });
    },
    
    loadComments() {
      uni.request({
        url: `${this.serverUrl}/social/posts/${this.noteId}/comments`,
        method: 'GET',
        success: (res) => {
          if (res.statusCode === 200 && Array.isArray(res.data)) {
            this.comments = res.data.map(comment => ({
              ...comment,
              author: comment.author || { 
                id: comment.authorId,
                name: '匿名用户'
              }
            }));
          }
        },
        fail: (err) => {
          console.error('加载评论失败:', err);
        }
      });
    },
    
    handleImageLoad(item) {
      // 图片加载处理（如有需要）
    },
    
    likeNote() {
      uni.request({
        url: `${this.serverUrl}/social/posts/${this.noteId}/like`,
        method: 'POST',
        success: () => {
          this.note.likes += 1;
          uni.showToast({ title: '点赞成功', icon: 'success' });
        }
      });
    },
    
    submitComment() {
      if (!this.newComment.trim()) return;
      const userInfo = uni.getStorageSync('userInfo');
      const userId = userInfo?.id;
      uni.request({
        url: `${this.serverUrl}/social/comments`,
        method: 'POST',
        data: {
          postId: this.noteId,
          content: this.newComment,
          authorId: userId,
          parentId: userId
        },
        success: () => {
          this.newComment = '';
          this.loadComments();
          this.note.commentCount += 1;
        }
      });
    },
    
    focusComment() {
      this.$refs.commentInput.focus();
    },
    
    shareNote() {
      uni.share({
        title: this.note.title,
        content: this.note.contents,
        imageUrl: this.note.images[0] || this.defaultImage,
        success: () => {
          uni.showToast({ title: '分享成功' });
        }
      });
    },
    
    formatTime(timestamp) {
      if (!timestamp) return '';
      const date = new Date(timestamp);
      return `${date.getFullYear()}-${date.getMonth()+1}-${date.getDate()}`;
    },
    
    goBack() {
      uni.navigateBack();
    }
  }
};
</script>

<style scoped>
/* 黄色渐变背景 */
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
  background: linear-gradient(to right, #FFB6C1, #FFE4B5, #CCE5FF);
  background-size: 300% 300%;
  animation: gradient 10s ease infinite;
}

@keyframes gradient {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 星星动画 */
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
  display: inline-block;
  color: rgba(255, 215, 0, 0.7); /* 金色半透明 */
  user-select: none;
}

@keyframes float {
  0% { transform: translateY(0) rotate(0deg); opacity: 0.3; }
  50% { opacity: 0.8; }
  100% { transform: translateY(-100vh) rotate(360deg); opacity: 0.3; }
}

img {
  max-width: 100%;
  height: auto;
  display: block;
}

.back-button {
  position: fixed;
  top: 20rpx;
  left: 20rpx;
  display: flex;
  align-items: center;
  background: rgba(255, 218, 185, 0.9);
  padding: 12rpx 24rpx;
  border-radius: 85rpx;
  color: #550000;
  font-size: 14px;
  cursor: pointer;
  z-index: 10;
  transition: transform 0.2s ease;
  box-shadow: 0 4rpx 12rpx rgba(85, 0, 0, 0.1);
}

.back-button:active {
  transform: scale(0.95);
}

.back-button .icon {
  font-size: 32rpx;
  color: #550000;
  margin-right: 8rpx;
}

.back-button text {
  font-size: 14px;
  color: #550000;
}

.content-scroll {
  padding: 80rpx 24rpx 120rpx 24rpx;
  flex: 1;
  overflow-y: auto;
}

/* 优化后的帖子卡片样式（关键修改点） */
.post-card {
  border-radius: 85rpx;
  margin-bottom: 0rpx;
  transition: transform 0.2s ease;
  background: #FFDAB9;
  box-shadow: 0 -5px 12px rgba(3, 3, 3, 0.2);
  padding: 0; /* 移除父容器内边距，使图片与文字紧贴 */
}

.post-card:hover {
  transform: translateY(-4rpx);
}

/* 图片区域样式 */
.post-image {
  width: 100%;
  height: 100%;
  overflow: hidden;
  border-radius: 85rpx 85rpx 0 0;
}

.item-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.item-image:hover {
  transform: scale(1.03);
}

/* 内容区域样式（关键修改点：调整内边距） */
.post-content {
  padding: 10rpx 16rpx; /* 减小垂直内边距，使文字贴近图片 */
  background: #FFDAB9;
}

.contents-text {
  font-size: 28rpx;
  line-height: 1.5; /* 略微减小行高，使文字更紧凑 */
  color: #5a3921;
  font-family: 'Roboto', sans-serif;
}

/* 交互区域样式（调整内边距） */
.interaction {
  display: flex;
  justify-content: space-around;
  padding: 10rpx 20rpx; /* 调整内边距，保持与内容区域的协调 */
  background: #FFDAB9;
  border-top: 1px solid #550000;
}

.interaction-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 24rpx;
  color: #5a3921;
  transition: transform 0.2s ease;
}

.interaction-item:hover {
  transform: scale(1.1);
}

.interaction-item .icon {
  font-size: 36rpx;
  color: #550000;
  margin-bottom: 2rpx;
}

/* 评论区域样式 */
.comments-section {
  border-radius: 85rpx;
  padding: 10rpx;
  background: #FFDAB9;
  box-shadow: 0 -5px 12px rgba(3, 3, 3, 0.2);
  margin-top: 15rpx; /* 与帖子保持适当间距 */
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #5a3921;
  font-family: 'Roboto', sans-serif;
}

.comment-count {
  font-size: 24rpx;
  color: #5a3921;
}

.comments-list {
  border-top: 1px solid #550000;
  padding-top: 8rpx;
}

.comment-item {
  margin-bottom: 8rpx;
  padding: 10rpx;
  background: #FFDAB9;
  border-radius: 85rpx;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease;
}

.comment-item:hover {
  transform: translateY(-2rpx);
}

.comment-text {
  font-size: 26rpx;
  color: #5a3921;
  font-family: 'Roboto', sans-serif;
}

.comment-time {
  font-size: 20rpx;
  color: #5a3921;
  text-align: right;
  font-family: 'Roboto', sans-serif;
  margin-top: 2rpx;
}

/* 评论输入框样式 */
.input-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  background: #FFDAB9;
  padding: 10rpx 24rpx;
  border-top: 1px solid #550000;
}

.comment-input {
  flex: 1;
  height: 60rpx;
  padding: 0 15rpx;
  background: rgba(255, 218, 185, 0.8);
  border-radius: 85rpx;
  font-size: 26rpx;
  margin-right: 10rpx;
  border: 1px solid #550000;
}

.submit-button {
  height: 60rpx;
  line-height: 60rpx;
  padding: 0 25rpx;
  background: radial-gradient(circle, #550000, #550000);
  color: #fff;
  border-radius: 40rpx;
  font-size: 16px;
  box-shadow: 0 2rpx 8rpx rgba(85, 0, 0, 0.3);
  transition: transform 0.2s ease;
}

.submit-button:active {
  transform: scale(0.95);
  background: #550000;
}
</style>