/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 21/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.LinkedList;
import java.util.Locale;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewDescriptionDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewDeveloperDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewInstallDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewOtherVersionsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewRateThisDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewScreenshotsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewSuggestedAppsDisplayable;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewFragment extends GridRecyclerFragment {

	//
	// constants
	//

	public static final int VIEW_ID = R.layout.fragment_app_view;
	//private static final String TAG = AppViewFragment.class.getName();

	//
	// vars
	//
	private AppViewHeader header;
	//	private GetAppMeta.App app;
	private long appId;

	//
	// static fragment default new instance method
	//

	public static AppViewFragment newInstance(long appId) {
		Bundle bundle = new Bundle();
		bundle.putLong(BundleKeys.APP_ID.name(), appId);

		AppViewFragment fragment = new AppViewFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void load(boolean refresh) {
		GetAppRequest.of(appId).execute(getApp -> {
			header.setup(getApp);
			setupDisplayables(getApp);
			setupObservables(getApp);
			finishLoading();
		});

//		if (refresh) {
//			loadAppInfo((int) appId)
//					.compose(ObservableUtils.applySchedulers())
//					.subscribe(
//						new Action1WithWeakRef<GetApp, AppViewFragment>(this) {
//						@Override
//						public void call(GetApp pojo) {
//							AppViewFragment fragment = weakReference.get();
//							if(fragment!=null) {
//								fragment.setApp(pojo.getNodes().getMeta().getData());
//								fragment.showAppInfo();
//							}
//						}
//					});
//		}
	}

	private void setupObservables(GetApp getApp) {
		// For stores subscription
		Database.StoreQ.getAll(realm)
				.asObservable()
				.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
				.subscribe(stores -> {
					if (Database.StoreQ.get(getApp.getNodes().getMeta().getData().getStore().getId(), realm) != null) {
						adapter.notifyDataSetChanged();
					}
				});

		// For install actions
		Database.RollbackQ.getAll(realm)
				.asObservable()
				.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
				.subscribe(rollbacks -> {
					adapter.notifyDataSetChanged();
				});

		// // TODO: 27-05-2016 neuro install actions, not present in v7
	}

	private void setupDisplayables(GetApp getApp) {
		LinkedList<Displayable> displayables = new LinkedList<>();

		GetAppMeta.App app = getApp.getNodes().getMeta().getData();

		displayables.add(new AppViewInstallDisplayable(getApp));
		displayables.add(new AppViewDescriptionDisplayable(getApp));
		displayables.add(new AppViewScreenshotsDisplayable(app));
		displayables.add(new AppViewRateThisDisplayable(getApp));
		displayables.add(new AppViewSuggestedAppsDisplayable(getApp));
		displayables.add(new AppViewCommentsDisplayable(getApp));
		displayables.add(new AppViewOtherVersionsDisplayable(getApp));
		displayables.add(new AppViewDeveloperDisplayable(getApp));

		setDisplayables(displayables);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public int getContentViewId() {
		return VIEW_ID;
	}

	@Override
	public void setupViews() {
		super.setupViews();
//		this.showAppInfo();

		final AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
		ActionBar supportActionBar = parentActivity.getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		header = new AppViewHeader(view);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_appview_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int i = item.getItemId();

		if (i == android.R.id.home) {
			getActivity().onBackPressed();
			return true;

		} else if (i == R.id.menu_share) {
			ShowMessage.show(item.getActionView(), "TO DO");

			// TODO

			return true;

		} else if (i == R.id.menu_schedule) {
			ShowMessage.show(item.getActionView(), "TO DO");

			// TODO
			return true;

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void loadExtras(Bundle args) {
		super.loadExtras(args);
		appId = args.getLong(BundleKeys.APP_ID.name());
	}

//	private Observable<GetApp> loadAppInfo(int appId) {
//		return GetAppRequest.of(appId).observe();
//	}

//	private void setApp(GetAppMeta.App app) {
//		this.app = app;
//	}

//	private void showAppInfo() {
//		if(app==null) return;
//
//		// setup displayables in view
//		addDisplayables(DisplayableType.newDisplayables(DisplayableType.Group.APP_VIEW, app));
//
//		// setup header in view
//		header.setup(app);
//	}

	//
	// bundle keys used internally in this fragment
	//

	private enum BundleKeys {
		APP_ID
	}

	//
	// micro widget for header
	//

	private static final class AppViewHeader {

		// views
		private CollapsingToolbarLayout collapsingToolbar;
		private ImageView featuredGraphic;
		private RelativeLayout badgeLayout;
		private ImageView badge;
		private TextView badgeText;
		private ImageView appIcon;
		private RatingBar ratingBar;
		private TextView fileSize;
		private TextView versionName;
		private TextView downloadsCount;

		// ctor
		public AppViewHeader(@NonNull View view) {
			collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
			featuredGraphic = (ImageView) view.findViewById(R.id.featured_graphic);
			badgeLayout = (RelativeLayout) view.findViewById(R.id.badge_layout);
			badge = (ImageView) view.findViewById(R.id.badge_img);
			badgeText = (TextView) view.findViewById(R.id.badge_text);
			appIcon = (ImageView) view.findViewById(R.id.app_icon);
			ratingBar = (RatingBar) view.findViewById(R.id.rating_bar_top);
			fileSize = (TextView) view.findViewById(R.id.file_size);
			versionName = (TextView) view.findViewById(R.id.version_name);
			downloadsCount = (TextView) view.findViewById(R.id.downloads_count);
		}

		// setup methods
		public void setup(@NonNull GetApp getApp) {

			if (getApp.getNodes().getMeta().getData().getGraphic() != null) {
				ImageLoader.load(getApp.getNodes().getMeta().getData().getGraphic(), featuredGraphic);
			}
			/*
			else if (screenshots != null && screenshots.size() > 0 && !TextUtils.isEmpty
			(screenshots.get(0).url)) {
				ImageLoader.load(screenshots.get(0).url, mFeaturedGraphic);
			}
			*/

			if (getApp.getNodes().getMeta().getData().getIcon() != null) {
				ImageLoader.load(getApp.getNodes().getMeta().getData().getIcon(), appIcon);
			}

			// TODO add placeholders in image loading

			collapsingToolbar.setTitle(getApp.getNodes().getMeta().getData().getName());
			ratingBar.setRating(getApp.getNodes().getMeta().getData().getStats().getRating().getAvg());
			fileSize.setText(String.format(Locale.ROOT, "%d", getApp.getNodes()
					.getMeta()
					.getData()
					.getFile()
					.getFilesize()));
			versionName.setText(getApp.getNodes().getMeta().getData().getFile().getVername());
			downloadsCount.setText(String.format(Locale.ROOT, "%d", getApp.getNodes().getMeta().getData().getStats()
					.getDownloads()));

			@DrawableRes int badgeResId = 0;
			@StringRes int badgeMessageId = 0;
			switch (getApp.getNodes().getMeta().getData().getFile().getMalware().getRank()) {
				case TRUSTED:
					badgeResId = R.drawable.ic_badge_trusted;
					badgeMessageId = R.string.appview_header_trusted_text;
					break;

				case WARNING:
					badgeResId = R.drawable.ic_badge_warning;
					badgeMessageId = R.string.warning;
					break;

				default:
				case UNKNOWN:
					badgeResId = R.drawable.ic_badge_unknown;
					badgeMessageId = R.string.unknown;
					break;
			}

			ImageLoader.load(badgeResId, badge);
			badgeText.setText(badgeMessageId);
		}

	}
}

