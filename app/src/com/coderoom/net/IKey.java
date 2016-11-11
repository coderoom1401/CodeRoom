package com.coderoom.net;

public interface IKey {
	// _timestamp
	String kTimestamp = "_timestamp";

	int AppCodeB = 10000; // 应用层错误码
    String CODE = "code";
    String MSG = "msg";
    String RES = "data";

	String FMTERR = "服务器返回错误";
	String UIDERR = "服务器ID为空,请重新登录";

	int ELoginError = 10101;// 账号或密码错误，请重新登录
	int ENoLogin = 10102;// 未登录错误码
	int ELoginTimeout = 10103;// Token过期错误码

	//SSO相关错误码
	int Acc51talk = 1;//用户验证51talk密码成功通过（用户只会遇到一次哦），code: 1
	int SsoError = 10270;//SSO服务异常， code: 270
	int AccAlready = 271;//用户注册的时候，帐号已经在51talk存在了
	int PasswordError = 272;//验证51talk的密码错误,code: 272
	int RePasswordError = 10273;//重置手机密码失败，code: 273
	int NotVerifiedError = 275;//登录时侦测到用户手机号码尚未验证， code: 275
}
