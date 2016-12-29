package cm.aptoide.pt.v8engine.view.recycler.widget.implementations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.v8engine.util.RecyclerViewUtils;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayableGroup;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetFactory;

/**
 * Created by neuro on 28-12-2016.
 */

public class WidgetGroup extends Widget<DisplayableGroup> {

	private RecyclerView recyclerView;

	public WidgetGroup(@NonNull View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		recyclerView = (RecyclerView) itemView;
		recyclerView.addItemDecoration(RecyclerViewUtils.newAptoideDefaultItemDecoration(getContext()));
	}

	@Override
	public void bindView(DisplayableGroup displayable) {
		BaseAdapter adapter = new BaseAdapter(displayable.getChildren());
		recyclerView.setNestedScrollingEnabled(false);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new BaseGridLayoutManager(getContext(), adapter));
	}

	public class BaseGridLayoutManager extends GridLayoutManager {

		public BaseGridLayoutManager(Context context, BaseAdapter baseAdapter) {
			super(context, WidgetFactory.getColumnSize());
			setSpanSizeLookup(new SpanSizeLookup(baseAdapter));
		}

		private class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

			private final BaseAdapter baseAdapter;

			public SpanSizeLookup(BaseAdapter baseAdapter) {
				this.baseAdapter = baseAdapter;
			}

			@Override
			public int getSpanSize(int position) {
				final Displayable displayable = baseAdapter.getDisplayable(position);

				if (displayable == null) {
					return 1;
				} else {
					if (displayable.getSpanSize() <= getSpanCount()) {
						return displayable.getSpanSize();
					} else {
						CrashReports.logException(new IllegalArgumentException("Displayable " + displayable.getClass()
								.getSimpleName() + " at position " + position + " spanSize > getSpanCount()! "));
						return getSpanCount();
					}
				}
			}
		}
	}
}
