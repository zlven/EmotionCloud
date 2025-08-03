<template>
  <view class="layout">
    <view>
      <view class="user-page">
        <view class="header">
          <view class="user-info">
            <view class="avatar-box">
              <view class="gradient-circle large">
                <image class="avatar-img" src="/static/my-notion-face-portrait.png"></image>
              </view>
              <view class="upload-sign">
                <view>+</view>
              </view>
            </view>
            <view class="flex-center">
              <view>
                <view class="name-row">
                  <text class="username">ylp</text>
                </view>
                <text class="user-id">ID: xhs_123456789</text>
              </view>
            </view>
          </view>
          <view class="signature">everything will be fine.</view>
          <view class="sign">
            <view class="level-badge">Lv.12</view>
            <view class="flex-center">
              <view class="gender-badge">♀</view>
            </view>
          </view>
          <!-- 数据面板 -->
          <view class="data-panel">
            <view class="user-data">
              <view v-for="item in state.panelData" :key="item.label">
                <view class="user-number">{{ item.value }}</view>
                <view class="user-label">{{ item.label }}</view>
              </view>
            </view>
            <view class="edit-btns">
              <view class="btn">编辑资料</view>
            </view>
          </view>
          <view class="other-panel">
            <view class="other-box" v-for="item in state.panelOtherData" :key="item.title">
              <view class="other-title">{{ item.title }}</view>
              <view class="other-label">{{ item.label }}</view>
            </view>
          </view>
        </view>
        <view class="reds-sticky-box user-page-sticky" :class="[state.isSticky ? 'sticky' : '']" style="height: 72px;"
          :style="{ '--stick-top': `${state.stickyHeight}px` }">
          <view class="reds-sticky" :style="{ borderRadius: state.isSticky ? '' : '20px 20px 0 0' }">
            <view class="reds-tabs-list">
              <view id="sticky-tabs" style="display: flex;">
                <view v-for="(item, index) in state.tab" :key="item.value" :id="`sticky-tabs-${item.value}`"
                  class="reds-tab-item sub-tab-list" :class="[state.tabValue === item.value ? 'active' : '']"
                  @click="onChangeTab(item, index)" style="padding: 0px 16px;">
                  <text>{{ item.label }}</text>
                </view>
              </view>
              <view class="active-tag"
                style="transition: left 0.3s cubic-bezier(0.2, 0, 0.25, 1), width 0.3s cubic-bezier(0.2, 0, 0.25, 1)"
                :style="{ left: `${state.tabLeft}px`, width: `${state.tabWidth}px` }"></view>
            </view>
          </view>
        </view>
      </view>
      <view v-if="state.tabValue === '2'" class="divider"></view>
      <view class="feeds-tab-container">
        <view class="transform-container"
          :style="{ transform: state.transformValue, width: `${state.transformContainerWidth}px` }"
          @touchstart="touchStart" @touchmove="touchMove" @touchend="touchEnd">
          <view class="tab-content-item"
            :style="{ height: state.tabValue === '1' ? 'auto' : '0px', width: state.tabContentItemWidth, overflow: state.isChanging && (state.tabValue === '1' || state.beforeTabValue === '1') ? '' : 'hidden' }">
            <view class="feeds-container" style="width: 100%; visibility:visible"
              :style="{ height: state.isChanging && (state.tabValue === '1' || state.beforeTabValue === '1') ? `${computedMaxHeight()}px` : `${state.notesContainerElHeight}px` }">
              <view v-for="(item, index) in state.notesList" :key="index" class="note-item"
                :style="{ transform: item.transform, width: item.noteWidth }">
                <view>
                  <view class="cover mask ld" :style="{ width: item.coverWidth, height: item.coverHeight }">
                    <view
                      style="width: 100%; height: 100%; background-color: #999; display: flex; justify-content: center; align-items: center; color: #fff; font-weight: 600;">
                      {{ item.label }}</view>
                  </view>
                  <view class="footer">
                    <view class="title">
                      <view
                        style="width: 100%; height: 20px; background-color: #999; border-radius: 10px; margin-top: 6px;">
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
          <view class="tab-content-item"
            :style="{ height: state.tabValue === '2' ? 'auto' : '0px', width: state.tabContentItemWidth, overflow: state.isChanging && (state.tabValue === '2' || state.beforeTabValue === '2') ? '' : 'hidden' }">
            <view class="sub-tab-list">
              <view class="left reds-tabs-list" style="padding:0px 0px;">
                <view class="reds-tab-item active" style="padding:0px 16px;margin-right:0px;font-size:16px;"
                  data-v-64126f47=""><span>笔记・108</span></view>
                <view class="reds-tab-item" style="padding:0px 16px;margin-right:0px;font-size:16px;"
                  data-v-64126f47=""><span>专辑・0</span></view>
                <view class="active-tag" style="width: 109.609px; left: 0px;"></view>
              </view>
            </view>
            <view class="feeds-container" style="width: 100%; visibility:visible"
              :style="{ height: state.isChanging && (state.tabValue === '2' || state.beforeTabValue === '2') ? `${computedMaxHeight()}px` : `${state.colletsContainerElHeight}px` }">
              <view v-for="(item, index) in state.colletsList" :key="index" class="note-item"
                :style="{ transform: item.transform, width: item.noteWidth }">
                <view>
                  <view class="cover mask ld" :style="{ width: item.coverWidth, height: item.coverHeight }">
                    <view
                      style="width: 100%; height: 100%; background-color: #999; display: flex; justify-content: center; align-items: center; color: #fff; font-weight: 600;">
                      {{ item.label }}</view>
                  </view>
                  <view class="footer">
                    <view class="title">
                      <view
                        style="width: 100%; height: 20px; background-color: #999; border-radius: 10px; margin-top: 6px;">
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
          <view class="tab-content-item"
            :style="{ height: state.tabValue === '3' ? 'auto' : '0px', width: state.tabContentItemWidth, overflow: state.isChanging && (state.tabValue === '3' || state.beforeTabValue === '3') ? '' : 'hidden' }">
            <view class="feeds-container" style="width: 100%; visibility:visible"
              :style="{ height: state.isChanging && (state.tabValue === '3' || state.beforeTabValue === '3') ? `${computedMaxHeight()}px` : `${state.likesContainerElHeight}px` }">
              <view v-for="(item, index) in state.likesList" :key="index" class="note-item"
                :style="{ transform: item.transform, width: item.noteWidth }">
                <view>
                  <view class="cover mask ld" :style="{ width: item.coverWidth, height: item.coverHeight }">
                    <view
                      style="width: 100%; height: 100%; background-color: #999; display: flex; justify-content: center; align-items: center; color: #fff; font-weight: 600;">
                      {{ item.label }}</view>
                  </view>
                  <view class="footer">
                    <view class="title">
                      <view
                        style="width: 100%; height: 20px; background-color: #999; border-radius: 10px; margin-top: 6px;">
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
  <view class="bottom-menu"></view>
  
  <GlobalPet />
</template>

<script>
import { onPageScroll } from '@dcloudio/uni-app'

export default {
  data() {
    return {
      state: {
        panelData: [
          {
            value: 256,
            label: '关注'
          },
          {
            value: '12.8万',
            label: '粉丝'
          },
          {
            value: '36.9万',
            label: '获赞与收藏'
          }
        ],
        panelOtherData: [
          {
            title: '购物',
            label: '好逛好玩又好买'
          },
          {
            title: '订单',
            label: '查看我的订单'
          },
          {
            title: '购物车',
            label: '10个商品'
          }
        ],
        beforeTabValue: '1',
        tabValue: '1',
        tabLeft: 0,
        tabWidth: 0,
        isSticky: false,
        stickyHeight: 0,
        transformValue: 'translate(0px, 0px)',
        tab: [
          {
            label: '笔记',
            value: '1',
            left: 0,
            width: 0,
          },
          {
            label: '收藏',
            value: '2',
            left: 0,
            width: 0,
          },
          {
            label: '点赞',
            value: '3',
            left: 0,
            width: 0,
          }
        ],
        isChanging: false,
        scrollPositions: {
          1: 0,
          2: 0,
          3: 0
        }, // 记录每个 Tab 的滚动位置
        startX: 0, // 触摸开始的X坐标
        startY: 0, // 触摸开始的Y坐标
        distanceX: 0, // 水平滑动距离
        distanceY: 0,  // 垂直滑动距离
        notesList: [
          {
            label: '1',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '2',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '3',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '4',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '5',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
        ],
        colletsList: [
          {
            label: '1',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '2',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '3',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '4',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '5',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '6',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '7',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '8',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '9',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '10',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
        ],
        likesList: [
          {
            label: '1',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '2',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '3',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '4',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '5',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '6',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '7',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '8',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '9',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
          {
            label: '10',
            coverWidth: 0,
            coverHeight: 0,
            noteWidth: 0,
            transform: '',
            dataSetWidth: 1440,
            dataSetHeight: 1920
          },
        ],
        notesContainerElHeight: 400,
        colletsContainerElHeight: 0,
        likesContainerElHeight: 0,
        transformContainerWidth: 0,
        tabContentItemWidth: '',
      }
    };
  },
  methods: {
    touchStart(event) {
      const touch = event.touches[0];
      this.state.startX = touch.clientX;
      this.state.startY = touch.clientY;
    },
    touchMove(event) {
      const touch = event.touches[0];
      this.state.distanceX = touch.clientX - this.state.startX;
      this.state.distanceY = touch.clientY - this.state.startY;
    },
    async touchEnd() {
      const { distanceX, tab, tabValue } = this.state;
      const threshold = 80;

      if (Math.abs(distanceX) > threshold) {
        const currentIndex = tab.findIndex(item => item.value === tabValue);
        const targetIndex = distanceX > 0 ? currentIndex - 1 : currentIndex + 1;

        if (targetIndex >= 0 && targetIndex < tab.length) {
          const targetTab = tab[targetIndex];
          Object.assign(this.state, {
            beforeTabValue: tabValue,
            tabValue: targetTab.value,
            tabLeft: targetTab.left,
            tabWidth: targetTab.width,
            transformValue: `translate(-${targetIndex * (this.state.transformContainerWidth / 3)}px, 0px)`,
          });
          await this.scrollAuto();
        }
      }
    },
    async onChangeTab(data, index) {
      this.state.beforeTabValue = this.state.tabValue;
      this.state.tabValue = data.value;
      this.state.tabLeft = data.left;
      this.state.tabWidth = data.width;
      this.state.transformValue = `translate(-${index * (this.state.transformContainerWidth / 3)}px, 0px)`;
      await this.scrollAuto();
    },
    async scrollAuto() {
      this.state.isChanging = true;
      await setTimeout(() => {
        this.state.isChanging = false;
      }, 300);
      if (this.state.isSticky) {
        let targetScrollPosition = this.state.scrollPositions[this.state.tabValue];
        this.$nextTick(() => {
          setTimeout(() => {
            uni.pageScrollTo({
              scrollTop: targetScrollPosition,
              duration: 400
            });
          }, 400);
        });
      }
    },
    computedMaxHeight() {
      return Math.max(this.state.notesContainerElHeight, this.state.colletsContainerElHeight, this.state.likesContainerElHeight) + 100;
    },
    getShortestColumn(colHeights) {
      let shortestIndex = 0;
      for (let i = 1; i < colHeights.length; i++) {
        if (colHeights[i] < colHeights[shortestIndex]) {
          shortestIndex = i;
        }
      }
      return shortestIndex;
    },
    layout(sign, data) {
      const systemInfo = uni.getSystemInfoSync();
      const layoutInfo = {
        columns: 2,
        gapV: 6,
        gapH: 12,
        columnWidth: Math.max(142, (systemInfo.windowWidth - (12 * 3)) / 2)
      };

      const containerWidth = Math.ceil(layoutInfo.columns * layoutInfo.columnWidth + layoutInfo.columns * layoutInfo.gapH);
      this.state.transformContainerWidth = containerWidth * this.state.tab.length;
      this.state.tabContentItemWidth = (containerWidth - layoutInfo.gapH) + 'px';
      let nextTop, nextLeft, shortestColumn, note;
      const colHeights = new Array(layoutInfo.columns).fill(0);
      for (let i = 0; i < data.length; i++) {
        note = data[i];
        const isAlignLeft = data.length % layoutInfo.columns === 0 ? false : data.length - i <= data.length % layoutInfo.columns;
        shortestColumn = isAlignLeft ? i % layoutInfo.columns : this.getShortestColumn(colHeights);
        nextTop = colHeights[shortestColumn];
        nextLeft = shortestColumn * (layoutInfo.columnWidth + layoutInfo.gapH);
        const rawWidth = note.dataSetWidth;
        const rawHeight = note.dataSetHeight;
        data[i].coverWidth = layoutInfo.columnWidth + 'px';
        data[i].coverHeight = Math.round(layoutInfo.columnWidth / rawWidth * rawHeight) + 'px';
        data[i].borderRadius = layoutInfo.columns < 3 ? '12px' : '16px';
        if (data.length >= layoutInfo.columns) {
          data[i].transform = 'translate(' + nextLeft + 'px,' + nextTop + 'px)';
        }
        data[i].noteWidth = layoutInfo.columnWidth + 'px';
        colHeights[shortestColumn] = colHeights[shortestColumn] + Math.round(layoutInfo.columnWidth / rawWidth * rawHeight) + 42 + layoutInfo.gapV;
        if (sign === 'notes') {
          this.state.notesContainerElHeight = Math.max.apply(null, colHeights) + 140;
        } else if (sign === 'collets') {
          this.state.colletsContainerElHeight = Math.max.apply(null, colHeights) + 140;
        } else if (sign === 'likes') {
          this.state.likesContainerElHeight = Math.max.apply(null, colHeights) + 140;
        }
      }
      return data;
    },
    getNavBarHeight() {
      const systemInfo = uni.getSystemInfoSync();
      return systemInfo.windowTop;
    }
  },
  mounted() {
    this.state.notesList = this.layout("notes", this.state.notesList);
    this.state.colletsList = this.layout("collets", this.state.colletsList);
    this.state.likesList = this.layout("likes", this.state.likesList);
    this.state.stickyHeight = this.getNavBarHeight();

    const query = uni.createSelectorQuery().in(this);
    this.state.tab.forEach((item, index) => {
      query.select(`#sticky-tabs-${item.value}`).boundingClientRect(data => {
        item.width = data.width;
      }).exec();
    });
    setTimeout(() => {
      this.state.tab.forEach((item, index) => {
        item.left = index === 0 ? 0 : this.state.tab[index - 1].left + this.state.tab[index - 1].width;
      });
      this.state.tabWidth = this.state.tab[0].width;
    }, 600);
  },
  onPageScroll(e) {
    this.state.scrollPositions[this.state.tabValue] = e.scrollTop;
    const query = uni.createSelectorQuery().in(this);
    query.select(".reds-sticky-box").boundingClientRect(data => {
      if (data.top <= 0) {
        this.state.isSticky = true;
        Object.keys(this.state.scrollPositions).forEach(key => {
          if (key !== this.state.tabValue && this.state.scrollPositions[key] === 0) {
            this.state.scrollPositions[key] = this.state.scrollPositions[key] + 420;
          }
        });
      } else {
        this.state.isSticky = false;
      }
    }).exec();
  }
};
</script>

<style lang="scss" scoped>
.header {
  padding: 50px 15px 25px 15px;

  .user-info {
    display: flex;

    .avatar-box {
      position: relative;
      margin-right: 32rpx;

      .avatar-img {
        width: 160rpx;
        height: 160rpx;
        border-radius: 50%;
      }

      .upload-sign {
        font-size: 36rpx;
        line-height: 20rpx;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: #FAE93E;
        position: absolute;
        font-weight: 600;
        right: 12rpx;
        bottom: 6rpx;
        border-radius: 50%;
        width: 40rpx;
        height: 40rpx;
      }
    }
  }

  .name-row {
    display: flex;
    align-items: center;
    margin-bottom: 10rpx;

    .username {
      color: #EFFAFB;
      font-size: 30rpx;
      font-weight: 700;
      margin-right: 16rpx;
    }
  }

  .user-id {
    color: #90A4AB;
    font-size: 24rpx;
  }

  .signature {
    color: #BCC1C4;
    font-size: 28rpx;
    padding-top: 34rpx;
  }

  .sign {
    display: flex;
    padding-top: 20rpx;

    .gender-badge {
      display: flex;
      text-align: center;
      align-items: center;
      justify-content: center;
      background-color: #707F84;
      width: 34rpx;
      height: 32rpx;
      padding: 2rpx 10rpx;
      font-size: 20rpx;
      border-radius: 40rpx;
      color: #FF8597;
      text-align: center;
      line-height: 36rpx;
    }

    .level-badge {
      padding: 4rpx 16rpx;
      border-radius: 32rpx;
      font-size: 24rpx;
      color: #EFFAFB;
      background-color: #637376;
      margin-right: 12rpx;
    }
  }
}

.data-panel {
  display: flex;
  justify-content: space-between;
  font-size: 26rpx;
  padding: 50rpx 0;
  color: #BCC1C4;

  .user-data {
    display: flex;
    justify-content: space-between;
    gap: 50rpx;

    .user-number {
      color: #EFFAFB;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .user-label {
      font-size: 22rpx;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }

  .edit-btns {
    display: flex;

    .btn {
      padding: 0 32rpx;
      border-radius: 40rpx;
      color: #EFFAFB;
      border: 1rpx solid rgba(248, 251, 251, 0.296);
      background-color: #637376;
      display: flex;
      align-items: center;
    }
  }
}

.other-panel {
  font-size: 24rpx;
  color: #BCC1C4;
  display: flex;
  gap: 20rpx;

  .other-box {
    background-color: #637376;
    padding: 20rpx;
    border-radius: 16rpx;
    width: 200rpx;

    .other-label {
      font-size: 20rpx;
    }

    .other-title {
      color: #EFFAFB;
      padding-bottom: 6rpx;
    }
  }
}

.divider {
  width: 100%;
  height: 1px;
  background: rgba(255, 255, 255, 0.08);
  width: calc(100vw - 24px);
  margin: 0 12px;
}

.layout {
  background: linear-gradient(180deg, #0c1520 0%, #546366 40%);
}

.bottom-menu {
  position: fixed;
  bottom: 0;
  width: 100%;
  background: #000;
  display: block;
  height: 48px;
  padding-bottom: env(safe-area-inset-bottom);
}

.user-page {
  overflow-y: hidden;
  overflow-x: hidden;
  flex: 1;
}

.user-page-sticky .reds-sticky {
  padding: 16px 0 16px 2ch;
}

.reds-sticky {
  z-index: 5 !important;
  background: #0c1520;
}

.reds-tabs-list {
  display: flex;
  flex-wrap: nowrap;
  position: relative;
  font-size: 16px;
}

.sub-tab-list {
  justify-content: flex-start;
}

.reds-tab-item {
  display: flex;
  align-items: center;
  box-sizing: border-box;
  height: 40px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.8);
  white-space: nowrap;
  transition: transform .3s cubic-bezier(.2, 0, .25, 1);
  z-index: 1;
}

.reds-tab-item.active {
  font-weight: 600;
  color: #fff;
}

.active-tag {
  position: absolute;
  height: 40px;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.04);
  border-radius: 999px;
  pointer-events: none;
}

.feeds-tab-container {
  padding-left: 12px;
  background: #0c1520;
}

.transform-container {
  display: flex;
  will-change: transform;
  transform: translate(0, 0);
  transition: all 0.4s cubic-bezier(0.2, 0, 0.25, 1) 0s;
  gap: 12px;
}

.tab-content-item {
  padding-top: 1px;
  flex-shrink: 0;
}

.static-layout {
  gap: 16px;
  justify-content: center;
}

.feeds-container.static-layout {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.feeds-container {
  position: relative;
  margin: 0 auto;
}

.empty-container {
  margin-top: 72px;
}

.empty {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

.empty-icon {
  color: rgba(255, 255, 255, 0.3);
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.reds-icon {
  display: inline-block;
  vertical-align: middle;
  fill: currentColor;
}

.empty-text {
  font-size: 14px;
  line-height: 18px;
  text-align: center;
  color: rgba(255, 255, 255, 0.6);
  margin-top: 16px;
}

.loading {
  margin-top: 72px;
}

.feeds-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 64px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  visibility: hidden;
}

.sub-tab-list {
  padding: 16px 0;
}

.reds-tabs-list.left {
  justify-content: flex-start;
}

.note-item {
  position: absolute;
  left: 0;
  top: 0;
}

.cover {
  position: relative;
  display: flex;
  border-radius: 12px;
  overflow: hidden;
  outline: 1px solid rgba(255, 255, 255, 0.08);
  outline-offset: -1px;
  transition: background .2s;
  transform: translateZ(0);
}

.cover.ld::before {
  background: transparent;
  -webkit-backdrop-filter: blur(0);
  backdrop-filter: blur(0);
}

.cover::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.04);
  backdrop-filter: blur(42.5px);
  z-index: 1;
  transition: all 400ms;
}

img {
  border-style: none;
}

.cover::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: background-color .2s;
  background-color: transparent;
  -webkit-transform: translate3d(0, 0, 0);
}

.reds-sticky-box.sticky .reds-sticky {
  position: fixed;
  z-index: 10010;
  top: var(--stick-top);
  width: 100%;
  box-sizing: border-box;
}
</style>