<template>
  <view class="publish-page">
    <!-- 页面标题 -->
    <view class="header">
      <text class="title">发布心情</text>
      <text class="confirm" @click="submitPost" :class="{ 'disabled': isSubmitting }">发布</text>
    </view>

    <!-- 文字输入区域 -->
    <view class="input-area">
      <textarea 
        class="text-input" 
        placeholder="分享你的心情故事..." 
        v-model="postContent"
        maxlength="500"
        @input="handleInput"
      ></textarea>
      <text class="char-count">{{ postContent.length }}/500</text>
    </view>

    <!-- 图片上传区域 -->
    <view class="image-uploader">
      <text class="section-title">上传图片 (可选)</text>
      <view class="image-container">
        <view 
          class="image-item" 
          v-for="(img, index) in uploadedImages" 
          :key="index"
          @click="previewImage(index)"
        >
          <image :src="img" mode="aspectFill" class="preview-image"></image>
          <view class="delete-btn" @click.stop="deleteImage(index)">
            <text class="icon">×</text>
          </view>
        </view>
        <view class="add-image" @click="chooseImage">
          <text class="icon">＋</text>
          <text>添加图片</text>
        </view>
      </view>
    </view>

    <!-- 情绪标签选择 -->
    <view class="tag-selector">
      <text class="section-title">选择情绪标签</text>
      <view class="tag-container">
        <view 
          class="tag-item" 
          v-for="(tag, index) in emotionTags" 
          :key="index" 
          :class="{ 'active': selectedTags.includes(tag) }"
          @click="toggleTag(tag)"
        >
          {{ tag }}
        </view>
        <view class="add-tag" @click="showAddTagModal">
          <text class="icon">＋</text>
          <text>添加标签</text>
        </view>
      </view>
    </view>

    <!-- 预览模态框 -->
    <view class="preview-modal" v-if="previewIndex !== -1">
      <view class="modal-mask" @click="closePreview"></view>
      <view class="modal-content">
        <image :src="uploadedImages[previewIndex]" mode="aspectFit" class="modal-image"></image>
        <view class="modal-actions">
          <text class="close-btn" @click="closePreview">关闭</text>
        </view>
      </view>
    </view>

    <!-- 添加标签模态框 -->
    <view class="add-tag-modal" v-if="showTagModal">
      <view class="modal-mask" @click="hideAddTagModal"></view>
      <view class="modal-content">
        <view class="modal-header">
          <text class="title">添加自定义标签</text>
        </view>
        <view class="modal-body">
          <input 
            class="tag-input" 
            placeholder="输入新标签 (如：开心、难过、兴奋)" 
            v-model="newTag"
            @confirm="addCustomTag"
          >
        </view>
        <view class="modal-footer">
          <text class="cancel-btn" @click="hideAddTagModal">取消</text>
          <text class="confirm-btn" @click="addCustomTag">确定</text>
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
      postContent: '',
      uploadedImages: [],
      emotionTags: ['开心', '难过', '兴奋', '平静', '愤怒', '感动', '困惑', '惊喜'],
      selectedTags: [],
      newTag: '',
      showTagModal: false,
      previewIndex: -1,
      maxImages: 9,
      isSubmitting: false,
      serverUrl: 'http://localhost:8080',
      userId: ''

    }
  },
  onLoad() {
    const userInfo = uni.getStorageSync('userInfo');
    this.userId = userInfo && userInfo.id ? userInfo.id : null;
    console.log('【发布界面】用户ID:', this.userId);
    
    if (!this.userId) {
      uni.showToast({ title: '请先登录', icon: 'none' });
      uni.navigateTo({ url: '/pages/auth/login/index' });
    }
  },
  methods: {
    handleInput(e) {
      this.postContent = e.detail.value;
    },
    
    toggleTag(tag) {
      if (this.selectedTags.includes(tag)) {
        this.selectedTags = this.selectedTags.filter(t => t !== tag);
      } else {
        this.selectedTags.push(tag);
      }
    },
    
    chooseImage() {
      if (this.uploadedImages.length >= this.maxImages) {
        uni.showToast({ title: '最多只能上传9张图片', icon: 'none' });
        return;
      }
      
      uni.showLoading({ title: '加载图片中...' });
      uni.chooseImage({
        count: this.maxImages - this.uploadedImages.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          this.uploadedImages = this.uploadedImages.concat(res.tempFilePaths);
          uni.hideLoading();
        },
        fail: () => {
          uni.hideLoading();
          uni.showToast({ title: '图片选择失败', icon: 'none' });
        }
      });
    },
    
    previewImage(index) {
      this.previewIndex = index;
    },
    
    closePreview() {
      this.previewIndex = -1;
    },
    
    deleteImage(index) {
      this.uploadedImages.splice(index, 1);
    },
    
    showAddTagModal() {
      this.showTagModal = true;
    },
    
    hideAddTagModal() {
      this.showTagModal = false;
      this.newTag = '';
    },
    
    addCustomTag() {
      if (!this.newTag.trim()) {
        uni.showToast({ title: '标签不能为空', icon: 'none' });
        return;
      }
      
      if (this.emotionTags.includes(this.newTag) || this.selectedTags.includes(this.newTag)) {
        uni.showToast({ title: '标签已存在', icon: 'none' });
        return;
      }
      
      this.emotionTags.push(this.newTag);
      this.selectedTags.push(this.newTag);
      this.hideAddTagModal();
    },
  uploadImages(postId) {
    return new Promise((resolve, reject) => {
      if (this.uploadedImages.length === 0) {
        resolve([]);
        return;
      }
      
      const uploadTasks = this.uploadedImages.map((imagePath, index) => {
        return new Promise((resolveSingle, rejectSingle) => {
          uni.uploadFile({
            url: `${this.serverUrl}/social/posts/${postId}/images`, // 注意这里使用单图上传接口
            filePath: imagePath,
            name: 'file',
            formData: {
              sortOrder: index + 1
            },
            success: (res) => {
              console.log('图片上传响应:', res);
              try {
                const data = JSON.parse(res.data);
                if (res.statusCode === 201) {
                  resolveSingle(data.imageUrl);
                } else {
                  rejectSingle(new Error(data?.message || '上传失败'));
                }
              } catch (e) {
                console.error('响应解析失败:', e);
                rejectSingle(new Error('响应解析失败'));
              }
            },
            fail: (err) => {
              console.error('上传失败:', err);
              rejectSingle(err);
            }
          });
        });
      });
      
      Promise.all(uploadTasks)
        .then((results) => {
          resolve(results);
        })
        .catch((error) => {
          console.error('图片上传过程中出错:', error);
          reject(error);
        });
    });
  },
    
    submitPost() {
      if (this.isSubmitting) return;
      if (!this.postContent.trim() && this.uploadedImages.length === 0 && this.selectedTags.length === 0) {
        uni.showToast({
          title: '至少需要输入文字、选择标签或上传图片',
          icon: 'none'
        });
        return;
      }
      this.isSubmitting = true;
      uni.showLoading({ title: '发布中...' });
      
      const postDTO = {
        title: '',
        contents: this.postContent,
        moodTag: this.selectedTags.join(','),
        publisherId: parseInt(this.userId, 10) || 0,
		commentCount: 0, // Default value
		collectCount: 0, // Default value
		likeCount: 0, // Default value
		shareCount: 0 // Default value
      };
	  console.log('Sending postDTO:', postDTO); // Debug payload
    this.createPost(postDTO)
        .then((postId) => {
          console.log('获取到的帖子ID:', postId);
          
          if (this.uploadedImages.length > 0) {
            return this.uploadImages(postId);
          }
          
          return Promise.resolve();
        })
        .then(() => {
          this.handleSubmitSuccess({ statusCode: 201 });
        })
        .catch((error) => {
          console.error('提交过程中出错:', error);
          this.handleSubmitError(error);
        })
        .finally(() => {
          this.isSubmitting = false;
          uni.hideLoading();
        });
    },
    
    handleSubmitError(error) {
      uni.hideLoading();
      this.isSubmitting = false;
      console.error('提交失败:', error);
      
      let errorMsg = '发布失败，请重试';
      if (error.statusCode === 403) {
        errorMsg = '权限不足，请检查登录状态';
        uni.navigateTo({ url: '/pages/auth/login/index' });
      } else if (error.statusCode === 401) {
        errorMsg = '未授权，请重新登录';
        uni.navigateTo({ url: '/pages/auth/login/index' });
      } else if (error.statusCode === 404) {
        errorMsg = '接口不存在，请检查后端接口';
      } else if (error.statusCode === 500) {
        errorMsg = '服务器内部错误，请稍后再试';
      } else if (error.data?.message) {
        errorMsg = error.data.message;
      } else if (error.errMsg?.includes('timeout')) {
        errorMsg = '请求超时，请检查网络连接';
      } else if (error.errMsg) {
        errorMsg = error.errMsg;
      }
      
      uni.showToast({
        title: errorMsg,
        icon: 'none',
        duration: 3000
      });
    },
    
    createPost(postDTO) {
      return new Promise((resolve, reject) => {
        uni.request({
          url: `${this.serverUrl}/social/posts`,
          method: 'POST',
          header: {
            'Content-Type': 'application/json'
          },
          data: postDTO,
          success: (res) => {
            console.log('Create post response:', res);
            if (res.statusCode === 201) {
              const postId = res.data.postid;
              resolve(postId);
            } else {
              reject(new Error(res.data?.message || '创建帖子失败'));
            }
          },
          fail: (err) => {
            console.error('Create post error:', err);
            console.error('Error response:', err.data);
            reject(err);
          }
        });
      });
    },
    
    handleSubmitSuccess(response) {
      uni.hideLoading();
      this.isSubmitting = false;
      if (response.statusCode === 201) {
        uni.showToast({
          title: '发布成功',
          icon: 'success'
        });
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);
      } else {
        uni.showToast({
          title: response.data?.message || '发布失败',
          icon: 'none'
        });
      }
    }
  }
}
</script>
<style>
/* 页面整体样式 */
.publish-page {
  padding: 30rpx;
  background: linear-gradient(to bottom, #ffc1cd, #FFE4B5, #CCE5FF); /* 黄色渐变背景 */
  height: 100vh;
  box-sizing: border-box;
}

/* 头部样式 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  margin-bottom: 30rpx;
  border-bottom: 1rpx solid #550000; /* 深棕色边框 */
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #5a3921; /* 暖棕色标题 */
}

.confirm {
  font-size: 28rpx;
  color: #550000; /* 深棕色发布按钮 */
}

.confirm.disabled {
  opacity: 0.5;
  pointer-events: none;
}

/* 文字输入区域 */
.input-area {
  margin-bottom: 40rpx;
}

.text-input {
  width: 100%;
  height: 300rpx;
  background-color: #FFDAB9; /* 米黄色输入框 */
  border-radius: 12rpx;
  padding: 20rpx;
  box-sizing: border-box;
  font-size: 28rpx;
  line-height: 1.5;
  border: 1rpx solid #550000; /* 深棕色边框 */
  box-shadow: 0 2rpx 10rpx rgba(85, 0, 0, 0.1); /* 暖色调阴影 */
}

.char-count {
  display: flex;
  justify-content: flex-end;
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #5a3921; /* 暖棕色字数统计 */
}

/* 图片上传区域和标签选择区域 */
.image-uploader, .tag-selector {
  margin-bottom: 40rpx;
  background-color: #FFDAB9; /* 米黄色卡片 */
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(85, 0, 0, 0.1); /* 暖色调阴影 */
  border: 1rpx solid #550000; /* 深棕色边框 */
}

.section-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #5a3921; /* 暖棕色标题 */
  margin-bottom: 20rpx;
}

/* 图片上传区域 */
.image-container {
  display: flex;
  flex-wrap: wrap;
}

.image-item {
  width: calc((100% - 40rpx) / 4);
  height: calc((100% - 40rpx) / 4);
  margin-right: 20rpx;
  margin-bottom: 20rpx;
  position: relative;
  border-radius: 10rpx;
  overflow: hidden;
  border: 1rpx solid #550000; /* 深棕色边框 */
}

.preview-image {
  width: 100%;
  height: 100%;
}

.delete-btn {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  width: 40rpx;
  height: 40rpx;
  background-color: rgba(85, 0, 0, 0.5); /* 深棕色半透明背景 */
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
  font-size: 24rpx;
  z-index: 1;
}

.add-image {
  width: calc((100% - 40rpx) / 4);
  height: calc((100% - 40rpx) / 4);
  background-color: #FFE4B5; /* 浅黄色添加按钮 */
  border-radius: 10rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #5a3921; /* 暖棕色文字 */
  font-size: 26rpx;
  cursor: pointer;
  border: 1rpx solid #550000; /* 深棕色边框 */
}

.add-image .icon {
  font-size: 40rpx;
  margin-bottom: 6rpx;
  color: #550000; /* 深棕色图标 */
}

/* 标签选择区域 */
.tag-container {
  display: flex;
  flex-wrap: wrap;
}

.tag-item {
  padding: 12rpx 24rpx;
  background-color: #FFE4B5; /* 浅黄色标签 */
  color: #5a3921; /* 暖棕色文字 */
  font-size: 26rpx;
  border-radius: 30rpx;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
  cursor: pointer;
  transition: all 0.3s;
  border: 1rpx solid #550000; /* 深棕色边框 */
}

.tag-item.active {
  background-color: #550000; /* 深棕色选中状态 */
  color: #FFDAB9; /* 米黄色文字 */
  border-color: #550000; /* 深棕色边框 */
}

.add-tag {
  padding: 12rpx 24rpx;
  background-color: #FFE4B5; /* 浅黄色添加标签 */
  color: #5a3921; /* 暖棕色文字 */
  font-size: 26rpx;
  border-radius: 30rpx;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
  display: flex;
  align-items: center;
  cursor: pointer;
  border: 1rpx solid #550000; /* 深棕色边框 */
}

.add-tag .icon {
  margin-right: 6rpx;
  color: #550000; /* 深棕色图标 */
}

/* 模态框样式 */
.preview-modal, .add-tag-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(85, 0, 0, 0.7); /* 深棕色半透明遮罩 */
}

.modal-content {
  position: relative;
  background-color: #FFDAB9; /* 米黄色模态框 */
  border-radius: 16rpx;
  overflow: hidden;
  max-width: 90%;
  max-height: 80%;
  box-shadow: 0 10rpx 30rpx rgba(85, 0, 0, 0.2); /* 暖色调阴影 */
  border: 1rpx solid #550000; /* 深棕色边框 */
}

.modal-image {
  width: 100%;
  height: auto;
  max-height: 70vh;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  padding: 20rpx;
}

.close-btn {
  color: #550000; /* 深棕色关闭按钮 */
  font-size: 28rpx;
}

.modal-header {
  padding: 30rpx;
  border-bottom: 1rpx solid #550000; /* 深棕色边框 */
  display: flex;
  justify-content: center;
  background-color: #FFE4B5; /* 浅黄色头部背景 */
}

.modal-header .title {
  font-size: 32rpx;
  font-weight: bold;
  color: #5a3921; /* 暖棕色标题 */
}

.modal-body {
  padding: 30rpx;
  background-color: #FFDAB9; /* 米黄色主体背景 */
}

.tag-input {
  width: 100%;
  height: 80rpx;
  border: 1rpx solid #550000; /* 深棕色边框 */
  border-radius: 8rpx;
  padding: 0 20rpx;
  box-sizing: border-box;
  font-size: 28rpx;
  background-color: #FFE4B5; /* 浅黄色输入框背景 */
}

.modal-footer {
  display: flex;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  border-top: 1rpx solid #550000; /* 深棕色边框 */
  background-color: #FFE4B5; /* 浅黄色底部背景 */
}

.cancel-btn, .confirm-btn {
  font-size: 28rpx;
  padding: 10rpx 30rpx;
  border-radius: 30rpx;
  border: none;
}

.cancel-btn {
  color: #5a3921; /* 暖棕色取消按钮 */
  background-color: #FFDAB9; /* 米黄色背景 */
}

.confirm-btn {
  color: #FFDAB9; /* 米黄色文字 */
  background-color: #550000; /* 深棕色确定按钮 */
}
</style>