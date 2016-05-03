/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 30/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.utils.StringUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.CircleTransform;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.StoreGridDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 29/04/16.
 */
@Displayables({StoreGridDisplayable.class})
public class StoreGridWidget extends Widget<StoreGridDisplayable> {

	private static final String TAG = StoreGridWidget.class.getSimpleName();

	private ImageView storeAvatar;
	private TextView storeName;
	private TextView storeUnsubscribe;
	private LinearLayout storeLayout;
	private TextView storeSubscribers;
	private TextView storeDownloads;
	//private LinearLayout infoLayout;

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
		//infoLayout = (LinearLayout) itemView.findViewById(R.id.store_layout_subscribers);
	}

	@Override
	public void bindView(StoreGridDisplayable storeGridDisplayable) {

		final Context context = itemView.getContext();
		final Store store = storeGridDisplayable.getPojo();

		storeName.setText(store.getName());
		storeDownloads.setText(StringUtils.withSuffix(store.getStats().getDownloads()));
		storeSubscribers.setText(StringUtils.withSuffix(store.getStats().getSubscribers()));

		// in order to re-use the row_store_item layout, we hide the unsubscribe button and
		// increase the padding
		storeUnsubscribe.setVisibility(View.GONE);

		//final EnumStoreTheme themeIz = EnumStoreTheme.get(store.getAppearance().getTheme());
		//@ColorInt int color = context.getResources().getColor(themeIz.getStoreHeader());
		@ColorInt int color = context.getResources().getColor(R.color.blue);
		storeLayout.setBackgroundColor(color);
		/*
		storeLayout.setOnClickListener(()->{
			Snackbar.make()
		});
		*/

		if (store.getId() == -1 || TextUtils.isEmpty(store.getAvatar())) {
			Glide.with(context)
					.fromResource()
					.load(R.drawable.ic_avatar_apps)
					.transform(new CircleTransform(context))
					.into(storeAvatar);
		} else {
			Glide.with(context)
					.load(store.getAvatar())
					.transform(new CircleTransform(context))
					.into(storeAvatar);
		}
	}
}
