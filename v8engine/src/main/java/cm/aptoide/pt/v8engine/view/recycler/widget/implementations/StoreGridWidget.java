/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 30/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.StoreGridDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 29/04/16.
 */
public class StoreGridWidget extends Widget<StoreGridDisplayable> {

	private static final String TAG = StoreGridWidget.class.getSimpleName();

	private ImageView storeAvatar;
	private TextView storeName;
	private TextView storeUnsubscribe;
	private LinearLayout storeLayout;
	private TextView storeSubscribers;
	private TextView storeDownloads;
	private LinearLayout infoLayout;

	public StoreGridWidget(View itemView) {
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
		infoLayout = (LinearLayout) itemView.findViewById(R.id.store_layout_subscribers);
	}

	@Override
	public void bindView(StoreGridDisplayable storeGridDisplayable) {
		Log.w(TAG, "TODO bindView()");

		/*
		final EnumStoreTheme themeIz = EnumStoreTheme.get(storeGridDisplayable.theme);

		storeName.setText(storeGridDisplayable.repoName);
		storeDownloads.setText(AptoideUtils.StringUtils.withSuffix(storeGridDisplayable.storeDwnNumber));
		storeSubscribers.setText(AptoideUtils.StringUtils.withSuffix(storeGridDisplayable.storeSubscribers));

		// in order to re-use the row_store_item layout, we hide the unsubscribe button and increase the padding
		storeUnsubscribe.setVisibility(View.GONE);

		final Context context = itemView.getContext();
		@ColorInt int color = context.getResources().getColor(themeIz.getStoreHeader());
		storeLayout.setBackgroundColor(color);
		storeLayout.setOnClickListener(()->{
			Snackbar.make()
		});

		if (storeGridDisplayable.id == -1 || TextUtils.isEmpty(storeGridDisplayable.avatar)) {
			Glide.with(context).fromResource().load(R.drawable.ic_avatar_apps)
					.transform(new CircleTransform(context)).into(storeAvatar);
		} else {
			Glide.with(context).load(storeGridDisplayable.avatar).transform(new CircleTransform(context))
					.into(storeAvatar);
		}
		*/

	}
}
