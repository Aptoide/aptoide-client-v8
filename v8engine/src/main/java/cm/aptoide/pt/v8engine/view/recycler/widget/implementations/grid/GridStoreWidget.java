/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreFragment;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 29/04/16.
 */
@Displayables({GridStoreDisplayable.class})
public class GridStoreWidget extends Widget<GridStoreDisplayable> {

	private static final String TAG = GridStoreWidget.class.getSimpleName();

	private ImageView storeAvatar;
	private TextView storeName;
	private TextView storeUnsubscribe;
	private LinearLayout storeLayout;
	private TextView storeSubscribers;
	private TextView storeDownloads;
	//private LinearLayout infoLayout;

	public GridStoreWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		storeAvatar = (ImageView) itemView.findViewById(R.id.store_avatar_row);
		storeName = (TextView) itemView.findViewById(R.id.store_name_row);
		storeUnsubscribe = (TextView) itemView.findViewById(R.id.store_unsubscribe_row);
		storeLayout = (LinearLayout) itemView.findViewById(R.id.store_main_layout_row);
		storeSubscribers = (TextView) itemView.findViewById(R.id.store_subscribers);
		storeDownloads = (TextView) itemView.findViewById(R.id.store_downloads);
		//infoLayout = (LinearLayout) itemView.findViewById(R.id.store_layout_subscribers);
	}

	@Override
	public void bindView(GridStoreDisplayable gridStoreDisplayable) {

		final Context context = itemView.getContext();
		final Store store = gridStoreDisplayable.getPojo();

		storeName.setText(store.getName());
		storeDownloads.setText(AptoideUtils.StringU.withSuffix(store.getStats().getDownloads()));
		storeSubscribers.setText(AptoideUtils.StringU.withSuffix(store.getStats().getSubscribers()));

		// in order to re-use the row_store_item layout, we hide the unsubscribe button and
		// increase the padding
		storeUnsubscribe.setVisibility(View.GONE);

		storeLayout.setBackgroundColor(StoreThemeEnum.get(store).getStoreHeaderInt());
		storeLayout.setOnClickListener(v -> FragmentUtils.replaceFragment((FragmentActivity) v
				.getContext(), StoreFragment
				.newInstance(gridStoreDisplayable.getPojo().getName())));

		if (store.getId() == -1 || TextUtils.isEmpty(store.getAvatar())) {
			ImageLoader.loadWithCircleTransform(R.drawable.ic_avatar_apps, storeAvatar);
		} else {
			ImageLoader.loadWithCircleTransform(store.getAvatar(), storeAvatar);
		}
	}
}
