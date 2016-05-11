/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Locale;

import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewSubscriptionDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 10/05/16.
 */
@Displayables({AppViewSubscriptionDisplayable.class})
public class AppViewSubscriptionWidget extends Widget<AppViewSubscriptionDisplayable> {

	private ImageView storeAvatar;
	private TextView storeName;
	private TextView storeNumberUsers;
	private Button buttonSubscribe;

	public AppViewSubscriptionWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		storeAvatar = (ImageView) itemView.findViewById(R.id.store_avatar);
		storeName = (TextView) itemView.findViewById(R.id.store_name);
		storeNumberUsers = (TextView) itemView.findViewById(R.id.store_number_users);
		buttonSubscribe = (Button) itemView.findViewById(R.id.btn_subscribe);
	}

	@Override
	public void bindView(AppViewSubscriptionDisplayable displayable) {
		final Store store = displayable.getPojo().getStore();

		if(!TextUtils.isEmpty(store.getAvatar())) {
			Glide.with(storeAvatar.getContext()).load(store.getAvatar()).into(storeAvatar);
		}

		if(!TextUtils.isEmpty(store.getName())) {
			storeName.setText(store.getName());
		} else {
			storeName.setVisibility(View.GONE);
		}

		storeNumberUsers.setText(
				String.format(Locale.ROOT, "%d", store.getStats().getSubscribers())
		);

		buttonSubscribe.setOnClickListener(
				v -> ShowMessage.show(v, "TO DO")
		);

	}
}
