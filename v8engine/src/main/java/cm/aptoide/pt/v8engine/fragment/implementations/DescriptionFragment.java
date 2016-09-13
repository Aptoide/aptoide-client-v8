/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 07/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.BaseLoaderToolbarFragment;

/**
 * Created by sithengineer on 28/06/16.
 */
public class DescriptionFragment extends BaseLoaderToolbarFragment {

	private static final String TAG = DescriptionFragment.class.getSimpleName();

	private static final String APP_ID = "app_id";
	private static final String STORE_NAME = "store_name";
	private boolean hasAppId = false;
	private long appId;
	private TextView emptyData;
	private TextView descriptionContainer;
	private String storeName;

	public static DescriptionFragment newInstance(long appId, String storeName) {
		DescriptionFragment fragment = new DescriptionFragment();
		Bundle args = new Bundle();
		args.putLong(APP_ID, appId);
		args.putString(STORE_NAME, storeName);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void loadExtras(Bundle args) {
		super.loadExtras(args);

		if (args.containsKey(APP_ID)) {
			appId = args.getLong(APP_ID, -1);
			hasAppId = true;
		}

		if(args.containsKey(STORE_NAME)){
			storeName = args.getString(STORE_NAME);
		}
	}

	@Override
	protected int getViewToShowAfterLoadingId() {
		return R.id.data_container;
	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {
		if (hasAppId) {
			Logger.d(TAG, "App Description should refresh? " + refresh);
			GetAppRequest.of(appId, storeName).execute(getApp -> {
				setupAppDescription(getApp);
				setupTitle(getApp);
				finishLoading();
			}, refresh);
		} else {
			Logger.e(TAG, "App id unavailable");
			setDataUnavailable();
		}
	}

	@Override
	public void setupToolbar() {
		super.setupToolbar();
		if (toolbar != null) {
			ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		emptyData = (TextView) view.findViewById(R.id.empty_data);
		descriptionContainer = (TextView) view.findViewById(R.id.data_container);
		setHasOptionsMenu(true);
	}

	private void setupTitle(GetApp getApp) {
		try {
			String appName = getApp.getNodes().getMeta().getData().getName();
			if (!TextUtils.isEmpty(appName)) {
				if (toolbar != null) {
					ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
					bar.setTitle(appName);
					return;
				}
			}
		} catch (NullPointerException e) {
			Logger.e(TAG, e);
		}
		setDataUnavailable();
	}

	private void setupAppDescription(GetApp getApp) {
		try {
			GetAppMeta.Media media = getApp.getNodes().getMeta().getData().getMedia();
			if (!TextUtils.isEmpty(media.getDescription())) {
				descriptionContainer.setText(AptoideUtils.HtmlU.parse(media.getDescription()));
				return;
			}
		} catch (NullPointerException e) {
			Logger.e(TAG, e);
		}
		setDataUnavailable();
	}

	private void setDataUnavailable() {
		emptyData.setVisibility(View.VISIBLE);
		descriptionContainer.setVisibility(View.GONE);
	}

	@Override
	public int getContentViewId() {
		return R.layout.fragment_app_view_description;
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
}
