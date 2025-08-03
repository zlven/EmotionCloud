<template>
  <view class="page">
    <!-- 头部Logo -->
    <view class="header">
	  <text class="pic1">🌠</text>
      <view class="logo">
        <image src="/static/login/logo.png" mode="aspectFill"></image>
      </view>
    </view>

    <!-- 注册表单 -->
    <view class="register-container">
      <view class="title">用户注册</view>

      <!-- 用户名输入 -->
      <view class="form-item">
        <view class="icon">
          <image src="/static/login/username.png" mode="aspectFill"></image>
        </view>
        <input
          v-model="formUsername"
          type="text"
          placeholder="请输入用户名（3-20位，支持字母/数字/下划线）"
          maxlength="20"
          :class="{ 'border-red': usernameError }"
        />
        <text v-if="usernameError" class="error-tip">用户名格式错误</text>
      </view>

      <!-- 密码输入 -->
      <view class="form-item">
        <view class="icon">
          <image src="/static/login/password.png" mode="aspectFill"></image>
        </view>
        <input
          v-model="formPassword"
          type="safe-password"
          placeholder="请输入密码（6-16位，支持特殊字符）"
          maxlength="16"
          :class="{ 'border-red': passwordError }"
        />
        <text v-if="passwordError" class="error-tip">密码格式错误</text>
      </view>

      <!-- 注册按钮 -->
      <view class="register-btn" :disabled="isSubmitting" @click="handleRegister">
        {{ isSubmitting ? '注册中...' : '立即注册' }}
      </view>

      <!-- 跳转登录链接 -->
      <view class="login-link" @click="goToLogin">
        已有账号？<text class="link-text">去登录</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      formUsername: '', // 用户名
      formPassword: '', // 密码
      usernameError: false, // 用户名校验错误状态
      passwordError: false, // 密码校验错误状态
      isSubmitting: false // 防止重复提交标志
    };
  },

  methods: {
    // 表单校验
    validateForm() {
      const usernameRegex = /^[\w]{3,20}$/; // 3-20位字母/数字/下划线
      const passwordRegex = /^[\w!@#$%^&*]{6,16}$/; // 6-16位，支持常见特殊字符

      // 用户名校验
      this.usernameError = !usernameRegex.test(this.formUsername);
      // 密码校验
      this.passwordError = !passwordRegex.test(this.formPassword);

      if (this.usernameError || this.passwordError) {
        return false; // 校验失败
      }
      return true; // 校验通过
    },
// 注册处理
async handleRegister() {
    if (this.isSubmitting) return; // 防止重复提交
    if (!this.validateForm()) return; // 表单校验

    this.isSubmitting = true; // 锁定按钮

    try {
        const response = await uni.request({
            url: 'http://localhost:8080/api/auth/register', // 后端注册接口
            method: 'POST',
            header: {
                'Content-Type': 'application/json'
            },
            data: {
                username: this.formUsername,
                password: this.formPassword
            },
            // 对 data 进行序列化，将对象转换为 key=value&key=value 的形式
            transformRequest: [function (data) {
                let ret = ''
                for (let it in data) {
                    ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
                }
                return ret.slice(0, -1)
            }]
        });

        if (response.data.code === 200) {
            uni.showToast({
                title: '注册成功！',
                icon: 'success',
                duration: 1500
            });
            // 注册成功后跳转到登录页
            uni.navigateTo({
                url: '/pages/auth/login/index' // 登录页路径，确保与项目路由一致
            });
        } else {
            uni.showToast({
                title: response.data.message,
                icon: 'none',
                duration: 2000
            });
        }
    } catch (error) {
        console.error('注册请求失败:', error);
        uni.showToast({
            title: '网络请求失败，请稍后再试',
            icon: 'none'
        });
    } finally {
        this.isSubmitting = false; // 解锁按钮
    }
},

    // 跳转到登录页
    goToLogin() {
      uni.navigateTo({
        url: '/pages/auth/login/index' // 确保路径正确，如：/pages/login/index
      });
    }
  }
};
</script>


<style lang="scss" scoped>
/* 页面整体样式 */
.page {
	min-height: 100vh;
	 overflow: hidden; // 禁止滚动
	//选择2
	background: #fff8e9; 
	//background: linear-gradient(to right, #b3c1ff, #f0f0ff); // 渐变色背景
	
	background-size: 150% 200%;
	position:relative;//背景相对定位
	z-index:1;

	
}

//下面那块
.header {
  width: 100%;
  text-align: center;
  margin-bottom: 80px;
  z-index:2;
  ///
  .pic1{
  	margin-top: 35px;
  	    margin-left: -66px;
  	    margin-right: 20px;
  	font-size: 100px;
  	position:absolute;
  	text-align: center;
  }
  
}

.logo image {
  width: 100px;
  height: 130px;//
  border-radius: 10px;
}

/* 注册容器 */
.register-container {
  width: 120%;
   height: 390px;
   margin-top:10px;
  max-width: 325px;
  background-color: #fff8e9;
  padding: 60px 25px;
  border-radius: 30px;
  border-top: 1px solid #550000;
}
//用户注册
.title {
  font-size: 26px;
  font-weight: 600;
  color: #550000;
  text-align: center;
  margin-top:-18px;
  margin-bottom: 75px;
}

/* 表单项样式 */
.form-item {
  display: flex;
  align-items: center;
  //输入框间隙
  margin-bottom: 25px;
}
//灰色输入框
.icon {
  width: 70px;
  height: 40px;
  margin-right: 30px;
}
//
.icon image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

input {
  flex: 1;
  padding: 15px 15px;
  background-color: #ffffff;
  border: 1px solid #ffffff;
  margin-right:15px;
  margin-left: -85px; /* 距离左侧像素 */
  margin-top: -70px; /* 距离上侧像素 */
  border-radius: 50px;
  font-size: 12px;
  outline: none;

  &.border-red {
    border-color: #aa0000;
  }
  &:focus-within {
      border-color: #9bb167; /* 聚焦时边框变绿色 */
     
  }
}

.error-tip {
  display: block;
  font-size: 12px;
  color: #55557f;
  margin-top: 8px;
  margin-left: 55px;
  line-height: 1.4;
}

/* 注册按钮 */
.register-btn {
  width: 100%;
  padding: 15px;
  width:260px;
  background-color: #610000;
  color: white;
  font-size: 16px;
  font-weight: 500;
  margin-top:-25px;
  margin-left: 18px; /* 距离左侧20像素 */
	  
  border-radius: 30px;
  text-align: center;
  cursor: pointer;
  transition: opacity 0.2s;
  border: none;
  outline: none;

  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }
}

/* 登录链接 */
.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;

  .link-text {
    color: #5555ff;
    font-weight: 500;
  }
}
</style>