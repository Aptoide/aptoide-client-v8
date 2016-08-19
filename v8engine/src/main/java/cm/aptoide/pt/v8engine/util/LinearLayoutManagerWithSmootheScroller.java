/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/08/2016.
 */

package cm.aptoide.pt.v8engine.util;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;

/**
 * Created by sithengineer on 17/08/16.
 * <p>
 * from {@see http://stackoverflow.com/questions/31235183/recyclerview-how-to-smooth-scroll-to-top-of-item-on-a-certain-position/32819067}
 */
public class LinearLayoutManagerWithSmootheScroller extends LinearLayoutManager {

	public LinearLayoutManagerWithSmootheScroller(Context context) {
		super(context, VERTICAL, false);
	}

	public LinearLayoutManagerWithSmootheScroller(Context context, int orientation, boolean reverseLayout) {
		super(context, orientation, reverseLayout);
	}

	@Override
	public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
		RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
		smoothScroller.setTargetPosition(position);
		startSmoothScroll(smoothScroller);
	}

	private class TopSnappedSmoothScroller extends LinearSmoothScroller {

		public TopSnappedSmoothScroller(Context context) {
			super(context);
		}

		@Override
		protected int getVerticalSnapPreference() {
			return SNAP_TO_START;
		}

		@Override
		public PointF computeScrollVectorForPosition(int targetPosition) {
			return LinearLayoutManagerWithSmootheScroller.this.computeScrollVectorForPosition(targetPosition);
		}
	}
}
