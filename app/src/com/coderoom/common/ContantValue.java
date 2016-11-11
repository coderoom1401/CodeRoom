package com.coderoom.common;


//接口地址
public interface ContantValue {
	String URL = GlobaleParms.URL;

	//登录注册相关
	/** 登录 */
	String F_LOGIN = URL + "/Logins";
	/** 添加 */
	String F_ADD = URL + "/adds";
	/** 查询 */
	String F_QUERY = URL + "/query";
	/** 登出 */
	String F_LOGOUT = URL + "/LogOut";

}
