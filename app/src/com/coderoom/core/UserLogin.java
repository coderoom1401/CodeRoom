package com.coderoom.core;


import android.content.Context;
import android.text.TextUtils;

import com.coderoom.bean.UserBean;
import com.coderoom.common.ContantValue;
import com.coderoom.common.IAutoParams;
import com.coderoom.db.dao.IUser;
import com.coderoom.frag.main.HomeFrag;
import com.coderoom.net.HttpItem;
import com.coderoom.net.HttpResp;
import com.coderoom.net.IKey;
import com.coderoom.net.SyncMgr;
import com.talk51.afast.log.Logger;
import com.talk51.afast.utils.MD5Utils;
import com.talk51.afast.utils.ToastUtils;

import z.db.ShareDB;
import z.frame.BaseFragment;
import z.frame.ICommon;
import z.frame.LocalCenter;

public class UserLogin implements HttpItem.IAllLis, IKey, ICommon, IUser, IAct {

    private static final String TAG = UserLogin.class.getSimpleName();
    public static String Auto_Login = "autologin";
    public static boolean isLogining = false;//是否正在登录
    public boolean isNormalLogin = false;//true==普通方式, false ==自动登录方式

    // 清除自动登录用户
    public static void clearUser() {
        // 清除自动登录信息
        ShareDB.Sec.clearSec(SEC_AUTOLOGIN);
        // 清除用户资料信息
        IUser.Dao.exitUser();
    }

    private HttpItem hi = new HttpItem();
    private Context mCtx;
    private String mUser; // 用户标示
    private String mPwd; // 用户密码/token
    private String mCid; //个推cid
    private BaseFragment mBfg;

    public UserLogin(Context ctx) {
        mCtx = ctx;
        // 从cookies获取登录状态
//		IUser.Dao.updateLogin(0);
    }

    public void setBaseFragment(BaseFragment bfg) {
        mBfg = bfg;
    }

    private void showShortToast(String txt) {
        if (mCtx == null) return;
        ToastUtils.showShortToast(txt);
    }

    public static void saveData(String account, String pwd, UserBean userInfo) {
        // 初始化内存变量
        // 重新打开数据库
        app.db.reOpen();
        // 保存到数据库
        IUser.Dao.saveUser(userInfo);
        // 保存到自动登录配置
        ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
        sec.put(Auto_Login, true);
        sec.put("account", account);
        sec.put("password", pwd);
        sec.put("token", userInfo.token != null ? userInfo.token : "");
        sec.put("uid", userInfo.uid);
        sec.put("useravatar", userInfo.avatar);
        sec.put("login", 1);
        sec.put("autoLoginTime", SyncMgr.curSec());
        sec.save(false);
    }

    // 执行登录
    public void doLogin(String user, String pwd) {
        String cid = ShareDB.Key.loadString(IAutoParams.kSec, IAutoParams.kGeTuiId);
        doLogin(user, pwd, cid);
    }

    public static void autoLogin(){
        ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
        int lastUp = sec.getInt("autoLoginTime");
        int cur = SyncMgr.curSec(); // 秒
        int checkTime = 60 * 60 * 1; // 失效时间1小时
        if (cur < lastUp + checkTime) {
            Logger.e("自动登录 >> 未到1小时");
            return;
        }
        if (isLogining)return;
        UserLogin userLogin = new UserLogin(app.ctx);
        userLogin.doAutoLogin();
    }

    private void doLogin(String account, String passWord, String cid) {
        mUser = account;
        mPwd = passWord;
        mCid = cid;
        isLogining = true;
        hi.setId(HttpLogin);
        if (!TextUtils.isEmpty(cid)) hi.put("cid", cid);
        hi.setUrl(ContantValue.F_LOGIN).put("account", account).put("password", MD5Utils.encode(passWord));
        hi.setListener(this).post(null);
    }

    // 执行自动登录
    public void doAutoLogin() {
        ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
        if (!sec.getBoolean(Auto_Login)) return;

        String userName = sec.getString("account");
        String pwd = sec.getString("password");
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)){
            Logger.i(TAG,"自动登录  账号或密码为空 >>> 账号="+userName+" ,密码="+pwd);
            LocalCenter.send(HomeFrag.IA_AutoLogin, 0, 0);
        }else{
            Logger.i(TAG,"自动登录中... >>>");
            doLogin(userName, pwd);
        }
    }

    // 执行注销接口
    public void doLogout() {
        new HttpItem().setId(HttpLogout).setUrl(ContantValue.F_LOGOUT).setListener(this).post(this);
    }

    // 登录和注销
    private static final int HttpLogout = 10000;
    private static final int HttpLogin = 10001;

    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {
        if (mBfg != null) {
            mBfg.hideLoading();
        }
        switch (resp.id) {
        case HttpLogout:
            clearUser();
            break;
        case HttpLogin:
            isLogining = false;
            UserBean userInfo = resp.getObject(UserBean.class, RES);
            if (userInfo == null) {
                showShortToast(FMTERR);
                return false;
            }
            if (TextUtils.isEmpty(userInfo.uid)) {
                showShortToast(UIDERR);
                return false;
            }
//            // 设置登录状态
//            IUser.Dao.updateLogin(1);
            saveData(mUser, mPwd, userInfo);
            //TODO 这个需要看一下干嘛的
//            SyncMgr.handleLoginOK();//这个需要看一下干嘛的
            Logger.i(TAG,"登录成功 >>> ");
            if (mBfg != null) mBfg.notifyActivity(LoginAs, 0, "");
            break;
        }
        return true;
    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        if (mBfg != null) {
            mBfg.hideLoading();
        }
        switch (id) {
        case HttpLogin:
            isLogining = false;
            showShortToast(TextUtils.isEmpty(errMsg) ? "登录失败，请重试" : errMsg);
            if (isNormalLogin){
                return;
            }
//            if ((errCode == ELoginError || errCode == ENoLogin)){
                if (mBfg != null) mBfg._log("自动登录，账号密码错误，跳转到登录页面");
                LocalCenter.send(HomeFrag.IA_AutoLogin, 0, 0);
//            }
            break;
        case HttpLogout:
            clearUser();
            break;
        }
    }

    @Override
    public void onHttpFinish(int id, boolean bOK) {
    }

    // 检查接口错误码 并重新登录 true提示 false不提示
    public static boolean checkErrorCode(int err) {
        if (err > AppCodeB) {
            err -= AppCodeB;
        }
        if (err == ELoginTimeout) {
            // 登录过期 尝试自动登录
            new UserLogin(MainApplication.mApplication).doAutoLogin();
            return false;
        }
        return true;
    }
}
