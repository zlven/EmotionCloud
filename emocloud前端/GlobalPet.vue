<template>
  <view
    class="pet-container"
    :style="{ left: petX + 'px', top: petY + 'px' }"
    @touchstart="handleTouchStart"
    @touchmove.stop.prevent="handleTouchMove"
    @touchend="handleTouchEnd"
    @tap.stop="handleTap"
  >
    <!-- 消息气泡调整为宠物右侧，避免重叠 -->
    <view v-if="petMsg" class="pet-msg-box">
      <text class="pet-msg">{{ petMsg }}</text>
      <!-- 小三角指向宠物 -->
      <view class="msg-arrow"></view>
    </view>
    
    <image 
      class="pet-image" 
      :src="petSrc" 
      mode="aspectFit" 
      @error="onImageError" 
    />
  </view>
</template>

<script>
export default {
  name: 'GlobalPet',
  data() {
    const getImageUrl = (path) => new URL(path, import.meta.url).href;

    const imgSrc = {
      Static: getImageUrl('./static/pet/Static.gif'),
      Attack: getImageUrl('./static/pet/Attack.gif'),
      Attacked: getImageUrl('./static/pet/Attacked.gif'),
      Walk: getImageUrl('./static/pet/Walk.gif'),
    };
    
    return {
      imgSrc: imgSrc,
      petSrc: imgSrc.Static,
      petMsg: '',
      isAnimating: false,
      isDragging: false,
      
      petX: 20,
      petY: 400,
      startPoint: null,
      startPetPosition: { x: 0, y: 0 },
    };
  },
  mounted() {
    console.log('GlobalPet: 组件已挂载，尝试显示宠物。');
    this.setDefaultPosition();
    // 初始问候语延迟显示，避免与模型加载重叠
    setTimeout(() => {
      this.petMsg = '你好啊主人，今天心情怎么样？';
    }, 1000);
  },
  methods: {
    onImageError(event) {
      console.error('GlobalPet: 图片加载失败！', event);
      this.petMsg = '图片加载异常';
    },

    setDefaultPosition() {
      const systemInfo = uni.getSystemInfoSync();
      // 调整初始位置，避免靠近边缘导致消息被截断
      this.petX = 30;
      this.petY = systemInfo.windowHeight - 180;
    },

    handleTap() {
      if (this.isAnimating || this.isDragging) return;
      
      this.isAnimating = true;
      this.petSrc = this.imgSrc.Attack;
      
      // 随机互动语句，避免重复
      const messages = [
        '主人在摸我呀~',
        '今天也要开心哦！',
        '喵~ 有事找我吗？',
        '一起玩呀主人~'
      ];
      this.petMsg = messages[Math.floor(Math.random() * messages.length)];

      setTimeout(() => {
        this.petSrc = this.imgSrc.Static;
        setTimeout(() => {
          this.petMsg = ''; // 自动隐藏消息
          this.isAnimating = false;
        }, 2000);
      }, 800);
    },

    handleTouchStart(event) {
      if (event.touches.length) {
        this.startPoint = event.touches[0];
        this.startPetPosition = { x: this.petX, y: this.petY };
        this.petSrc = this.imgSrc.Walk; // 拖动时切换行走动画
      }
    },
    
    handleTouchMove(event) {
      this.isDragging = true; 
      if (!this.startPoint || !event.touches.length) return;

      const currentPoint = event.touches[0];
      const deltaX = currentPoint.clientX - this.startPoint.clientX;
      const deltaY = currentPoint.clientY - this.startPoint.clientY;

      // 限制拖动范围，避免超出屏幕
      const systemInfo = uni.getSystemInfoSync();
      this.petX = Math.min(
        Math.max(this.startPetPosition.x + deltaX, 10),
        systemInfo.windowWidth - 120
      );
      this.petY = Math.min(
        Math.max(this.startPetPosition.y + deltaY, 10),
        systemInfo.windowHeight - 150
      );
    },

    handleTouchEnd() {
      this.startPoint = null;
      this.petSrc = this.imgSrc.Static; // 结束拖动恢复静态
      setTimeout(() => {
        this.isDragging = false;
      }, 50);
    },
  },
};
</script>

<style scoped>
.pet-container {
  position: fixed;
  z-index: 999;
  /* 增加点击区域，避免点不到 */
  padding: 10rpx;
}

.pet-image {
  width: 100rpx;
  height: 130rpx;
  z-index: 2; /* 确保宠物在消息下方，避免遮挡箭头 */
}

/* 消息气泡调整到宠物右侧上方，增加间距 */
.pet-msg-box {
  position: absolute;
  left: 110rpx; /* 与宠物保持距离 */
  top: -50rpx;
  background-color: rgba(40, 40, 40, 0.8);
  color: #fff;
  padding: 10rpx 20rpx;
  border-radius: 20rpx;
  white-space: nowrap;
  z-index: 1; /* 气泡在宠物下方 */
}

/* 小三角指向宠物，增强关联感 */
.msg-arrow {
  position: absolute;
  left: -10rpx;
  top: 30rpx;
  width: 0;
  height: 0;
  border-top: 10rpx solid transparent;
  border-bottom: 10rpx solid transparent;
  border-right: 10rpx solid rgba(40, 40, 40, 0.8);
}

.pet-msg {
  font-size: 24rpx;
  line-height: 1.4;
}
</style>