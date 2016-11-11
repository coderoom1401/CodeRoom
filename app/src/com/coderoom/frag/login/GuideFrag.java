package com.coderoom.frag.login;


import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.coderoom.R;
import com.coderoom.common.IAutoParams;
import com.coderoom.core.IUmEvt;

import z.frame.BaseFragment;
import z.frame.DelayAction;
import z.frame.UmBuilder;

// 导航界面
public class GuideFrag extends BaseFragment implements View.OnClickListener , ViewPager.OnPageChangeListener{
    public static final int FID = 1100;
    private static final String EvtID = IUmEvt.Guide;

    private ViewPager mVp;
    private GuideAdapter mGuideAdapter = null;
    private LinearLayout mLlDotGroup;
    // 防止点击太频繁
    private DelayAction mDelay = new DelayAction();
    private ArrayList<View> mGuides = new ArrayList<View>();
    private ImageView[] mDots = null;
    private int mDotPos = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_guide, null);
            setName("引导页面");
            initView();
        }
        return mRoot;
    }

    private void initView() {
        mVp = (ViewPager) findViewById(R.id.mVp);
        mLlDotGroup = (LinearLayout) findViewById(R.id.mLlDotGroup);
        addPage();
        mGuideAdapter = new GuideAdapter();
        mVp.setAdapter(mGuideAdapter);
        mVp.setOnPageChangeListener(this);
        initDots();
        mVp.setCurrentItem(0);
        mGuideAdapter.notifyDataSetChanged();
    }

    private void addPage() {
        if (mRoot == null)return;
        View help1 = View.inflate(mRoot.getContext(), R.layout.item_guide_blue, null);
        View help2 = View.inflate(mRoot.getContext(), R.layout.item_guide_yellow, null);
        View help3 = View.inflate(mRoot.getContext(), R.layout.item_guide_red, null);
        mGuides.add(help1);
        mGuides.add(help2);
        mGuides.add(help3);
    }

    @Override
    public void onClick(View v) {
        if (mDelay.invalid()) return;
        switch (v.getId()) {
        case R.id.mBtnLogin:
            UmBuilder.reportSimple(EvtID, "登录");
            new Builder(this, new LoginFrag()).show();
            break;
        case R.id.mBtnReg:
            UmBuilder.reportSimple(EvtID, "注册");
            new Builder(this, new RegisterFrag()).show();
            break;
        }
    }

    // 初始化圆点
    private void initDots() {
        int len = mGuides.size();
        if (mLlDotGroup.getChildCount() > 0) {
            mLlDotGroup.removeAllViews();
        }
        float deny = IAutoParams.Sec.loadFloat(IAutoParams.kDensity);
        ImageView mIvDot = null;
        mDots = new ImageView[len];
        for (int i = 0; i < len; ++i) {
            if (mRoot == null)return;
            mIvDot = new ImageView(mRoot.getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            // 点与点间距
            int marg = (int)(8 * deny);
            lp.leftMargin = marg;
            lp.rightMargin = marg;
            mIvDot.setLayoutParams(lp);

            if (i == 0) {
                mIvDot.setImageResource(R.drawable.guide_dot_blue);
            } else {
                mIvDot.setImageResource(R.drawable.guide_dot_normal);
            }
            mDots[i] = mIvDot;
            mLlDotGroup.addView(mIvDot);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        mDots[mDotPos].setImageResource(R.drawable.guide_dot_normal);
        int dot =  R.drawable.guide_dot_blue;
//        if (i == 0){
//            dot = R.drawable.guide_dot_blue;
//        }else if(i == 1){
//            dot = R.drawable.guide_dot_yellow;
//        }else if(i == 2){
//            dot = R.drawable.guide_dot_red;
//        }
        mDots[i].setImageResource(dot);
        mDotPos = i;
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private class GuideAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return (mGuides != null) ? mGuides.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            View mi = (View) o;
            return view == mi;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int pos) {
            View mi = mGuides.get(pos);
            container.addView(mGuides.get(pos));
            return mi;
        }

        @Override
        public void destroyItem(ViewGroup container, int pos, Object o) {
            View mi = (View) o;
            container.removeView(mi);
        }
    }

}
