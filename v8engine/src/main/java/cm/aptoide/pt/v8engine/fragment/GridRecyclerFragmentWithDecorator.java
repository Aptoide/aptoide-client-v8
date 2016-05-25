/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 25-05-2016.
 */
public abstract class GridRecyclerFragmentWithDecorator extends GridRecyclerFragment {

	@Override
	public void setupViews() {
		super.setupViews();
		recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

				int offset = 0;
				FragmentActivity activity = getActivity();
				if (activity != null) {
					offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, activity.getResources()
							.getDisplayMetrics());
				}

				if (view.getId() == R.id.brick_app_item) {
					offset /= 2;
				}

				outRect.set(offset, offset, offset, offset);
			}
		});
	}
}
