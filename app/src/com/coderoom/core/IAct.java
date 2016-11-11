package com.coderoom.core;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import z.frame.ICommon;

// activity & fragment 相关基类
public interface IAct extends ICommon {
	//	public static final int FragmentEnter = 100;
//	public static final int FragmentExit = 101; // 退出界面
//	public static final int FragmentOnClick = 102; // 点击事件 msg=View

//	public static final int IA_ShareRes = UMServiceUtil.ID_SHARE;
	int LoginAs = 120; // 通用登录
	String ID = "id";
	String DATA = "data";
	String kType = "type"; // 通用类型 区分进入界面的方式

	// 上拉更多的样式处理
	class LoadingFooter implements ICommon {
		private FrameLayout mFootContainer;
		private View mFooter;
		private ImageView mIvLoading;
		private TextView mTvFootText;
		//foot是否显示。0表示不显示，1表示显示加载中，2表示显示没有更多数据了
		private int mType = 0;

		public LoadingFooter init(ListView lv,int id){
			if (mFootContainer != null) return this;
			mFootContainer = new FrameLayout(lv.getContext());
			mFooter = View.inflate(lv.getContext(), id,null);
//			mIvLoading = (ImageView) mFooter.findViewById(R.id.mIvLoading);
//			mTvFootText = (TextView) mFooter.findViewById(R.id.mTvFootText);
			lv.addFooterView(mFootContainer);
			return this;
		}

		public void show(int type){
			if (type == mType) return;
			switch (type){
				case 0:
					Util.setParent(mFooter, null, null);
					Util.clearAnim(mIvLoading);
					break;
				case 1:
					Util.setParent(mFooter, mFootContainer, null);
					mIvLoading.setVisibility(View.VISIBLE);
					Util.startAnim(mIvLoading);
					mTvFootText.setText("正在载入...");
					break;
				case 2:
					Util.setParent(mFooter,mFootContainer,null);
					Util.clearAnim(mIvLoading);
					mIvLoading.setVisibility(View.GONE);
					mTvFootText.setText("没有更多数据了");
					break;
			}
			mType = type;
		}
	}
}
