/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewSubscriptionDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by sithengineer on 10/05/16.
 */
@Displayables({AppViewSubscriptionDisplayable.class})
public class AppViewSubscriptionWidget extends Widget<AppViewSubscriptionDisplayable> {

	private String TAG = AppViewSubscriptionWidget.class.getSimpleName();

	private ImageView storeAvatar;
	private TextView storeNameTextView;
	private TextView storeNumberUsers;
	private Button buttonSubscribe;

	public AppViewSubscriptionWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		storeAvatar = (ImageView) itemView.findViewById(R.id.store_avatar);
		storeNameTextView = (TextView) itemView.findViewById(R.id.store_name);
		storeNumberUsers = (TextView) itemView.findViewById(R.id.store_number_users);
		buttonSubscribe = (Button) itemView.findViewById(R.id.btn_subscribe);
	}

	boolean containsThisStore;
	@Override
	public void bindView(AppViewSubscriptionDisplayable displayable) {
		try {
			final Store appMeta = displayable.getPojo().getNodes().getMeta().getData().getStore();

			if(!TextUtils.isEmpty(appMeta.getAvatar())) {
				ImageLoader.load(appMeta.getAvatar(), storeAvatar);
			}

			final String storeName = appMeta.getName();
			if(!TextUtils.isEmpty(storeName)) {
				storeNameTextView.setText(storeName);
			} else {
				storeNameTextView.setVisibility(View.GONE);
			}

			storeNumberUsers.setText(
					String.format(Locale.ROOT, "%d", appMeta.getStats().getSubscribers())
			);

			@Cleanup Realm realm = Realm.getDefaultInstance();
			containsThisStore = Database.StoreQ.contains(storeName, realm);

			buttonSubscribe.setText(containsThisStore ? R.string.unsubscribe : R.string.subscribe);

			buttonSubscribe.setOnClickListener(
					v -> {

						if(containsThisStore){
							AptoideAccountManager.unsubscribeStore(storeName);
						}
						else {
							AptoideAccountManager.subscribeStore(storeName);
						}

						containsThisStore = !containsThisStore;
						buttonSubscribe.setText(containsThisStore ? R.string.unsubscribe : R.string.subscribe);
					}
			);
		} catch (NullPointerException ex) {
			Log.e(TAG, "", ex);
		}

	}
}
