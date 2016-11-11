package com.coderoom.net;


import com.coderoom.common.IAutoParams;
import com.coderoom.db.dao.IUser;

import z.db.ShareDB;
import z.ext.base.ZGlobalMgr;
import z.http.ZHttpParams;
import z.util.MD5;

// 接口上下文
public class HttpIFCtx {
	private static final String Key = "HttpIFCtx";
	// 由外部初始化一次,不自动生成这个全局对象
	private String phoneType;
	private String deviceType;
	private String deviceId;
	private String appVer;
	private String systemVer;
	private String channel;
	private String clientId = "";

	public static HttpIFCtx instance() {
		HttpIFCtx ctx = ZGlobalMgr.getGlobalObj(Key);
		if (ctx==null) {
			ctx = new HttpIFCtx();
			ZGlobalMgr.setGlobalObj(Key,ctx);
		}
		return ctx;
	}

	public HttpIFCtx() {
		initPublicParams();
	}

	private void initPublicParams() {
		ShareDB.Sec sec = new ShareDB.Sec(IAutoParams.kSec);
		channel = sec.getString(IAutoParams.kVendor);
		appVer = sec.getString(IAutoParams.kVerS);
		systemVer = sec.getString(IAutoParams.kVerS);
		deviceId = sec.getString(IAutoParams.kDevId);
		phoneType = "android";
		systemVer = android.os.Build.VERSION.RELEASE;
		deviceType = android.os.Build.MODEL;
		String cid = sec.getString(IAutoParams.kGeTuiId);
		clientId = null == cid ? "" : cid;
	}

	public void addPublicParams(ZHttpParams params) {
		params.put("phoneType", phoneType);
		params.put("deviceType", deviceType);
		params.put("deviceId", deviceId);
		params.put("appVer", appVer);
		params.put("systemVer", systemVer);
		params.put("channel", channel);
		params.put("timestamp", System.currentTimeMillis() / 1000);
		params.put("token", IUser.Dao.getToken());
		params.remove("sign"); // 签名
		String sb = params.toOrderString().append("u6r4lj1yxgqmuc8h5r24a7qotiowx7gu").toString();
		String md5 = MD5.md5Lower(sb);
		params.put("sign", md5);

	}

	public static void saveCid(String cid) {
		if (cid == null) cid = "";
		ShareDB.Key.update(IAutoParams.kSec,IAutoParams.kGeTuiId,cid);
		HttpIFCtx ctx = ZGlobalMgr.getGlobalObj(Key);
		if (ctx != null) {
			if (!cid.equals(ctx.clientId)) {
				ctx.clientId = cid;
				// 重新发起登录
			}
		}
	}

	public static String addWebViewParams(String url) {
		HttpIFCtx ic = instance();
		StringBuilder sb = new StringBuilder(1024);
		sb.append(url);
		sb.append(url.contains("?")?'&':'?');
		sb.append("phoneType").append('=').append(ic.phoneType);
		sb.append('&').append("appVer").append('=').append(ic.appVer);
		sb.append('&').append("channel").append('=').append(ic.channel);
		sb.append('&').append("systemVer").append('=').append(ic.systemVer);
		return sb.toString();
	}
}
