/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.ThemeUtils;
import cm.aptoide.pt.v8engine.util.Translator;

/**
 * Created by neuro on 10-05-2016.
 */
public class StoreGridRecyclerFragment extends StoreTabGridRecyclerFragment {

	public static StoreGridRecyclerFragment newInstance(Event event, String
			title, String theme) {
		Bundle args = buildBundle(event, title, theme);

		StoreGridRecyclerFragment fragment = new StoreGridRecyclerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public static StoreGridRecyclerFragment newInstance(Event event, String
			title) {
		Bundle args = buildBundle(event, title);

		StoreGridRecyclerFragment fragment = new StoreGridRecyclerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void setupToolbar() {
		// It's not calling super cause it does nothing in the middle class}
		// StoreTabGridRecyclerFragment.
		if (toolbar != null) {
			((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
			((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Translator.translate(title));
			((AppCompatActivity) getActivity()).getSupportActionBar()
					.setDisplayHomeAsUpEnabled(true);
			toolbar.setLogo(R.drawable.ic_aptoide_toolbar);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_empty, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public int getContentViewId() {
		return R.layout.recycler_swipe_fragment_with_toolbar;
	}

	@Override
	public void setupViews() {
		super.setupViews();
		setupToolbar();
		setHasOptionsMenu(true);
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
			savedInstanceState) {
		if(storeTheme != null) {
			ThemeUtils.setStoreTheme(getActivity(), storeTheme);
			ThemeUtils.setStatusBarThemeColor(getActivity(), StoreThemeEnum.get(storeTheme));
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}


}
