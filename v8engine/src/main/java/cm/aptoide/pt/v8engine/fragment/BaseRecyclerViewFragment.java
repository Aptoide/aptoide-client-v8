/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 09/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

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
		BaseLoaderToolbarFragment {

	protected T adapter;
	private RecyclerView recyclerView;

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		adapter = createAdapter();

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public int getRootViewId() {
		return R.layout.recycler_fragment;
	}

	@Override
	protected void setupViews() {
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(createLayoutManager());

		recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
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
	protected void bindViews(View view) {
		super.bindViews(view);
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		recyclerView = null;
		adapter = null;
	}

	protected abstract T createAdapter();

	protected abstract RecyclerView.LayoutManager createLayoutManager();
}
