/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/05/2016.
 */

package cm.aptoide.pt.v8engine.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class BaseRecyclerViewFragment<T extends RecyclerView.Adapter> extends
		BaseLoaderFragment<RecyclerView> {

	protected T adapter;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = createAdapter();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		baseView.setAdapter(adapter);
		baseView.setLayoutManager(createLayoutManager());

		baseView.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView
					.State state) {

				int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
						getActivity()
						.getResources()
						.getDisplayMetrics());
				outRect.set(offset, offset, offset, offset);
			}
		});
	}

	@Override
	public int getRootViewId() {
		return R.layout.recycler_fragment;
	}

	protected abstract T createAdapter();

	protected abstract RecyclerView.LayoutManager createLayoutManager();
}
