package com.coderoom.frag.main;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHostFixs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.coderoom.R;
import com.coderoom.activity.HomeAct;
import com.coderoom.core.IUmEvt;
import com.coderoom.frag.login.LoginFrag;

import z.frame.BaseFragment;
import z.frame.DelayAction;
import z.frame.UmBuilder;

// 主界面
public class HomeFrag extends BaseFragment implements TabHost.OnTabChangeListener {

    private static final String EvtID = IUmEvt.HomeTab;
	public static final int FID = 1900;
    public static final int IA_SelectTab = FID+1;
    public static final int IA_AutoLogin = FID+2;
    public static final int IA_Tab = FID+3;

    public static final String kSelectTab = "select_tab";
	public static final int ST_Home = 0; // 首页
	public static final int ST_Add = 1; // Add
	public static final int ST_Self = 2; // 设置

    private DelayAction mDelay = new DelayAction(); // 防止点击太频繁
    private long mPressedTime;

    private Class mFrags[] = {ListFrag.class,AddFrag.class,SettingFrag.class};
//    private int[] mIcons = {R.drawable.ic_lesson,R.drawable.ic_task,R.drawable.ic_rank};
    private String[] mNames = {"首页","添加","自己"};
    private int[] mTvColor = {R.color.red_f36b4b,R.color.blue_57c5f6,R.color.yellow_ffb017};
    private ArrayList<TextView>  mTvList = new ArrayList<TextView>();

    private FragmentTabHostFixs mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_home, null);
            setName("主界面(Home)");
            initView();
            initData();
            handleArgs(getArguments());
        }
        return mRoot;
    }

    private void initData() {
        registerLocal(IA_Tab);
        registerLocal(IA_AutoLogin);
    }

    private void initView(){
        if (mRoot == null)return;
        mTabHost = (FragmentTabHostFixs)findViewById(android.R.id.tabhost);
        mTabHost.setup(mRoot.getContext(), mTabHost.fix(getChildFragmentManager()), R.id.mFlGroup);
        mTabHost.setOnTabChangedListener(this);
//        if (mTvList.size() >0)mTvList.clear();
        for(int i = 0; i < mFrags.length; i++){
            //设置Tab按钮图标,文字
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mNames[i]).setIndicator(getTabView(i));
            //设置Tab内容
            mTabHost.addTab(tabSpec, mFrags[i], null);
            //Tab按钮背景
//            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    // 给Tab按钮设置图标和文字
    private View getTabView(int i){
        if (mRoot == null)return null;
        View view = View.inflate(mRoot.getContext(),R.layout.item_tab, null);
        TextView mTvIcon = (TextView) view.findViewById(R.id.mTvIcon);
//        ImageView iv = (ImageView) view.findViewById(R.id.mIvIcon);
//        iv.setImageResource(mIcons[i]);
//        mTvList.add(mTvIcon);
        if (i ==0) mTvIcon.setTextColor(mRoot.getResources().getColor(mTvColor[0]));
        Util.setText(view,R.id.mTvIcon,mNames[i]);
        return view;
    }

    public void handleArgs(Bundle args) {
		if (args!=null) {
			int page = args.getInt(kSelectTab, -1);
			if (page>-1) {
				commitAction(IA_SelectTab,page,null,20);
			}
		}
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        long curTs = System.currentTimeMillis();
        if (mPressedTime == 0 || curTs - mPressedTime > 2000) {
            mPressedTime = curTs;
            Toast.makeText(getActivity(), R.string.string_exit,
                    Toast.LENGTH_SHORT).show();
            return true;
        } else if (curTs - mPressedTime < 2000) {
//            System.exit(0);
//            Intent home = new Intent(Intent.ACTION_MAIN);
//            home.addCategory(Intent.CATEGORY_HOME);
//            startActivity(home);
            return false;
        }
        return false;
    }

    // 异步操作统一入口 避免许多Runnable
    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id) {
        case IA_AutoLogin:
            if (mRoot == null)return;
            Intent it = new Intent(mRoot.getContext(), HomeAct.class);
            it.putExtra(HomeAct.kFID, LoginFrag.FID);
            startActivity(it);
            pop(false);
            break;
        case IA_Tab:
            mTabHost.setCurrentTab(arg);
            break;
        default:
            break;
        }
    }
    private int mTabIndex = 0;
    @Override
    public void onTabChanged(String tabId) {
        if (mRoot == null)return;
        for (int i = 0; i < mTvList.size(); i++) {
            mTvList.get(i).setTextColor(mRoot.getResources().getColor(R.color.black_344758));
        }

        if (tabId.equals(mNames[0])){
            UmBuilder.reportSimple(EvtID, "首页");
            mTabIndex = 0;
//            UpdateVerFrag.checkJoinShow(this);
        }else if (tabId.equals(mNames[1])){
            UmBuilder.reportSimple(EvtID, "添加");
            mTabIndex = 1;
        }else if (tabId.equals(mNames[2])){
            UmBuilder.reportSimple(EvtID, "自己");
            mTabIndex = 2;
        }
        mTvList.get(mTabIndex).setTextColor(mRoot.getResources().getColor(mTvColor[mTabIndex]));
    }
}
