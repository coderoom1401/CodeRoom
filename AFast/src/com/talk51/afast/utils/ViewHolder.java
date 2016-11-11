package com.talk51.afast.utils;

import android.util.SparseArray;
import android.view.View;

/**
 * 说明：Adapter复用convertView的帮助类
 * @author Duguang
 * @version 创建时间：2014-12-29  下午6:30:20
 */
public class ViewHolder {

	// I added a generic return type to reduce the casting noise in client code
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}
