<template>
	<!--加载网页图片 -->
	 <!-- <div class="emjoy-cloud">
	    <img src="https://www.canva.cn/design/DAGoWMBFwR8/optEabObDtyHopAVcNk-Yw/edit" alt="Canva Logo">
	  </div> -->
	
    <view class="page">
		<image></image>
		
		<!--星星-->	
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
		
        <!-- 修改点1：在header中添加APP名称 -->
        <view class="header">
            <view class="logo-container">
                <image class="logo" src="/static/login/logo.png" />
				<text class="pic">🌠</text>
                <text class="app-name">EMOCloud</text>
				<!-- 修改点2：在标题上方添加欢迎语 -->
				            <view class="welcome">
				                <text class="welcome-text"> 🖊 记 录 情 绪 的 小 助 手 ✈  </text>
				              </view>
            </view>
        </view>
		
        
        <view class="login">        
            <view class="title"><text>登录</text></view>

            <!-- 用户名密码登录表单 -->
            <view class="block">
                <view class="icon">
                    <image src="/static/login/mobile.png"/>
                </view>
                <view class="input">
                    <input v-model="formUsername" placeholder="📱 请输入情绪id" type="text"/>
                </view>
            </view>
            <view class="block">
                <view class="icon">
                    <image src="/static/login/lock.png"/>
                </view>
                <view class="input">
                    <input v-model="formPassword" placeholder="🔒 请输入密码" type="safe-password"/>
                </view>
            </view>

            <!-- 注册按钮 -->
            <view class="change-type">
                <text class="register-link" @click="goToRegister">立即注册</text>
            </view>

            <view class="button" @click="handleLogin"><text>登录</text></view>
        </view>
    </view>
</template>

<script>
export default {
	//<!--星星-
	  data() {
	     return {
	       stars: Array(20).fill().map(() => ({
	         symbol: ['⭐', '🌟', '✨', '★','☁'][Math.floor(Math.random() * 5)],
	         x: Math.random() * 100, // 横向随机分布（0-100vw）
	         y: Math.random() * 100, // 纵向随机分布（0-100vh）
	         size: Math.random() * 10 + 10, // 大小（10px-20px）
	         opacity: Math.random() * 0.5 + 0.3, // 透明度（0.3-0.8）
	         duration: Math.random() * 10 + 5, // 动画时长（5s-15s）
	         delay: Math.random() * 5, // 动画延迟（0s-5s）
	       })),
	     };
	   },
	   
	  
    data() {
        return {
            formUsername: '', // 变量名改为驼峰式（符合 Vue 规范）
            formPassword: ''
        };
    },
    methods: {
        /**
         * 登录函数（完善错误处理和响应解析）
         */
        async handleLogin() {
            // 表单验证
            if (!this.formUsername || !this.formPassword) {
                uni.showToast({
                    title: '用户名和密码不能为空',
                    icon: 'none'
                });
                return;
            }

            try {
                const response = await uni.request({
                    url: 'http://localhost:8080/api/auth/login',
                    method: 'POST',
                    header: {
                        'Content-Type': 'application/x-www-form-urlencoded' // 必须指定此格式
                    },
                    data: {
                        username: this.formUsername,
                        password: this.formPassword
                    }
                });

                // 处理响应（根据后端返回结构调整）
                if (response.statusCode === 200) {
                    const userDTO = response.data; // 后端返回的 UserDTO 对象
                    
                    // 假设 UserDTO 包含 token 和用户信息
                    if (userDTO) {
                        // 存储用户信息和 Token
                        uni.setStorageSync('userInfo', userDTO);
                        uni.setStorageSync('token', userDTO.token || ''); // 假设 token 在 UserDTO 中
                        
                        uni.showToast({
                            title: '登录成功',
                            icon: 'success'
                        });
                        // 跳转到首页（请根据实际路径修改）
                        uni.switchTab({ url: '/pages/home/index' });
                    } else {
                        uni.showToast({
                            title: '登录失败，未获取到用户信息',
                            icon: 'none'
                        });
                    }
                } else if (response.statusCode === 401) {
                    // 未授权（用户名或密码错误）
                    uni.showToast({
                        title: '用户名或密码错误',
                        icon: 'none'
                    });
                } else {
                    // 其他错误
                    uni.showToast({
                        title: response.data?.message || '登录失败，请稍后再试',
                        icon: 'none'
                    });
                }
            } catch (error) {
                console.error('登录请求异常', error);
                uni.showToast({
                    title: '网络错误，请检查网络连接',
                    icon: 'none'
                });
            }
        },

        /**
         * 跳转到注册页
         */
        goToRegister() {
            uni.navigateTo({ 
                url: '/pages/auth/register/index' // 请确认注册页实际路径
            });
        }
    }
};
</script>

<style lang="scss" scoped>
    @import "./index.scss";
    .register-link {
        text-align: center;
        margin: 15px 0;
        color: #007aff;
        font-size: 14px;
        cursor: pointer;
        display: block;
    }
    .button {
        margin-top: 30px;
        background-color: #007aff;
        color: white;
        border-radius: 8px;
        padding: 16px 0;
    }
    .button text {
        font-size: 16px;
    }
</style>