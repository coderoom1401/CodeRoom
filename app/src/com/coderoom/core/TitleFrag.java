package com.coderoom.core;

import android.view.View;
import android.widget.ImageView;

import com.coderoom.R;
import com.talk51.afast.view.RecycleImageView;

import z.frame.BaseFragment;
//import z.frame.IAct;

// 标题设置
public class TitleFrag extends BaseFragment implements View.OnClickListener {
    protected View mTitle;
    private RecycleImageView mIvMain;

    public View getTitle() {
        if (mTitle == null) {
            if (mRoot != null)
                mTitle = mRoot.findViewById(R.id.RlTitle);
        }
        return mTitle;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 设置标题 支持string和resId
    public void setTitle(Object left, Object title, Object right) {
        View t = getTitle();
        if (t == null) return;
        // 左边文字
        setLeftText(left);
        // 中间标题
        setTitleText(title);
        // 右边文字
        setRightText(right);
        t.setVisibility(View.VISIBLE);
    }

    public View setRightImage(int Rdraw) {
        View t = getTitle();
        if (t == null) return null;
        ImageView v = (ImageView) t.findViewById(R.id.mIvUp);
        if (v == null) return null;
        v.setImageResource(Rdraw);
        v.setVisibility(View.VISIBLE);
        View right = Util.setVisible(t, R.id.right, View.VISIBLE);
        if (v == null) return null;
        right.setOnClickListener(this);
        return right;
    }

    // 左边文字
    public View setLeftText(Object left) {
        View t = getTitle();
        if (left != null) {
            View v = Util.setVisible(t, R.id.left, View.VISIBLE);
            if (v != null) v.setOnClickListener(this);
            return Util.setText(t, R.id.tv_left, left);
        } else {
            return Util.setVisible(t, R.id.left, View.INVISIBLE);
        }
    }

    // 中间标题
    public View setTitleText(Object title) {
        View t = getTitle();
        View leftView = Util.setVisible(t, R.id.left, View.VISIBLE);
        if (leftView != null) leftView.setOnClickListener(this);
        if (title != null) {
            Util.setVisible(t, R.id.middle, View.VISIBLE);
            return Util.setText(t, R.id.tv_title, title);
        } else {
            return Util.setVisible(t, R.id.middle, View.INVISIBLE);
        }
    }

    // 右边文字
    public View setRightText(Object right) {
        View t = getTitle();
        if (right != null) {
            View v = Util.setVisible(t, R.id.right, View.VISIBLE);
            if (v != null) v.setOnClickListener(this);
            return Util.setText(t, R.id.tv_right, right);
        } else {
            return Util.setVisible(t, R.id.right, View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left: // 默认返回上个界面
                pop(true);
                break;
        }
    }
}
