/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreFragment;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SubscribedStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by neuro on 11-05-2016. //todo: código duplicado, se cair a reflexão, deixa de o ser.
 */
@Displayables({SubscribedStoreDisplayable.class})
public class SubscribedStoreWidget extends Widget<SubscribedStoreDisplayable> {

	private static final String TAG = GridStoreWidget.class.getSimpleName();

	private ImageView storeAvatar;
	private TextView storeName;
	private TextView storeUnsubscribe;
	private LinearLayout storeLayout;
	private View infoLayout;

	public SubscribedStoreWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		storeAvatar = (ImageView) itemView.findViewById(R.id.store_avatar_row);
		storeName = (TextView) itemView.findViewById(R.id.store_name_row);
		storeUnsubscribe = (TextView) itemView.findViewById(R.id.store_unsubscribe_row);
		storeLayout = (LinearLayout) itemView.findViewById(R.id.store_main_layout_row);
		infoLayout = itemView.findViewById(R.id.store_layout_subscribers);
	}

	@Override
	public void bindView(SubscribedStoreDisplayable displayable) {

		final Context context = itemView.getContext();
		final Store store = displayable.getPojo();

		storeName.setText(store.getStoreName());
		infoLayout.setVisibility(View.GONE);

		@ColorInt int color = context.getResources().getColor(StoreThemeEnum.get(store.getTheme()).getStoreHeader());
		storeLayout.setBackgroundColor(color);
		storeLayout.setOnClickListener(v->FragmentUtils.replaceFragment((FragmentActivity) v.getContext(),
				StoreFragment
				.newInstance(displayable.getPojo().getStoreName())));

		if (store.getStoreId() == -1 || TextUtils.isEmpty(store.getIconPath())) {
			ImageLoader.loadWithCircleTransform(R.drawable.ic_avatar_apps, storeAvatar);
		} else {
			ImageLoader.loadWithCircleTransform(store.getIconPath(), storeAvatar);
		}

		storeUnsubscribe.setOnClickListener(v->{
			GenericDialogs.createGenericYesNoCancelMessage(itemView.getContext(), displayable.getPojo()
					.getStoreName(), AptoideUtils.StringU.getFormattedString(R.string
					.unsubscribe_yes_no))
					.subscribe(eResponse->{
						switch (eResponse) {
							case YES:
								@Cleanup Realm realm = Database.get(itemView.getContext());

								if (AptoideAccountManager.isLoggedIn()) {
									AptoideAccountManager.unsubscribeStore(store.getStoreName());
								}

								Database.StoreQ.delete(store.getStoreId(), realm);

								break;
						}
					});
		});
	}
}