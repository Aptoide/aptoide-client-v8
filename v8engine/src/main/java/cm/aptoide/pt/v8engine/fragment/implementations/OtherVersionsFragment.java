/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppVersionsRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.util.AppBarStateChangeListener;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.OtherVersionDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.EndlessRecyclerOnScrollListener;

/**
 * Created by sithengineer on 05/07/16.
 */
public class OtherVersionsFragment extends GridRecyclerFragment {

	private static final String TAG = OtherVersionsFragment.class.getSimpleName();

	private static final String APP_NAME = "app_name";
	private static final String APP_IMG_URL = "app_img_url";
	private static final String APP_PACKAGE = "app_package";
	// vars
	private String appName;
	private String appImgUrl;
	private String appPackge;
	// views
	private ViewHeader header;
	//private TextView emptyData;

	// data
	private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

	/**
	 * @param appName
	 * @param appImgUrl
	 * @param appPackage
	 *
	 * @return
	 */
	public static OtherVersionsFragment newInstance(String appName, String appImgUrl, String appPackage) {
		OtherVersionsFragment fragment = new OtherVersionsFragment();
		Bundle args = new Bundle();
		args.putString(APP_NAME, appName);
		args.putString(APP_IMG_URL, appImgUrl);
		args.putString(APP_PACKAGE, appPackage);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {
		Logger.d(TAG, "Other versions should refresh? " + refresh);

		fetchOtherVersions();

		if (header != null) {
			header.setImage(appImgUrl);
			setTitle(appName);
		}
	}

	@Override
	public int getContentViewId() {
		return R.layout.fragment_other_versions;
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		header = new ViewHeader(view);
		//emptyData = (TextView) view.findViewById(R.id.empty_data);
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void loadExtras(Bundle args) {
		super.loadExtras(args);
		appName = args.getString(APP_NAME);
		appImgUrl = args.getString(APP_IMG_URL);
		appPackge = args.getString(APP_PACKAGE);
	}

	@Override
	public void setupToolbar() {
		super.setupToolbar();
		if (toolbar != null) {
			ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
		}
	}

	private void setTitle(String title) {
		if (toolbar != null) {
			ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
			bar.setTitle(title);
		}
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_empty, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void fetchOtherVersions() {

		final SuccessRequestListener<ListAppVersions> otherVersionsSuccessRequestListener = listAppVersions -> {
			List<App> apps = listAppVersions.getList();
			ArrayList<Displayable> displayables = new ArrayList<>(apps.size());
			for (final App app : apps) {
				displayables.add(new OtherVersionDisplayable(app));
			}
			addDisplayables(displayables);
		};

		endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(this.getAdapter(), ListAppVersionsRequest.of(appPackge),
				otherVersionsSuccessRequestListener, errorRequestListener, false);

		recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
		endlessRecyclerOnScrollListener.onLoadMore(false);
	}

	/*
	private void otherVersionsSuccessRequestListener(ListAppVersions listAppVersions) {
		List<App> apps = listAppVersions.getList();
		displayables = new ArrayList<>(apps.size());
		for (final App app : apps) {
			displayables.add(new OtherVersionDisplayable(app));
		}
		setDisplayables(displayables);
		//finishLoading();
	}
	*/


	//
	// micro widget for header
	//

	private static final class ViewHeader {

		private final boolean animationsEnabled;

		// views
		private final TextView otherVersionsTitle;
		private final AppBarLayout appBarLayout;
		private final CollapsingToolbarLayout collapsingToolbar;
		private final ImageView appIcon;

		private final SpannableString composedTitle1;
		private final SpannableString composedTitle2;

		// ctor
		public ViewHeader(@NonNull View view) {


			composedTitle1 = new SpannableString(view.getResources().getString(R.string.other_versions_partial_title_1));
			composedTitle1.setSpan(new StyleSpan(Typeface.ITALIC), 0, composedTitle1.length(), 0);

			composedTitle2 = new SpannableString(view.getResources().getString(R.string.other_versions_partial_title_2));
			composedTitle2.setSpan(new StyleSpan(Typeface.ITALIC), 0, composedTitle2.length(), 0);

			animationsEnabled = ManagerPreferences.getAnimationsEnabledStatus();

			otherVersionsTitle = (TextView) view.findViewById(R.id.other_versions_title);
			appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);
			collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
			appIcon = (ImageView) view.findViewById(R.id.app_icon);

			appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {

				@Override
				public void onStateChanged(AppBarLayout appBarLayout, State state) {
					switch (state) {
						case EXPANDED: {
							if (animationsEnabled) {
								appIcon.animate().alpha(1F).start();
							} else {
								appIcon.setVisibility(View.VISIBLE);
							}
							otherVersionsTitle.setVisibility(View.VISIBLE);
							break;
						}
						default:
						case IDLE:
						case COLLAPSED: {
							if (animationsEnabled) {
								appIcon.animate().alpha(0F).start();
							} else {
								appIcon.setVisibility(View.INVISIBLE);
							}
							otherVersionsTitle.setVisibility(View.INVISIBLE);
							break;
						}
					}
				}
			});
		}

		private void setImage(String imgUrl) {
			ImageLoader.load(imgUrl, appIcon);
		}
	}
}
