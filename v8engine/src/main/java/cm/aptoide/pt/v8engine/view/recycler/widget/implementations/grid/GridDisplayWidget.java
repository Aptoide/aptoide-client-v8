/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.HomeFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreGridRecyclerFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridDisplayDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by sithengineer on 02/05/16.
 */
@Displayables({GridDisplayDisplayable.class})
public class GridDisplayWidget extends Widget<GridDisplayDisplayable> {

	private ImageView imageView;

	public GridDisplayWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		imageView = (ImageView) itemView.findViewById(R.id.image_category);
	}

	@Override
	public void bindView(GridDisplayDisplayable displayable) {
		GetStoreDisplays.EventImage pojo = displayable.getPojo();
		ImageLoader.load(pojo.getGraphic(), imageView);

		imageView.setOnClickListener(v -> {
			Event event = pojo.getEvent();
			Event.Name name = event.getName();
			if (StoreTabGridRecyclerFragment.validateAcceptedName(name)) {
				FragmentUtils.replaceFragmentV4((FragmentActivity) itemView.getContext(), StoreGridRecyclerFragment.newInstance(event, pojo.getLabel(),
						displayable
						.getStoreTheme()));
			} else {
				switch (name) {
					case facebook:
						@Cleanup Realm realm = DeprecatedDatabase.get();
						Installed installedFacebook = DeprecatedDatabase.InstalledQ.get(HomeFragment.FACEBOOK_PACKAGE_NAME, realm);
						sendActionEvent(AptoideUtils.SocialLinksU.getFacebookPageURL(installedFacebook == null ? 0 : installedFacebook.getVersionCode(), event
								.getAction()));
						break;
					case twitch:
					case youtube:
					default:
						sendActionEvent(event.getAction());
						break;
				}
			}
		});
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}

	private void sendActionEvent(String eventActionUrl) {
		Intent i;
		if (eventActionUrl != null) {
			i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(eventActionUrl));
			itemView.getContext().startActivity(i);
		}
	}
}
