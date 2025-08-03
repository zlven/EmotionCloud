<template>
	<view class="page">
		<view class="user">
			<image class="avatar" src="/static/center/avatar.png"></image>
			<view class="name">
				<text>姓名</text>
				<text>ip</text>
			</view>
			<image class="right" src="/static/center/right.png"></image>
		</view>
		
		<view class="card">
			<view class="important">
				<view class="item">
					<image :src="'/static/center/wallet.png'"></image>
					<text>情绪日志</text>
				</view>
				<view class="item">
					<image :src="'/static/center/collect.png'"></image>
					<text>我的收藏</text>
				</view>
				<view class="item">
					<image :src="'/static/center/order.png'"></image>
					<text>浏览记录</text>
				</view>
			</view>
		</view>
		
		<view class="card">
			<view class="menu">
				<view class="item" v-for="v in menus1" :key="v.id">
					<image :src="v.icon"></image>
					<text>{{v.name}}</text>
					<image class="arrow" v-if="v.arrow" src="/static/center/right.png"></image>
				</view>
			</view>
		</view>
		
		<view class="card">
			<view class="menu">
				<view class="item" v-for="v in menus2" :key="v.id">
					<image :src="v.icon"></image>
					<text>{{v.name}}</text>
					<image class="arrow" v-if="v.arrow" src="/static/center/right.png"></image>
				</view>
			</view>
		</view>
	</view>
</template>


<script>
	export default {
		data() {
			return {
				menus1: [
					{id: 1, name: '个人资料', icon: '/static/center/idcard.png', arrow: true},
					{id: 2, name: '我的咨询', icon: '/static/center/comment.png', arrow: true},
					{id: 3, name: '我的消息', icon: '/static/center/bell.png', arrow: true}
				],
				menus2: [
					{id: 4, name: 'BUG反馈', icon: '/static/center/bug.png', arrow: true},
					{id: 5, name: '联系客服', icon: '/static/center/whatsapp.png', arrow: true},
					{id: 6, name: '系统设置', icon: '/static/center/setting.png', arrow: true},
				]
			}
		}
	}
</script>

<style lang="scss">
	@import "./index.scss";
</style>