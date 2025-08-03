<template>
	<view class="page-container">
		<!-- 顶部导航栏 -->
		<view class="header-bar">
			<view class="back-icon" @click="goBack">
				<uni-icons type="left" size="22" color="#333"></uni-icons>
			</view>
			<text class="header-title">{{ isNew ? '新建日记' : '编辑日记' }}</text>
			<!-- 占位元素，让标题居中 -->
			<view class="placeholder"></view>
		</view>

		<!-- 滚动内容区 -->
		<scroll-view class="scroll-content" scroll-y>
			<view class="form-wrapper">
				<!-- 标题输入 -->
				<input v-model="diary.title" class="title-input" placeholder="给你的心情起个名字吧~" />

				<!-- 格式设置区域 -->
				<view class="card-section">
					<view class="setting-block">
						<text class="setting-label">装饰风格:</text>
						<view class="option-group">
							<text v-for="item in decorationOptions" :key="item"
								:class="['option-item', { 'active': diary.decoration === item }]"
								@click="selectDecoration(item)">
								{{ item }}
							</text>
						</view>
					</view>
					<!-- 如果需要字体和布局设置，可以取消下面的注释 -->
					<!-- 
					<view class="setting-block">
						<text class="setting-label">字体设置:</text>
						<view class="option-group">
							<text v-for="item in fontOptions" :key="item"
								:class="['option-item', { 'active': diary.font === item }]"
								@click="selectFont(item)">
								{{ item }}
							</text>
						</view>
					</view>
					<view class="setting-block">
						<text class="setting-label">布局设置:</text>
						<view class="option-group">
							<text v-for="item in layoutOptions" :key="item"
								:class="['option-item', { 'active': diary.layout === item }]"
								@click="selectLayout(item)">
								{{ item }}
							</text>
						</view>
					</view> 
					-->
				</view>

				<!-- 内容输入 -->
				<view class="card-section">
					<textarea v-model="diary.content" class="content-textarea" placeholder="写下你的心情..."
						maxlength="-1" auto-height />
				</view>

				<!-- 图片上传 -->
				<view class="card-section">
					<text class="setting-label">添加图片:</text>
					<view class="image-uploader">
						<!-- 已选图片列表 -->
						<view class="image-item" v-for="(img, index) in diary.images" :key="index">
							<image :src="img" mode="aspectFill" class="preview-image"></image>
							<view class="delete-icon" @click="deleteImage(index)">
								<uni-icons type="closeempty" size="14" color="#fff"></uni-icons>
							</view>
						</view>
						<!-- 上传按钮，只有当图片少于9张时显示 -->
						<view v-if="diary.images.length < 9" class="upload-button" @click="chooseImage">
							<uni-icons type="plusempty" size="28" color="#a0b9e6"></uni-icons>
							<text class="upload-text">上传图片</text>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 底部操作栏 -->
		<view class="footer-bar">
			<button class="save-button" @click="saveDiary">保存日记</button>
		</view>

		<!-- 全局宠物组件 -->
		<GlobalPet />
	</view>
</template>

<script>
export default {
  data() {
    return {
      diary: {
        id: null,
        title: '',
        content: '',
        font: 'SimSun',
        layout: 'default',
        decoration: 'none',
        images: [],
        videos: []
      },
      isNew: true,
      fontOptions: ['SimSun', 'SimHei', 'KaiTi', 'Microsoft YaHei'],
      layoutOptions: ['default', 'classic', 'modern', 'minimalist'],
      decorationOptions: ['none', 'flowers', 'stars', 'hearts'],
      // textareaHeight: '200rpx'
    };
  },
  onLoad(options) {
    // const options = this.$route.query;
    console.log('【编辑页】onLoad收到的options:', options); // 添加调试日志
    if (options.id) {
            // 编辑模式
            this.isNew = false;
            this.diary.id = options.id; 
            this.fetchDiary(options.id);
        } else {
            // 新建模式
            this.isNew = true;

            // --- 解决方案：从 options 中解码并填充数据 ---
            if (options.title) {
                // 使用 decodeURIComponent 解码，防止中文或特殊字符乱码
                this.diary.title = decodeURIComponent(options.title);
            }
            if (options.content) {
                this.diary.content = decodeURIComponent(options.content);
            }
        }
    
    // 获取用户ID
 // 从本地存储获取用户信息
   const userInfo = uni.getStorageSync('userInfo');
     console.log('从本地存储获取的userInfo:', userInfo);//调试信息
   // 安全获取用户ID
   this.diary.userId = userInfo && userInfo.id ? userInfo.id : null;
     console.log('设置的userId:', this.diary.userId);//调试信息
  
    if (!this.diary.userId) {
      uni.showToast({ title: '请先登录', icon: 'none' });
      uni.navigateTo({ url: '/pages/auth/login/index' });
    }
  },
  methods: {
    fetchDiary(id) {
      uni.request({
        url: `http://localhost:8080/emotion-log/diary/detail/${id}`,
        method: 'GET',
        success: (res) => {
          if (res.statusCode === 200) {
            this.diary = res.data;
          }
        }
      });
    },
    selectFont(font) {
      this.diary.font = font;
    },
    selectLayout(layout) {
      this.diary.layout = layout;
    },
    selectDecoration(decoration) {
      this.diary.decoration = decoration;
    },
    // autoResize(e) {
    //   // 自动调整textarea高度
    //   const query = uni.createSelectorQuery();
    //   query.select('.content-textarea').boundingClientRect(rect => {
    //     this.textareaHeight = rect.height + 'px';
    //   }).exec();
    // },
    chooseImage() {
      uni.chooseImage({
        count: 9 - (this.diary.images || []).length,
        success: (res) => {
          this.diary.images = [...(this.diary.images || []), ...res.tempFilePaths];
        }
      });
    },
    deleteImage(index) {
      this.diary.images.splice(index, 1);
    },
    saveDiary() {
      if (!this.diary.content.trim()) {
        uni.showToast({
          title: '请输入内容',
          icon: 'none'
        });
        return;
      }
      
      if (!this.diary.userId) {
        uni.showToast({
          title: '用户未登录',
          icon: 'none'
        });
        return;
      }
      
      // 构建符合后端要求的请求数据
      const requestData = {
        title: this.diary.title,
        content: this.diary.content,
        font: this.diary.font,
        layout: this.diary.layout,
        decoration: this.diary.decoration,
        images: this.diary.images || [],
        videos: [], // 暂时不支持视频
        userId: this.diary.userId,
	
      };
      
      const url = this.isNew 
        ? 'http://localhost:8080/emotion-log/diary/create'
        : `http://localhost:8080/emotion-log/diary/edit/${this.diary.id}`;
  
      const method = this.isNew ? 'POST' : 'PUT';
      
      uni.showLoading({
        title: '保存中...'
      });
      
      uni.request({
        url,
        method,
        data: requestData,
        header: { 
          'Content-Type': 'application/json',
          Authorization: `Bearer ${uni.getStorageSync('token')}` 
        },
        success: (res) => {
          uni.hideLoading();
          if (res.statusCode >= 200 && res.statusCode < 300) {
            uni.showToast({
              title: this.isNew ? '创建成功' : '更新成功',
              icon: 'success'
            });
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          } else {
            uni.showToast({
              title: `保存失败: ${res.data.message || '未知错误'}`,
              icon: 'none'
            });
          }
        },
        fail: (err) => {
          uni.hideLoading();
          uni.showToast({
            title: `网络错误: ${err.errMsg}`,
            icon: 'none'
          });
        }
      });
    },
    goBack() {
      uni.navigateBack();
    }
  }
};
</script>

<style lang="scss" scoped>
	/* 全局容器和布局 */
	.page-container {
		display: flex;
		flex-direction: column;
		height: 100vh;
		background: #fff8e9; 
	}

	.scroll-content {
		flex: 1;
		overflow-y: auto;
	}

	.form-wrapper {
		padding: 30rpx;
	}

	/* 顶部导航栏 */
	.header-bar {
		display: flex;
		align-items: center;
		justify-content: space-between;
		height: 44px; // 标准导航栏高度
		padding: 0 30rpx;
		background-color: #ffffff;
		border-bottom: 1rpx solid #e9e9e9;
		position: sticky;
		top: 0;
		z-index: 99;

		.back-icon, .placeholder {
			width: 60rpx;
			height: 100%;
			display: flex;
			align-items: center;
		}

		.header-title {
			font-size: 34rpx;
			font-weight: 600;
			color: #333;
		}
	}

	/* 标题输入框 */
	.title-input {
		width: 100%;
		font-size: 40rpx;
		font-weight: 600;
		padding: 30rpx 0;
		border: none;
		border-bottom: 1rpx solid #e9e9e9;
		margin-bottom: 30rpx;
		background-color: transparent;
		color: #333;
		&::placeholder {
			color: #b0b0b0;
		}
	}

	/* 卡片式分区 */
	.card-section {
		// background-color: #fff8e9;
		background-color: #ffffff;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.04);
	}

	/* 设置项 (如装饰风格) */
	.setting-block {
		margin-bottom: 20rpx;
		&:last-child {
			margin-bottom: 0;
		}
	}
	
	.setting-label {
		display: block;
		font-size: 30rpx;
		color: #333;
		margin-bottom: 20rpx;
		font-weight: 500;
	}

	/* 选项组 (Flex 布局) */
	.option-group {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}

	.option-item {
		padding: 12rpx 30rpx;
		border-radius: 30rpx;
		background-color: #f0f0f0;
		color: #666;
		font-size: 26rpx;
		transition: all 0.2s ease;

		&.active {
			background-color: #4a77e5;
			color: #ffffff;
			font-weight: 500;
		}
	}

	/* 内容输入框 */
	.content-textarea {
		width: 100%;
		min-height: 250rpx; // 初始高度
		font-size: 30rpx;
		line-height: 1.6;
		color: #333;
		padding: 0; // card-section 已有内边距
	}

	/* 图片上传区域 */
	.image-uploader {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}

	.image-item, .upload-button {
		width: 180rpx;
		height: 180rpx;
		border-radius: 12rpx;
	}
	
	.image-item {
		position: relative;
		overflow: hidden;
		.preview-image {
			width: 100%;
			height: 100%;
		}
		.delete-icon {
			position: absolute;
			top: 0;
			right: 0;
			width: 40rpx;
			height: 40rpx;
			background-color: rgba(0, 0, 0, 0.5);
			border-radius: 0 0 0 12rpx;
			display: flex;
			align-items: center;
			justify-content: center;
		}
	}

	.upload-button {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		border: 2rpx dashed #d0d8e8;
		background-color: #f9fafc;
		transition: all 0.2s ease;
		.upload-text {
			font-size: 24rpx;
			color: #a0b9e6;
			margin-top: 10rpx;
		}
		&:active {
			border-color: #4a77e5;
			background-color: #eff3fe;
		}
	}
	
	/* 底部操作栏 */
	.footer-bar {
		padding: 20rpx 30rpx;
		background-color: #ffffff;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
	}
	
	.save-button {
		width: 100%;
		height: 90rpx;
		line-height: 90rpx;
		border-radius: 45rpx;
		background-color: #4a77e5;
		color: #fff;
		font-size: 32rpx;
		font-weight: 500;
		border: none; // 清除 uni-app button 默认边框
		// uni-app button 样式修复
		&::after {
			border: none;
		}
		&:active {
			background-color: #3b60b7;
		}
	}

</style>