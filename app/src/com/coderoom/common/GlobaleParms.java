package com.coderoom.common;


import com.coderoom.core.FileCenter;

/** 说明：全局的变量 */
public class GlobaleParms {
	/**  创建数据库所有表的名称 */
	public static  String TABLE_USERINFO = "userInfo";
	public static  String TABLE_MSG = "msg";

	/** 评分系统相关 */
	public static double mNoiseLevel = 3.0;  //评分时降噪级别，越大过滤杂音越多，反之越少
	
	// coderoom.cfg ==> {"url":"http://","debug":true}
	// 调试开关
	public static final boolean isDebug = FileCenter.getJsonKey(FileCenter.loadCfg(), "debug", true);
//	public static final boolean isDebug = true;

	/** 线上环境*/
//	public static final String BaseURL = "http://";
	/** 预上线环境*/
//	public static final String BaseURL = "http://";
	/** 测试线环境 */
//	public static final String BaseURL = "http://";
	public static String BaseURL = FileCenter.getJsonKey(FileCenter.loadCfg(), "url", "http://androidblog.cn/admin.php");

	public static final String URL = BaseURL+"/Car";

}
