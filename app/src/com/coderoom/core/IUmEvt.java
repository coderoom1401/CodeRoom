package com.coderoom.core;

// umeng自定义事件
public interface IUmEvt {

	String Splash = "tj_pv_splash";//启动页
	String Guide = "引导页";//新手引导页 登录按钮点击数、注册按钮点击数
	String Login = "登录页";//登录页进入 页面进入次数、返回按钮点击数、登录按钮点击数、注册按钮点击数、忘记密码按钮点击数
	String Reg = "注册页";//注册 返回，下一步，提交学校信息

	String Home = "首页";//我的课程页
	String HomeTab = "底部三个Tab";// 统计-首页Tab

	String Account = "个人中心";//个人中心
	String Msg = "消息";//消息页
	String MsgDetail = "消息详情";//消息详情页
	String setting = "设置";//设置页面
	String feedBack = "给我们提建议";//用户反馈页

}
