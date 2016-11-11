package com.coderoom.net;

import java.io.File;

import com.coderoom.common.GlobaleParms;
import com.coderoom.core.UserLogin;
import com.talk51.afast.log.Logger;

import z.db.ShareDB;
import z.frame.UmBuilder;
import z.http.ZHttpCenter;
import z.http.ZHttpItem;

//{
//"code": 1,
//"remindMsg": "登录成功",
//"res": {
//"id": "55123",
//"nickName": "Fly138",
//"avatar": "http://duoshuo.51talk.com/storage/avatar/20150519/20150519180518_602935.jpg",
//"checkHobby": 0,
//"golds": 79
//}
//}
// 封装http请求
public class HttpItem extends ZHttpItem implements IKey {
    public static final String TAG = "HttpItem";
	public static interface ILis {
    }

    public static interface IOKLis extends ILis {
        boolean onHttpOK(String msg, HttpResp resp);
    }

    public static interface IErrLis extends ILis {
        void onHttpError(int id, int errCode, String errMsg, Throwable e);
    }

    public static interface IFinishLis extends ILis {
        void onHttpFinish(int id, boolean bOK);
    }
	public static interface IOKErrLis extends IOKLis, IErrLis {
	}

    public static interface IAllLis extends IOKLis, IErrLis, IFinishLis {
    }

    private ILis mLis;

    // 设置自定义id
    public HttpItem setId(int id) {
	    super.setId(id);
        return this;
    }

    // 设置结果监听
    public HttpItem setListener(ILis lis) {
        mLis = lis;
        return this;
    }

    public HttpItem setUrl(String url) {
	    super.setUrl(url);
        return this;
    }

    public HttpItem put(String k, int v) {
	    getParams().put(k, v);
        return this;
    }

    public HttpItem put(String k, double v) {
	    getParams().put(k, v);
        return this;
    }

    public HttpItem put(String k, String v) {
	    getParams().put(k, v);
        return this;
    }

	public boolean put(String k, File v) {
		getParams().put(k, v, null);
		return true;
	}
	// 清空参数
	public void clearParams() {
		getParams().removeAll();
	}
	// 获取设定的参数
	public String getStringParams(String k) {
		return getParams().urlParams.get(k).value;
	}
	public int getIntParams(String k) {
		return Integer.valueOf(getParams().urlParams.get(k).value);
	}

	public String fullId() {
		return mLis+"["+getId()+"]";
//		return (mLis!=null?mLis.getClass().getName()+"@"+mLis.hashCode():"null@null")+"["+id+"]";
	}
	@Override
	public void onFinish(ZHttpItem hi, int status, String body, Exception e) {
		if (mbCancel) return;
		boolean bOK = false;
		if (status<300&&e==null) {
			// 成功
			Logger.w(TAG, fullId()+"onSuccess:" + status + ", body=" + body);
			try {
				if (body!=null) {
					HttpResp resp = new HttpResp();
					resp.parse(body);
					resp.mParam = mParams;
					status = resp.getCode();
					ShareDB.Sec sec = new ShareDB.Sec("debug");
					boolean isOpenCode = sec.getBoolean("isOpenCode");
					if (isOpenCode){
						status = sec.getInt("code");
						sec.put("isOpenCode",false);
						sec.save(true);
					}
					body = resp.getMsg();
//					SyncMgr.httpTimeout(false);
					if (status==1) {
						bOK = onOK(body, resp);
//			        UmengErr.reportHttp(resp.getCode(),resp.getMsg(),mUrl);
					} else {
						if (status == ENoLogin || status == ELoginTimeout){
							//ENoLogin == 10102账号未登录
							//ELoginTimeout == 10103Token过期
							//需要自动登录一下
							UserLogin.autoLogin();
						}
						onError(status, body, null);
					}
				} else {
					onError(-1, FMTERR, null); // 包体格式错误
				}
			} catch (Exception ex) {
				onError(-1, FMTERR, ex); // 包体格式错误
				ex.printStackTrace();
			}
		} else {
			if (status < 300) {
				status = 1000;
			}
			Logger.w(TAG, fullId()+"onFailure:" + status + ", body=" + body);
			onError(status, body == null ? "" : body, e);
			onFinish(false);
		}
		onFinish(bOK);
	}

    private boolean onOK(String msg, HttpResp resp) {
	    UmBuilder.reportHttp(resp.getCode(), resp.getMsg(), mUrl);
        if (mLis != null && (mLis instanceof IOKLis)) {
            IOKLis lis = (IOKLis) mLis;
            resp.id = mId;
            return lis.onHttpOK(msg, resp);
        }
        return true;
    }

    // 错误分支
    // errCode>=300 http层的错误
    // 否则 数据/解析错误
    public void onError(int errCode, String errMsg, Throwable e) {
	    UmBuilder.reportHttp(errCode, errMsg, mUrl);
//	    if (errCode>AppCodeB||errCode==-1) {
//		    StringBuilder sb = UmengErr.buildFull(null, mUrl, "errCode=" + (errCode - AppCodeB), "errMsg=" + errMsg);
//		    UmengErr.report(sb);
//	    }
        if (mLis != null && (mLis instanceof IErrLis)) {
            IErrLis lis = (IErrLis) mLis;
            lis.onHttpError(mId, errCode, errMsg, e);
        } else {
	        if (e!=null) e.printStackTrace();
        }
    }

    // 请求结束
    public void onFinish(boolean bOK) {
        if (mLis != null) {
            if (mLis instanceof IFinishLis) {
                IFinishLis lis = (IFinishLis) mLis;
                mLis = null;
                lis.onHttpFinish(mId, bOK);
            } else {
                mLis = null;
            }
        }
    }

    // 发送请求
    public void post(Object o) {
	    mDebug = GlobaleParms.isDebug;
	    mUseGzip = true;
	    setKey(o);
	    setCtx(ZHttpCenter.getCtx());
	    HttpIFCtx.instance().addPublicParams(getParams());
	    Logger.w(TAG, fullId()+"request:" + mUrl +"?"+ getParams().toOrderString());
	    request();
    }
}
