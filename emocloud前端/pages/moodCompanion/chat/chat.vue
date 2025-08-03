<template>
    <view class="chat">
        <!-- 顶部标题 -->
        <view class="topTabbar">
            <!-- 新增返回按钮区域 -->
            <u-icon class="return-icon" name="arrow-left" size="20px" color="#00ffff" @click="goback()"></u-icon>
            <!-- 修正点击事件绑定 -->
            <button class="custom-button" @click="customButtonClickHandler">🔙</button>
            <!-- 标题 -->
            <view class="text">今天是什么情绪呢</view>
			  <!-- 总结对话按钮 -->
			<button class="summary-btn" @click="summarizeDialogue">总结对话</button>
        </view>
        <scroll-view :style="{ height: `${windowHeight - inputHeight - 180}rpx` }"
                     id="scrollview"
                     scroll-y="true"
                     :scroll-top="scrollTop"
                     class="scroll-view">
            <!-- 聊天主体 -->
            <view id="msglistview" class="chat-body">
                <!-- 聊天记录 -->
                <view v-for="(item, index) in msgList" :key="index">
                    <!-- 自己发的消息 -->
                    <view class="item self" v-if="item.userContent != ''">
                        <!-- 文字内容 -->
                        <view class="content right">
                            {{ item.userContent }}
                        </view>
                        <!-- 头像 -->
                        <image class="avatar" :src="item.image">
                        </image>
                    </view>
                    <!-- 机器人发的消息 -->
                    <view class="item Ai" v-if="item.botContent != ''">
                        <!-- 头像 -->
                        <image class="avatar" :src="item.image">
                        </image>
                        <!-- 文字内容 -->
                        <view class="content left">
                            {{ item.botContent }}
                        </view>
                    </view>
                </view>
            </view>
        </scroll-view>
        <!-- 底部消息发送栏 -->
        <!-- 用来占位，防止聊天消息被发送框遮挡 -->
        <view class="chat-bottom" :style="{ height: `${inputHeight}rpx` }">
            <view class="send-msg" :style="{ bottom: `${keyboardHeight - 60}rpx` }">
                <view class="uni-textarea">
                    <textarea v-model="chatMsg"
                              maxlength="300"
                              confirm-type="send"
                              @confirm="handleSend"
                              placeholder="快来聊天吧~"
                              :show-confirm-bar="false"
                              :adjust-position="false"
                              @linechange="sendHeight"
                              @focus="focus"
                              @blur="blur"
                              auto-height></textarea>
                </view>
                <button @click="handleSend" class="send-btn" :class="{ 'loading': isLoading }" :disabled="isLoading">
                    {{ isLoading ? '发送中...' : '发送' }}
                </button>
            </view>
        </view>
		  <!-- ==================== 新增的总结弹窗HTML ==================== -->
		        <view class="summary-popup" v-if="showSummaryPopup">
		            <view style="width: 90%; background-color: #fff; border-radius: 16rpx;">
		                <view class="popup-header">
		                    <view class="popup-title">对话总结</view>
		                    <!-- 添加一个关闭按钮 -->
		                    <u-icon name="close" size="20" @click="showSummaryPopup = false"></u-icon>
		                </view>
		                <view class="summary-content">
		                    <textarea v-model="dialogueSummary" 
		                              placeholder="总结内容加载中..." 
		                              maxlength="-1" 
		                              style="background-color: #f5f5f5; width: 100%; height: 300rpx; padding: 20rpx; box-sizing: border-box;"></textarea>
		                </view>
		                <view class="popup-footer">
		                    <button class="save-btn" @click="saveSummaryToJournal">保存到日记本</button>
		                </view>
		            </view>
		        </view>
		        <!-- ==================== 新增代码结束 ==================== -->
		
    </view>
	
	<GlobalPet />
</template>
<script>
export default {
    data() {
        return {
            keyboardHeight: 0,
            bottomHeight: 0,
            scrollTop: 0,
            userId: '',
            dialogueId: null,
            chatMsg: "",
            msgList: [
                {
                    botContent: "你好啊，很高兴为你服务！请问有什么可以帮助你的吗？",
                    userContent: "",
                    image: "/static/common/unname1.jpeg"
                }
            ],
            isLoading: false,
            isDialogueLoading: false,
            apiBaseUrl: 'http://localhost:8080/companion',
            showSummaryPopup: false,
            dialogueSummary: '',
            isSummaryLoading: false,
            scrollViewHeight: 0  // 聊天区域高度
        }
    },
    updated() {
        this.scrollToBottom();
    },
    computed: {
        windowHeight() {
            return this.rpxTopx(uni.getSystemInfoSync().windowHeight);
        },
        inputHeight() {
            return this.bottomHeight + this.keyboardHeight;
        },
        adjustedInputHeight() {
            return Math.max(this.bottomHeight + this.keyboardHeight, 150);
        }
    },
    onLoad() {
        uni.hideTabBar();
        
        // 键盘监听
        if (typeof uni.onKeyboardHeightChange === 'function') {
            uni.onKeyboardHeightChange(res => {
                this.keyboardHeight = this.rpxTopx(res.height);
                this.calculateScrollViewHeight(); // 键盘变化时重新计算高度
            });
        }
        
        const userInfo = uni.getStorageSync('userInfo');
        this.userId = userInfo && userInfo.id ? userInfo.id : null;
        
        if (!this.userId) {
            uni.showToast({ title: '请先登录', icon: 'none' });
            uni.navigateTo({ url: '/pages/auth/login/index' });
            return;
        }
        
        this.calculateScrollViewHeight(); // 初始化高度计算
        this.loadUserDialogue();
    },
    methods: {
        // 计算聊天区域高度（无原生导航栏）
        calculateScrollViewHeight() {
            const systemInfo = uni.getSystemInfoSync();
            const windowHeightRpx = systemInfo.windowHeight * (750 / systemInfo.windowWidth);
            
            // 无原生导航栏时，只扣除自定义顶部栏(90rpx)和底部输入区域
            const topBarHeight = 90; // 自定义顶部栏高度
            const extraSpace = 30; // 微调空间，可根据实际调整
            
            this.scrollViewHeight = windowHeightRpx - topBarHeight - this.adjustedInputHeight - extraSpace;
        },
        loadUserDialogue() {
			
            this.isDialogueLoading = true;
            uni.showLoading({ title: '加载对话中...' });
            
            this.dialogueId = uni.getStorageSync(`user_dialogue_${this.userId}`);
            console.log('【本地存储】获取对话ID:', this.dialogueId);
            
            if (this.dialogueId) {
                this.loadDialogueMessages(this.dialogueId);
                uni.hideLoading();
                this.isDialogueLoading = false;
                return;
            }
            
            uni.request({
                url: `${this.apiBaseUrl}/users/${this.userId}/dialogues`,
                method: 'GET',
                header: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${uni.getStorageSync('token')}`
                },
                success: (res) => {
                    console.log('【对话API】响应状态:', res.statusCode);
                    console.log('【对话API】响应数据:', res.data);
                    
                    if (res.statusCode === 200 && res.data && res.data.content && res.data.content.length > 0) {
                        this.dialogueId = res.data.content[0].id;
                        uni.setStorageSync(`user_dialogue_${this.userId}`, this.dialogueId);
                        console.log('【成功】对话ID:', this.dialogueId);
                        
                        uni.showToast({ title: '对话加载成功', icon: 'success' });
                        this.loadDialogueMessages(this.dialogueId);
                    } else {
                        uni.showToast({ 
                            title: res.data?.message || '未找到对话，请确认用户已创建对话', 
                            icon: 'none' 
                        });
                    }
                },
                fail: (err) => {
                    console.error('【对话API】请求失败:', err);
                    uni.showToast({ title: '网络错误，请检查网络', icon: 'none' });
                },
                complete: () => {
                    uni.hideLoading();
                    this.isDialogueLoading = false;
                }
            });
        },
        loadDialogueMessages(dialogueId) {
            const token = uni.getStorageSync('token');
            console.log('当前token:', token);
            uni.request({
                url: `${this.apiBaseUrl}/dialogues/${dialogueId}/messages`,
                method: 'GET',
                header: {
                    'Authorization': `Bearer ${uni.getStorageSync('token')}`
                },
                success: (res) => {
				 // 1. 首先，检查请求是否成功，并且返回的数据是有效的
				            if (res.statusCode === 200 && res.data && res.data.content) {
				                
				                // 2. 使用 Array.map() 方法遍历后端返回的 `content` 数组
				                //    对每一个 messageDTO 对象，都执行一次转换
				                const transformedMessages = res.data.content.map(messageDTO => {
				                    
				                    // 3. 判断消息发送者类型
				                    if (messageDTO.senderType === 'USER') {
				                        // 如果是用户，则返回符合前端用户消息格式的对象
				                        return {
				                            userContent: messageDTO.content, // 内容来自 messageDTO.content
				                            botContent: "",                  // 机器人内容为空
				                            image: "/static/common/unname2.jpg" // 你的用户头像路径
				                        };
				                    } else { 
				                        // 如果不是用户（我们假设就是机器人 'BOT'），则返回机器人消息格式的对象
				                        return {
				                            userContent: "",                 // 用户内容为空
				                            botContent: messageDTO.content,  // 内容来自 messageDTO.content
				                            image: "/static/common/unname1.jpeg" // 你的机器人头像路径
				                        };
				                    }
				                });
				
				                // 4. 注意：后端分页返回的数据通常是按时间倒序的（最新的在最前面）
				                //    而聊天记录需要按时间正序显示（最旧的在最前面），所以我们需要反转数组
				                const orderedMessages = transformedMessages.reverse();
				
				                // 5. 将转换并排序后的消息列表赋值给 this.msgList
				                //    这里我们不再保留那个写死的欢迎语，而是完全显示真实的历史记录
				                //    如果您还想保留欢迎语，可以这样写：
				                //    this.msgList = [ this.msgList[0], ...orderedMessages ];
				                this.msgList = orderedMessages;
				
				                // 6. 滚动到底部，显示最新的消息
				                this.scrollToBottom();
				
				            } else {
				                // 如果请求失败或没有内容，可以给一个提示
				                uni.showToast({
				                    title: '加载历史消息失败',
				                    icon: 'none'
				                });
				            }
				        },
				        fail: (err) => {
				            console.error('请求历史消息失败', err);
				            uni.showToast({
				                title: '网络错误，请稍后重试',
				                icon: 'none'
				            });
				        }
				    });
				},
        
        goback() {
            uni.switchTab({
                url: "/pages/home/index"
            })
            uni.showTabBar();
        },
        focus() {
            this.scrollToBottom()
        },
        blur() {
            this.scrollToBottom()
        },
        // px 转换成 rpx
        rpxTopx(px) {
            let deviceWidth = uni.getSystemInfoSync().windowWidth
            let rpx = (750 / deviceWidth) * Number(px)
            return Math.floor(rpx)
        },
        // 监视聊天发送栏高度
        sendHeight() {
            setTimeout(() => {
                let query = uni.createSelectorQuery();
                query.select('.send-msg').boundingClientRect()
                query.exec(res => {
                    this.bottomHeight = this.rpxTopx(res[0].height)
                })
            }, 10)
        },
        // 滚动至聊天底部
        scrollToBottom(e) {
            setTimeout(() => {
                let query = uni.createSelectorQuery().in(this);
                query.select('#scrollview').boundingClientRect();
                query.select('#msglistview').boundingClientRect();
                query.exec((res) => {
                    if (res[1].height > res[0].height) {
                        this.scrollTop = this.rpxTopx(res[1].height - res[0].height)
                    }
                })
            }, 15)
        },
        // 发送消息
       handleSend() {
           console.log('handleSend() 调用时的状态:');
           console.log('dialogueId:', this.dialogueId);
           console.log('本地存储对话ID:', uni.getStorageSync(`user_dialogue_${this.userId}`));
           console.log('isDialogueLoading:', this.isDialogueLoading);
           
           if (!this.dialogueId) {
               uni.showToast({ title: '对话ID不存在，请重试', icon: 'none' });
               return;
           }
           
           // 如果消息不为空
           if (!this.chatMsg || /^\s+$/.test(this.chatMsg)) {
               uni.showToast({
                   title: '不能发送空白消息',
                   icon: 'none'
               })
               return;
           }
           
           this.isLoading = true;
           const messageData = {
               dialogueId: this.dialogueId,
               senderType: 'USER',
               content: this.chatMsg,
               contentType: 'TEXT',
               emotionScore: 0.5, // 假设情绪分数，实际从其他界面获取
               functionCall: ''
           };
           
           uni.request({
               url: `${this.apiBaseUrl}/messages/with-response`,
               method: 'POST',
               data: messageData,
               header: {
                   'Content-Type': 'application/json'
               },
               success: (res) => {
                   if (res.statusCode === 201) {
                       // 添加用户消息
                       const userMsg = {
                           botContent: "",
                           userContent: this.chatMsg,
                           image: "/static/common/unname2.jpg"
                       };
                       this.msgList.push(userMsg);
                       
                       // 添加AI回复
                       const aiMsg = {
                           botContent: res.data.content,
                           userContent: "",
                           image: "/static/common/unname1.jpeg"
                       };
                       this.msgList.push(aiMsg);
                       
                       this.chatMsg = '';
                       this.scrollToBottom();
                   } else {
                       uni.showToast({
                           title: '获取回复失败，请重试',
                           icon: 'none'
                       });
                   }
               },
               fail: (err) => {
                   uni.showToast({
                       title: '网络错误，请检查网络连接',
                       icon: 'none'
                   });
                   console.error('发送消息失败', err);
               },
               complete: () => {
                   this.isLoading = false;
               }
           });
       },
        customButtonClickHandler() {
            // 使用 this 调用 goback 方法
            this.goback();
        },
		
		 summarizeDialogue() {
		        if (!this.dialogueId) {
		            uni.showToast({ title: '对话ID不存在，请重试', icon: 'none' });
		            return;
		        }
		        // console.log('token:', uni.getStorageSync('token'));
		        this.isSummaryLoading = true;
		        uni.request({
		            url: `${this.apiBaseUrl}/dialogues/${this.dialogueId}/summary`,
		            method: 'GET',
		            header: {
		                'Authorization': `Bearer ${uni.getStorageSync('token')}`
		            },
		            success: (res) => {
		                this.isSummaryLoading = false;
		                if (res.statusCode === 200) {
		                    this.dialogueSummary = res.data;
		                    this.showSummaryPopup = true;
		                } else {
		                    uni.showToast({
		                        title: '获取对话总结失败，请重试',
		                        icon: 'none'
		                    });
		                }
		            },
		            fail: (err) => {
		                this.isSummaryLoading = false;
		                console.error('获取对话总结失败', err);
		                uni.showToast({
		                    title: '网络错误，请检查网络连接',
		                    icon: 'none'
		                });
		            }
		        });
		    },
		    
		saveSummaryToJournal() {
        if (!this.dialogueSummary.trim()) {
            uni.showToast({ title: '总结内容为空，无法保存', icon: 'none' });
            return;
        }

        const userId = uni.getStorageSync('userInfo')?.id;
        if (!userId) {
            uni.showToast({ title: '请先登录', icon: 'none' });
            return;
        }

        uni.showLoading({ title: '正在检查日记...' });

        // 步骤1: 调用新接口，检查当天是否已有日记
        uni.request({
            url: `http://localhost:8080/emotion-log/diary/user/${userId}/today`,
            method: 'GET',
            header: {
                Authorization: `Bearer ${uni.getStorageSync('token')}`
            },
            success: (res) => {
                uni.hideLoading();

                // 步骤2: 根据响应状态码判断
                if (res.statusCode === 200) {
                    // 状态码200，说明找到了当天的日记
                    const existingDiary = res.data;
                    this.appendToExistingDiary(existingDiary);
                } else if (res.statusCode === 404) {
                    // 状态码404，说明当天没有日记
                    this.navigateToCreateDiary();
                } else {
                    // 其他错误
                    uni.showToast({ title: '检查日记失败，请重试', icon: 'none' });
                }
            },
            fail: (err) => {
                uni.hideLoading();
                uni.showToast({ title: '网络错误，请稍后重试', icon: 'none' });
            }
        });
    },

    // 新增方法: 追加内容到已存在的日记
    appendToExistingDiary(diary) {
        const newContent = diary.content + 
                           '\n\n--- AI对话总结 ---\n' + 
                           this.dialogueSummary;

        uni.showLoading({ title: '正在追加内容...' });

        uni.request({
            url: `http://localhost:8080/emotion-log/diary/edit/${diary.id}`,
            method: 'PUT',
            header: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${uni.getStorageSync('token')}`
            },
            data: {
                // 只发送需要更新的字段
                content: newContent
            },
            success: (res) => {
                uni.hideLoading();
                if (res.statusCode === 200) {
                    uni.showToast({ title: '已追加到今日日记', icon: 'success' });
                    this.showSummaryPopup = false;
                } else {
                    uni.showToast({ title: '追加失败，请重试', icon: 'none' });
                }
            },
            fail: (err) => {
                uni.hideLoading();
                uni.showToast({ title: '网络错误', icon: 'none' });
            }
        });
    },

    // 新增方法: 跳转到新建日记页面并传递参数
    navigateToCreateDiary() {
        // 1. 准备要传递的标题和内容
        const title = `对话总结 ${new Date().toLocaleDateString()}`;
        const content = this.dialogueSummary;

        // 2. 使用 encodeURIComponent 对参数进行编码
        // 这是为了防止URL中出现像 '&', '?', '/' 等特殊字符，导致URL解析错误
        const encodedTitle = encodeURIComponent(title);
        const encodedContent = encodeURIComponent(content);

        // 3. 拼接成 uni-app 标准的带参URL
        const url = `/pages/moodJournal/diary/Ddetail?title=${encodedTitle}&content=${encodedContent}`;
        
        console.log('【聊天页】即将跳转到:', url); // 添加调试日志

        // 4. 执行跳转
        uni.navigateTo({
            url: url,
            success: () => {
                console.log('【聊天页】跳转成功');
                // 跳转成功后，关闭聊天页的总结弹窗
                this.showSummaryPopup = false;
            },
            fail: (err) => {
                console.error('【聊天页】跳转失败', err);
                uni.showToast({
                    title: '页面跳转失败',
                    icon: 'none'
                });
            }
        });
    }
}
}
</script>

<style lang="scss" scoped>
$chatContentbgc: #C2DCFF;
/*用户头像？*/
$sendBtnbgc: #55557f;
/*AI的头像（这里用机器人emoji，也可换图标）*/

view, button, text, input, textarea {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 聊天消息 */
.chat {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;

  .topTabbar {
    width: 100%;
    height: 90rpx;
    line-height: 90rpx;
    display: flex;
    margin-top: 0rpx;
    justify-content: space-between;

    .return-icon {
      margin-left: 20rpx;
    }

    .text {
      margin: auto;
      font-size: 16px;
      font-weight: 700;
    }

    .button {
      width: 10%;
      margin: auto 20rpx auto 0rpx;
    }
  }

  .scroll-view {
    flex: 1;
    width: 100%;
    background: linear-gradient(to right, #ffe4b4, #CCE5FF);
    background-size: 150% 200%;
    overflow-y: scroll;

    ::-webkit-scrollbar {
      display: none;
      width: 0 !important;
      height: 0 !important;
      -webkit-appearance: none;
      background: transparent;
      color: transparent;
    }

    .chat-body {
      display: flex;
      flex-direction: column;
      padding-top: 23rpx;

      .self {
        justify-content: flex-end;
      }

      .item {
        display: flex;
        padding: 30rpx 30rpx;

        .right {
          background-color: $chatContentbgc;
        }

        /* 方形聊天框 */
        .left {
          background-color: #ffffff;
        }

        // 聊天消息的三角形
        .right::after {
          position: absolute;
          display: inline-block;
          content: '';
          width: 0;
          height: 0;
          left: 100%;
          top: 10px;
          border: 12rpx solid transparent;
          border-left: 12rpx solid $chatContentbgc;
        }

        // 聊天消息的三角形
        .left::after {
          position: absolute;
          display: inline-block;
          content: '';
          width: 0;
          height: 0;
          top: 10px;
          right: 100%;
          border: 12rpx solid transparent;
          border-right: 12rpx solid #ffffff;
        }

        // 消息内容样式
        .content {
          position: relative;
          max-width: 486rpx;
          border-radius: 19rpx;
          word-wrap: break-word;
          padding: 24rpx 24rpx;
          margin: 0 24rpx;
          border-radius: 5px;
          font-size: 28rpx;
          font-family: PingFang SC;
          font-weight: 500;
          color: #333333;
          line-height: 42rpx;
        }

        // 头像样式（AI 用机器人 emoji，用户头像保留原逻辑）
        .avatar {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 78rpx;
          height: 78rpx;
          border-radius: 50rpx;
          overflow: hidden;

          &::before {
            content: "👤";
            font-size: 60rpx;
            color: #fff;
          }

          image {
            display: none;
            align-self: center;
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }
      }
    }
  }

  /* 底部聊天发送栏 */
  .chat-bottom {
    width: 100%;
    background: transparent;
    transition: all 0.1s ease;

    .send-msg {
      display: flex;
      align-items: flex-end;
      padding: 30rpx 30rpx;
      width: 100%;
      min-height: 177rpx;
      position: fixed;
      bottom: 0;
      background: #eef6ff;
      transition: all 0.1s ease;
    }

    .uni-textarea {
      padding-bottom: 70rpx;
      textarea {
        width: 537rpx;
        min-height: 75rpx;
        max-height: 500rpx;
        background: #f1f1f1;
        border-radius: 20rpx;
        font-size: 32rpx;
        font-family: PingFang SC;
        color: #333333;
        line-height: 74rpx;
        padding: 5rpx 8rpx;
        text-indent: 30rpx;
      }
    }

    // 发送按钮
    .send-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 76rpx;
      margin-left: 25rpx;
      width: 120rpx;
      height: 75rpx;
      background: #55557f;
      border-radius: 20rpx;
      font-size: 28rpx;
      font-family: PingFang SC;
      font-weight: 500;
      color: #FFFFFF;
      line-height: 28rpx;
    }

    /// 发送中样式
    .send-btn.loading {
      background-color: #550000;
      color: #fff;
      cursor: not-allowed;
    }
  }
}

/// 返回按钮样式
.custom-button {
  background-color: #aaaaff;
  color: white;
  width: 120rpx;
  padding: 1rpx 30rpx;
  text-align: center;
  border-radius: 25rpx;
}

/// 总结按钮等样式
.chat {
  .topTabbar {
    justify-content: space-between;
    // 总结按钮
    .summary-btn {
      background-color: #aaaaff;
      color: #ffffff;
      border: none;
      border-radius: 19rpx;
      padding: 13rpx 19rpx;
      font-size: 24rpx;
      margin-left: 1rpx;
    }
  }

  /* 总结弹窗样式 */
  .summary-popup {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 1000;

    .popup-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 30rpx;
      background-color: #fff;
      border-top-left-radius: 16rpx;
      border-top-right-radius: 16rpx;

      .popup-title {
        font-size: 32rpx;
        font-weight: 500;
      }
    }

    .summary-content {
      width: 90%;
      background-color: #fff;
      margin-top: -10rpx;
      padding: 20rpx;

      textarea {
        width: 100%;
        min-height: 300rpx;
        padding: 20rpx;
        border: none;
        background-color: #f5f5f5;
        border-radius: 8rpx;
        font-size: 28rpx;
        line-height: 1.5;
        resize: none;
      }
    }

    .popup-footer {
      width: 90%;
      background-color: #fff;
      padding: 20rpx 0;
      border-bottom-left-radius: 16rpx;
      border-bottom-right-radius: 16rpx;

      .save-btn {
        width: 100%;
        background-color: #aa55ff;
        color: #fff;
        border: none;
        border-radius: 38rpx;
        padding: 16rpx 0;
        font-size: 28rpx;
      }
    }
  }
}
</style>