/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.v8engine.util;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.activities.SearchActivity;
import cm.aptoide.pt.v8engine.fragment.implementations.GlobalSearchFragment;
import cm.aptoide.pt.v8engine.websocket.WebSocketSingleton;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchUtils {

	public static void setupGlobalSearchView(Menu menu, FragmentActivity fragmentActivity) {
		setupSearchView(menu.findItem(R.id.action_search), fragmentActivity, true);
	}

	public static void setupStoreSearchView(Menu menu, FragmentActivity fragmentActivity) {
		setupSearchView(menu.findItem(R.id.action_search), fragmentActivity, false);
	}

	public static void setupSearchView(MenuItem searchItem, FragmentActivity fragmentActivity, boolean
			searchInOtherStores) {

		// Get the SearchView and set the searchable configuration
		final SearchManager searchManager = (SearchManager) V8Engine.getContext()
				.getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		ComponentName cn = new ComponentName(V8Engine.getContext(), SearchActivity.class);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				MenuItemCompat.collapseActionView(searchItem);

				boolean validQueryLenght = s.length() > 1;

				if (validQueryLenght) {
					FragmentUtils.replaceFragmentV4(fragmentActivity, GlobalSearchFragment.newInstance(s,
							searchInOtherStores));
				} else {
					ShowMessage.toast(V8Engine.getContext(), R.string.search_minimum_chars);
				}

				return true;
			}

			@Override
			public boolean onQueryTextChange(String s) {
				return false;
			}
		});

		searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
			@Override
			public boolean onSuggestionSelect(int position) {
				return false;
			}

			@Override
			public boolean onSuggestionClick(int position) {
				Cursor item = (Cursor) searchView.getSuggestionsAdapter().getItem(position);

				FragmentUtils.replaceFragmentV4(fragmentActivity, GlobalSearchFragment.newInstance(item.getString(1),
						searchInOtherStores));

				return true;
			}
		});

		searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {

			if (!hasFocus) {
				MenuItemCompat.collapseActionView(searchItem);

				WebSocketSingleton.getInstance().disconnect();
			}
		});

		searchView.setOnSearchClickListener(v -> WebSocketSingleton.getInstance().connect());

	}
}
