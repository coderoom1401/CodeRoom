package com.coderoom.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.coderoom.R;
import com.coderoom.core.IAct;
import com.coderoom.core.UserLogin;
import com.coderoom.db.dao.IUser;
import com.coderoom.frag.login.GuideFrag;
import com.coderoom.frag.login.LoginFrag;
import com.coderoom.frag.main.HomeFrag;
import com.coderoom.frag.main.SplashFrag;

import z.frame.BaseAct;

// 第1层activity 单例
// 管理从闪屏->导航->登录->注册->主页一连串逻辑
// 从主页之后的页面都弹出到第2层activity中
public class HomeAct extends BaseAct implements IAct {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置容器
        setContentView(R.layout.act_cnt);
//        PushManager.getInstance().initialize(this.getApplicationContext());
        mCntId = R.id.scr_cnt;
        onEnterCreate(savedInstanceState);
    }

    protected void onEnterCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            // 显示闪屏页面
            switch (getIntent().getIntExtra(kFID, SplashFrag.FID)) {
            case SplashFrag.FID: // 需要支持从其他入口进来
                pushFragment(new SplashFrag(), 0);
                break;
            case GuideFrag.FID: // 导航页面
            case SplashFrag.FExit: // splash页面退出
                onActMsg(SplashFrag.FExit, null, 0, null);
                break;
            case HomeFrag.FID:
//				if (UserLogin.checkAutoLogin()) {
//					// 有用户 进入主页
//					gotoHomeFrag();
//				} else {
//					// 没用户 相当于重进应用
//					pushFragment(new SplashFrag(),0);
//				}
                break;
            }
        }
    }

    @Override
    public int onActMsg(int id, Object sender, int arg, Object extra) {
        switch (id) {
        case LoginAs:
            // 执行登录
            gotoHomeFrag();
            break;
        case SplashFrag.FExit: { // 闪屏页面退出
            // 考虑自动登录到主界面
            if (IUser.Dao.checkAutoLogin()) {
                audoLogin();
                gotoHomeFrag();
            } else {
//                pushFragment(new LoginFrag(), 0);
                pushFragment(new GuideFrag(), 0);
            }
            break;
        }
        default:
            super.onActMsg(id, sender, arg, extra);
            break;
        }
        return 1;
    }

    public void gotoHomeFrag() {
        if (mIsResume) {
            // 弹出所有界面
            popAll();
            // 进入主界面
            HomeFrag hf = new HomeFrag();
            hf.setArguments(getIntent().getExtras());
            pushFragment(hf, 0);
        } else {
            Intent it = new Intent(this, HomeAct.class);
            it.putExtra(kFID, HomeFrag.FID);
            startActivity(it);
        }
    }

    //隔一段时间，自动登录一次
    private void audoLogin() {
//        Cache c = new Cache(FileCenter.getUserRootDir(), "autoLoginTime", true);
//        c.load();
//        int lastUp = c.getInt("autoLoginTime", 0);自动登录 >> 未到1小时
//        int cur = SyncMgr.curSec(); // 秒
//        int checkTime = 60 * 60 * 1; // 失效时间1小时
//        if (cur < lastUp + checkTime) {
//            _log("自动登录 >> 未到1小时");
//            return;
//        }
//        c.put("autoLoginTime",cur);
//        c.save();
        UserLogin.autoLogin();
    }

    private void onLogout() {
        // 退出登录 跳到导航页面
        popAll();
//        clearAutoLogin();
        UserLogin.clearUser();
        pushFragment(new GuideFrag(), 0);
    }

    private void onLogin(){
        popAll();
//        clearAutoLogin();
        UserLogin.clearUser();
        pushFragment(new GuideFrag(), 0);
        pushFragment(new LoginFrag(), PF_Back);
    }

//    private void clearAutoLogin() {
//        ShareDB.Sec sec = new ShareDB.Sec(IUser.SEC_AUTOLOGIN);
//        sec.put("autologin", false);
//        sec.save(false);
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int fid = intent.getIntExtra(kFID, 0);
        if (fid == GuideFrag.FID) {
            onLogout();
        }else if (fid == LoginFrag.FID) {
            onLogin();
        }else if (fid == HomeFrag.FID) {
            FragmentManager fm = getSupportFragmentManager();
            HomeFrag hf = (HomeFrag) fm.findFragmentByTag(HomeFrag.class.getName());
            if (hf != null) {
                hf.handleArgs(intent.getExtras());
                return;
            }
            // 弹出所有界面
            popAll();
            // 进入主界面
            hf = new HomeFrag();
            hf.setArguments(intent.getExtras());
            pushFragment(hf, 0);
        }
    }

    public static boolean fixOpen(Context ctx) {
        // 当前没有主界面 将主界面弹出来
        if (app.am.findActivity(HomeAct.class) == null) {
            Intent it = new Intent(ctx, HomeAct.class);
            it.putExtra(kFID, HomeFrag.FID);
            ctx.startActivity(it);
            return true;
        }
//		if (ActivityManager.getScreenManager().findActivity(HomeActivity.class) == null) {
//			startActivity(HomeActivity.class);
//		}
        return false;
    }
}
