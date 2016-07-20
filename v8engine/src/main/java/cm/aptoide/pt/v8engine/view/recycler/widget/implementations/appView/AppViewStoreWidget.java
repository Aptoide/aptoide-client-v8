/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.StoreUtils;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreFragment;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by sithengineer on 10/05/16.
 */
@Displayables({AppViewStoreDisplayable.class})
public class AppViewStoreWidget extends Widget<AppViewStoreDisplayable> {

	private ImageView storeAvatarView;
	private TextView storeNameView;
	private TextView storeNumberUsersView;
	private Button followButton;
	private View storeLayout;

	public AppViewStoreWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		storeAvatarView = ((ImageView) itemView.findViewById(R.id.store_avatar));
		storeNameView = ((TextView) itemView.findViewById(R.id.store_name));
		storeNumberUsersView = ((TextView) itemView.findViewById(R.id.store_number_users));
		followButton = ((Button) itemView.findViewById(R.id.follow_store_btn));
		storeLayout = itemView.findViewById(R.id.store_layout);
	}

	@Override
	public void bindView(AppViewStoreDisplayable displayable) {
		setupStoreInfo(displayable.getPojo());
	}

	@Override
	public void unbindView() {

	}

	private void setupStoreInfo(GetApp getApp) {

		GetAppMeta.App app = getApp.getNodes().getMeta().getData();
		Store store = app.getStore();

		if (TextUtils.isEmpty(store.getAvatar())) {
			ImageLoader.loadWithCircleTransform(R.drawable.ic_avatar_apps, storeAvatarView);
		} else {
			ImageLoader.loadWithCircleTransform(store.getAvatar(), storeAvatarView);
		}

		StoreThemeEnum storeThemeEnum = StoreThemeEnum.get(store);

		storeNameView.setText(store.getName());
		storeNameView.setTextColor(storeThemeEnum.getStoreHeaderInt());
		storeNumberUsersView.setText(String.format(Locale.getDefault(), V8Engine.getContext().getString(R.string.appview_followers_count_text), store
				.getStats()
				.getSubscribers()));
		followButton.setBackgroundDrawable(storeThemeEnum.getButtonLayoutDrawable());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			followButton.setElevation(0);
		}
		followButton.setTextColor(storeThemeEnum.getStoreHeaderInt());
		storeLayout.setOnClickListener(new Listeners().newOpenStoreListener(itemView, store.getName()));

		@Cleanup
		Realm realm = Database.get();
		boolean subscribed = Database.StoreQ.get(store.getId(), realm) != null;

		if (subscribed) {
			//int checkmarkDrawable = storeThemeEnum.getCheckmarkDrawable();
			//followButton.setCompoundDrawablesWithIntrinsicBounds(checkmarkDrawable, 0, 0, 0);
			followButton.setText(R.string.followed);
			followButton.setOnClickListener(new Listeners().newOpenStoreListener(itemView, store.getName()));
		} else {
			//int plusMarkDrawable = storeThemeEnum.getPlusmarkDrawable();
			//followButton.setCompoundDrawablesWithIntrinsicBounds(plusMarkDrawable, 0, 0, 0);
			followButton.setText(R.string.appview_follow_store_button_text);
			followButton.setOnClickListener(new Listeners().newSubscribeStoreListener(itemView, store.getName()));
		}
	}

	private static class Listeners {

		private View.OnClickListener newOpenStoreListener(View itemView, String storeName) {
			return v -> {
				FragmentUtils.replaceFragmentV4((FragmentActivity) itemView.getContext(), StoreFragment.newInstance(storeName));
			};
		}

		private View.OnClickListener newSubscribeStoreListener(View itemView, String storeName) {
			return v -> {
				StoreUtils.subscribeStore(storeName, getStoreMeta -> {
					ShowMessage.asSnack(itemView, AptoideUtils.StringU.getFormattedString(R.string.store_subscribed, storeName));
				}, Throwable::printStackTrace);
			};
		}
	}
}
