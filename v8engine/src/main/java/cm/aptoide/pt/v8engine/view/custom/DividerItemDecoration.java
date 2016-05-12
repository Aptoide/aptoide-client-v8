/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 11/05/2016.
 */

package cm.aptoide.pt.v8engine.view.custom;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by fabio on 22-10-2015.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

	private int space;

	public DividerItemDecoration(int space) {
		this.space = space;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		outRect.left = space;
		outRect.right = space;
		outRect.bottom = space;

		// Add top margin only for the first item to avoid double space between items

		if(parent.getLayoutManager() instanceof GridLayoutManager){

			int colcount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();

			if(parent.getChildPosition(view) < colcount){
				outRect.top = space;
			}

		}else if(parent.getChildPosition(view) == 0){
			outRect.top = space;
		}
	}
}
