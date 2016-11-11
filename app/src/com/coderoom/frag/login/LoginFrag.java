package com.coderoom.frag.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.coderoom.R;
import com.coderoom.core.IUmEvt;
import com.coderoom.core.TitleFrag;
import com.coderoom.core.UserLogin;
import com.coderoom.utils.EditListener;
import com.talk51.afast.utils.NetUtil;

import z.frame.DelayAction;
import z.frame.UmBuilder;
import z.image.universal_image_loader.core.DisplayImageOptions;

/**
 * Created by duguang
 */
public class LoginFrag extends TitleFrag implements TextView.OnEditorActionListener {

    private static final String EvtID = IUmEvt.Login;
    public static final int FID = 1200;
    public static final String AC = "ac";//账号
    public static final String PW = "pw";//密码
    public static final String PW_Desc = "pwDesc";//密码提示描述

    // 防止点击太频繁
    private DelayAction mDelay = new DelayAction();
    private EditText mEtNickName;
    private EditText mEtPassWord;
    private String mPassWord;
    private String mAccount;
    private DisplayImageOptions mOptions;

    private EditListener mNameEditL = new EditListener();
    private EditListener mPwdEditL = new EditListener();

    private UserLogin mUserLogin;
    private View mDebugView;

    private ListView mLvAccountHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_login, null);
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        setTitleText("登录");
        mNameEditL.init(mRoot, R.id.editTxt_nickName, R.id.iv_nickName_x);
        mPwdEditL.init(mRoot, R.id.editTxt_password, R.id.iv_password_x);

        mEtNickName = (EditText) findViewById(R.id.editTxt_nickName);
        mEtPassWord = (EditText) findViewById(R.id.editTxt_password);
        mEtPassWord.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mEtPassWord.setOnEditorActionListener(this);
    }

    private void initData() {
//        mOptions = ImageLoderUtil.setImageLogder(R.drawable.bg_main);
        mUserLogin = new UserLogin(mRoot.getContext());
        Bundle args = getArguments();
        if (args != null) {
            String ac = args.getString(AC);
            String pw = args.getString(PW);
            String pwDesc = args.getString(PW_Desc);
            if (!TextUtils.isEmpty(ac)) {
                mEtNickName.setText(ac);
            }
            if (!TextUtils.isEmpty(pwDesc)) {
                mEtPassWord.setHint(pwDesc);
            }
            mEtPassWord.setText(!TextUtils.isEmpty(pw) ? pw : "");
        }
    }

    @Override
    public void onClick(View v) {
        if (mDelay.invalid()) {
            return;
        }
        switch (v.getId()) {
            case R.id.left: // 默认返回上个界面
                UmBuilder.reportSimple(EvtID, "返回");
                pop(true);
                break;
            case R.id.btn_login:
                UmBuilder.reportSimple(EvtID, "登录");
                startLogin();
                break;
            case R.id.tv_find_password:
                UmBuilder.reportSimple(EvtID, "找回密码");
//                new BaseFragment.Builder(this, new MobileFrag()).with(MobileFrag.KEY_CODDE_TYPE, MobileFrag.TYPE_FIND_PWD).show();
                break;
            case R.id.tv_register:
                UmBuilder.reportSimple(EvtID, "注册");
//                pushFragment(new RegisterFrag(), PF_Back);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void startLogin() {
        mAccount = mEtNickName.getText().toString().trim();
        if (TextUtils.isEmpty(mAccount)) {
            showShortToast("请输入正确的手机号");
            return;
        }

        mPassWord = mEtPassWord.getText().toString().trim();
        if (TextUtils.isEmpty(mPassWord)) {
            showShortToast("请输入密码");
            return;
        }

        if (mRoot != null && !NetUtil.checkNet(mRoot.getContext())) {
            showShortToast("网络连接失败，请稍后再试");
            return;
        }

        showLoading(true);
        showImm(false, null);
        mUserLogin.isNormalLogin = true;
        mUserLogin.setBaseFragment(this);
        mUserLogin.doLogin(mAccount, mPassWord);

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //当actionId == XX_SEND 或者 XX_DONE时都触发
        //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
        //注意，这时一定要判断event != null。因为在某些输入法上会返回null。
        if (actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE
                || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {

            String editStr = v.getText().toString().trim();

            if (TextUtils.isEmpty(editStr)) {
                showShortToast("请输入密码");
            } else {
                //登录逻辑
                startLogin();
            }
        }
        return false;
    }

}
