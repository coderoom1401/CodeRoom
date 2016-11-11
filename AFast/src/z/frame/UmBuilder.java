package z.frame;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.talk51.afast.utils.ToastUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMusic;

// 几种类型: 1.纯文本(+logo) 2.纯图 3.文本+图 4.音乐链接
public class UmBuilder implements View.OnClickListener, UMShareListener, ICommon {
	// 初始化SDK
	static {
		Config.OpenEditor = true;//是否开启分享编辑页
//      Log.LOG ＝ false;//关闭log
		Config.IsToastTip = false;//关闭toast

		//微信 appid appsecret
		PlatformConfig.setWeixin(app.mWXAppId, app.mWXAppSecret);
		//新浪微博 appkey appsecret
		PlatformConfig.setSinaWeibo(app.mWBAppId, app.mWBAppKey);
		// QQ和Qzone appid appkey
		PlatformConfig.setQQZone(app.mQQAppId, app.mQQAppKey);
	}
	private BaseFragment mBf; // 界面
	private Object mImage; // 图片 bitmap,url String
	private String mText; // 文本
	private String mTitle; // 标题
	private String mUrl; // 点击的url
	private String mMusicUrl; // 歌曲的url
	private String mOk = "分享成功"; // 成功文案
	private String mCancel = "取消分享"; // 取消文案
	private String mFail = "分享失败"; // 失败文案
	private int mType = 0;
	private int mLayoutID = 0;
	private FragmentActivity mAct; //
	private UMShareAPI mShareAPI = null;
	private ShareAction mShareAct;
	/**
	 * 判断微信是否安装
	 *
	 * @param context
	 * @return
	 */
	public static boolean isWXAppInstalledAndSupported(Context context) {
		boolean bSupported = false;
		try {
			IWXAPI weixinAPI = WXAPIFactory.createWXAPI(context, app.mWXAppId, false);
			if (weixinAPI != null) {
				bSupported = weixinAPI.isWXAppInstalled() && weixinAPI.isWXAppSupportAPI();
				weixinAPI.detach();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bSupported;
	}
	public UmBuilder clear() {
		mImage = null;
		mType = 0;
		mText = null;
		mUrl = null;
			mMusicUrl = null;
		return this;
	}
	public UmBuilder(int type) {
		mType = type;
	}
	public UmBuilder fragment(BaseFragment f) {
		mBf = f;
		return this;
	}
	public UmBuilder layout(int layoutID) {
		mLayoutID = layoutID;
		return this;
	}
	public UmBuilder image(Bitmap image) {
		mImage = image;
		return this;
	}
	public UmBuilder image(String url) {
		mImage = url;
		return this;
	}
	public UmBuilder content(String txt) {
		mText = txt;
		return this;
	}
	public UmBuilder title(String txt) {
		mTitle = txt;
		return this;
	}
	public UmBuilder musicUrl(String murl) {
		mMusicUrl = murl;
		return this;
	}
	public UmBuilder url(String url) {
		mUrl = url;
		return this;
	}
	public UmBuilder type(int t) {
		mType = t;
		return this;
	}
	public UmBuilder tips(String ok, String cancel, String fail) {
		mOk = ok;
		mCancel = cancel;
		mFail = fail;
		return this;
	}
	// 开始分享
	public void show() {
		if (mBf == null) return;
		mAct = mBf.getActivity();
		if (mAct == null) return;
		Dialog dlg = mBf.showDlg(ID_SHARE, shareView(mAct), true, app.Loading_Style);
		if (dlg != null) {
			//设置Dialog充满全屏
			Window win = dlg.getWindow();
			win.getDecorView().setPadding(0, 0, 0, 0);
			WindowManager.LayoutParams lp = win.getAttributes();
			lp.width = WindowManager.LayoutParams.FILL_PARENT;
			lp.height = WindowManager.LayoutParams.FILL_PARENT;
			win.setAttributes(lp);
		}
	}
	// 直接分享
	public void share(SHARE_MEDIA plat) {
		mAct = mBf.getActivity();
		initApi();
		cancel();
		if (begin(plat)) {
			if (plat == SHARE_MEDIA.WEIXIN || plat == SHARE_MEDIA.WEIXIN_CIRCLE) {
				// 微信及朋友圈
				media(mType != ImageOnly, mType != TextOnly, mType != ImageOnly);
				if (plat == SHARE_MEDIA.WEIXIN_CIRCLE && mType!=ImageOnly) {
					String txt = !TextUtils.isEmpty(mTitle) ? mTitle : mText;
					if (!TextUtils.isEmpty(txt)) {
						mShareAct.withTitle(txt);
						// 朋友圈withText没有作用 但不能为空 music need withText
						mShareAct.withText(!TextUtils.isEmpty(mText)?mText:app.app_name);					}
				}
				mShareAct.share();
			} else if (plat == SHARE_MEDIA.QQ) {
				media(false, true, true);
				qq();
			} else if (plat == SHARE_MEDIA.SINA) {
				media(false, true, false);
				sina();
			}
		}
	}
	public UMShareAPI initApi() {
		if (mShareAPI == null && mBf != null) {
			Activity act = mAct;
			if (act == null) act = mBf.getActivity();
			if (act == null) return null;
			mShareAPI = UMShareAPI.get(act);
		}
//			if (mShareAPI==null&&mAct!=null) mShareAPI = UMShareAPI.get(mAct.getApplicationContext());
		return mShareAPI;
	}
	public void destroy() {
		cancel();
		if (mShareAPI != null) {
			mShareAPI = null;
		}
		if (mAct != null) {
			mBf.hideDlg(ID_SHARE);
			mAct = null;
		}
		if (mBf != null) {
			BaseFragment bf = mBf;
			mBf = null;
			bf.shareExit();
		}
	}
	public void cancel() {
		if (mShareAct != null) {
			mShareAct.setCallback(null);
			mShareAct = null;
		}
	}
	private View shareView(Context ctx) {
		View view = View.inflate(ctx, mLayoutID ==0 ?app.Share_Layout:mLayoutID, null);
		for (int i=TCancel; i<=TWeixinCircle; ++i) {
			if (app.mPlatIds[i]!=0) {
				Util.setOnClick(view, app.mPlatIds[i], this);
			}
		}
		return view;
	}
	private boolean begin(SHARE_MEDIA t) {
		if (mAct == null) return false;
		initApi();
		cancel();
		// 微信及朋友圈
		if ((t == SHARE_MEDIA.WEIXIN || t == SHARE_MEDIA.WEIXIN_CIRCLE) && !isWXAppInstalledAndSupported(mAct)) {
			ToastUtils.showShort(mAct, "请安装微信后分享");
			return false;
		}
		mShareAct = new ShareAction(mAct);
		mShareAct.setCallback(this);
		mShareAct.setPlatform(t);
		if (TextUtils.isEmpty(mTitle)){
			mShareAct.withTitle(app.app_name);
		}else{
			mShareAct.withTitle(mTitle);
		}
		return true;
	}
	private void media(boolean text, boolean image, boolean url) {
		if (text) {
			mShareAct.withText(mText);
		}
			if (mMusicUrl != null){
				UMusic music = new UMusic(mMusicUrl);
				music.setTitle(TextUtils.isEmpty(mTitle) ? mText : mTitle);
				music.setAuthor(app.app_name);
				if (mImage!=null){
					if (mImage instanceof Bitmap) {
						music.setThumb(new UMImage(mAct, (Bitmap)mImage));
					} else if (mImage instanceof String) {
						music.setThumb(new UMImage(mAct, (String)mImage));
					}
				} else { // logo
					music.setThumb(new UMImage(mAct, app.logo_share));
				}
				if (url) {
					music.setTargetUrl(TextUtils.isEmpty(mUrl) ? app.DefaultShareUrl : mUrl);
				}
				mShareAct.withMedia(music);
			}else if (mMusicUrl == null && image) {
			if (mImage != null) {
				UMImage img = null;
				if (mImage instanceof Bitmap) {
					img = new UMImage(mAct, (Bitmap)mImage);
				} else if (mImage instanceof String) {
					img = new UMImage(mAct, (String)mImage);
				}
				if (img != null) mShareAct.withMedia(img);
			} else { // logo
				mShareAct.withMedia(new UMImage(mAct, app.logo_share));
			}
		}
		if (url) {
			mShareAct.withTargetUrl(TextUtils.isEmpty(mUrl) ? app.DefaultShareUrl : mUrl);
		}
	}
	//		腾讯平台
	private void qq() {
		if (!TextUtils.isEmpty(mText) && (mType == TextOnly || mType == TextImage || mType == MusicImage)) {
			mShareAct.withText(mText.length() <= 20 ? mText : (mText.substring(0, 17) + "..."));
		}
		mShareAct.share();
	}

	// 新浪微博
	private void sina() {
		if (!TextUtils.isEmpty(mText)) {
			StringBuilder sb = new StringBuilder(256);
			String url = TextUtils.isEmpty(mUrl) ? app.DefaultShareUrl : mUrl;
			if (mText.contains(url)) {
				sb.append('#').append(app.app_name).append('#').append(mText).append(" @").append(app.app_name);
//				url = "#多说英语#" + mText + " @多说英语";
			} else {
				sb.append('#').append(app.app_name).append('#').append(mText).append(" @").append(app.app_name).append(' ').append(url);
//				url = "#多说英语#" + mText + " @多说英语 " + url;
			}
			mShareAct.withText(sb.toString());
		}
		mShareAct.share();
	}
	@Override
	public void onClick(View v) {
		int type = TCancel;
		int id = v.getId();
		for (int i=0; i<app.mPlatIds.length; ++i) {
			if (app.mPlatIds[i]==id) {
				type = i;
				break;
			}
		}
		switch (type) {
		case TCancel: // 取消了
			destroy();
			break;
		case TWeixinCircle:
			//朋友圈
			if (begin(SHARE_MEDIA.WEIXIN_CIRCLE)) {
				media(false, mType != TextOnly, mType != ImageOnly);
				if (mType!=ImageOnly){
					String txt = !TextUtils.isEmpty(mTitle)?mTitle:mText;
					if (!TextUtils.isEmpty(txt)) {
						mShareAct.withTitle(txt);
						// 朋友圈withText没有作用 但不能为空 music need withText
						mShareAct.withText(!TextUtils.isEmpty(mText)?mText:app.app_name);
					}
				}
				mShareAct.share();
			}
			break;
		case TWeixin: {
			// 微信
			if (begin(SHARE_MEDIA.WEIXIN)) {
				media(mType != ImageOnly, mType != TextOnly, mType != ImageOnly);
				mShareAct.share();
			}
			break;
		}
		case TQQ:
			if (begin(SHARE_MEDIA.QQ)) {
				media(false, true, true);
				qq();
			}
			break;
		case TWeibo:
			if (begin(SHARE_MEDIA.SINA)) {
				String url = null;
				if (mMusicUrl != null) {
					url = mMusicUrl;
					mMusicUrl = null;
				}
				media(false, true, false);
				if (url != null) mMusicUrl = url;
				url = null;
				if (mMusicUrl != null){
					url = mText;
					mText = mTitle;
				}
				sina();
				if (url != null){
					mText = url;
				}
			}
			break;
		}
	}
	@Override
	public void onResult(SHARE_MEDIA plat) {
		cancel();
		int state = TCancel;
		if (plat == SHARE_MEDIA.QQ) {
			state = TQQ;
		} else if (plat == SHARE_MEDIA.SINA) {
			state = TWeibo;
		} else if (plat == SHARE_MEDIA.WEIXIN) {
			state = TWeixin;
		} else if (plat == SHARE_MEDIA.WEIXIN_CIRCLE) {
			state = TWeixinCircle;
		} else {
			state = TOther;
		}
		showToast(mOk);
		onResult(state);
	}
	@Override
	public void onError(SHARE_MEDIA share_media, Throwable throwable) {
		cancel();
		showToast(mFail);
		onResult(TFail);
	}
	@Override
	public void onCancel(SHARE_MEDIA share_media) {
		cancel();
		showToast(mCancel);
		onResult(TCancel);
	}
	public void onResult(int result) {
		if (mBf != null) {
			mBf.handleAction(IA_ShareRes, result, this);
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mBf != null && mShareAPI != null) {
			mShareAPI.onActivityResult(requestCode, resultCode, data);
		}
	}
	private void showToast(String s) {
		if (mBf != null) mBf.showShortToast(s);
	}

	public static void reportErr(String err) {
		MobclickAgent.reportError(app.ctx, err);
	}
	public static void reportErr(Throwable e) {
		MobclickAgent.reportError(app.ctx, e);
	}
	public static void report(StringBuilder sb) {
		reportErr(sb.toString());
	}
	public static StringBuilder buildFull(StringBuilder sb,String prefix,String...params) {
		if (sb==null) {
			sb = new StringBuilder(1024);
		}
		sb.append(prefix);
		for (String line : params) {
			if (line!=null)
				sb.append("\r\n\t").append(line);
		}
		return sb;
	}
	public static StringBuilder build(StringBuilder sb,String...params) {
		if (sb==null) {
			sb = new StringBuilder(1024);
		}
		for (String line : params) {
			if (line!=null)
				sb.append("\r\n\t").append(line);
		}
		return sb;
	}
	// 统计http接口失败
	public static void reportHttp(int err,String msg,String url) {
		HashMap<String,String> attrs = initNullAttr(3);
		attrs.put("err",err+msg);
		attrs.put("url",url);
		attrs.put("err+url",err+url);
		MobclickAgent.onEventValue(app.ctx,HttpErr,attrs,err);
	}
	public static HashMap<String,String> initNullAttr(int size) {
		return new HashMap<String, String>(size);
	}
	// 添加设备基础信息
	public static HashMap<String,String> initDevAttr(int size) {
		HashMap<String,String> attrs = new HashMap<String, String>(size+3);
		attrs.put("dev",String.format("%s-%s", Build.BRAND!=null?Build.BRAND:"", Build.MODEL!=null?Build.MODEL:""));
		attrs.put("android", Build.VERSION.RELEASE);
		attrs.put("api", String.valueOf(Build.VERSION.SDK_INT));
		return attrs;
	}
	public static void reportWarning(String s) {
		reportSimple(AppWarn,"ctx",s);
	}
	// 上报简单事件
	public static void reportSimple(String eid,String val) {
		reportSimple(eid,"value",val);
	}
	public static void reportSimple(String eid,String key,String val) {
		if (TextUtils.isEmpty(key)||TextUtils.isEmpty(val)) return;
		HashMap<String,String> attrs = initNullAttr(1);
		attrs.put(key,val);
		MobclickAgent.onEvent(app.ctx,eid,attrs);
	}
	public static StringWriter buildStack(StringWriter writer,Throwable ex) {
		if (writer==null)
			writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		return writer;
	}
	public static void report(String eid, HashMap<String, String> attrs) {
		MobclickAgent.onEvent(app.ctx,eid,attrs);
	}
}
