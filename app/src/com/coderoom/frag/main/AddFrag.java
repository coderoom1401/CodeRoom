package com.coderoom.frag.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coderoom.R;

import z.frame.BaseFragment;

/**
 * Created by duguang on 16-11-11.
 */
public class AddFrag extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_add, null);
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {

    }

    private void initData() {

    }

}
