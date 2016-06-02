/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/06/2016.
 */

package cm.aptoide.pt.v8engine.util;

import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.implementations.GlobalSearchFragment;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchUtils {

	public static void setupSearch(Menu menu, FragmentManager fragmentManager) {
		setupSearch(menu.findItem(R.id.action_search), fragmentManager);
	}

	public static void setupSearch(MenuItem searchItem, FragmentManager fragmentManager) {
//		final MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//		final android.app.SearchManager searchManager = (android.app.SearchManager) activity.getSystemService(Context
// .SEARCH_SERVICE);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				MenuItemCompat.collapseActionView(searchItem);
				System.out.println("onQueryTextSubmit: " + s);

				boolean validQueryLenght = s.length() > 1;

				if (validQueryLenght) {
					FragmentUtils.replaceFragment(fragmentManager, GlobalSearchFragment.newInstance(s));
				} else {
					ShowMessage.toast(V8Engine.getContext(), R.string.search_minimum_chars);
				}

				return validQueryLenght;
			}

			@Override
			public boolean onQueryTextChange(String s) {
				System.out.println("onQueryTextChange: " + s);
				return false;
			}
		});

		searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (!hasFocus) {
					MenuItemCompat.collapseActionView(searchItem);
//					isSocketDisconnect = true;

					if (Build.VERSION.SDK_INT > 7) {

						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {

//								if (isSocketDisconnect) {
//									WebSocketSingleton.getInstance().disconnect();
//								}

							}
						}, 10000);
					}
				}
			}
		});

		searchView.setOnSearchClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				isSocketDisconnect = false;

//                FlurryAgent.logEvent("Clicked_On_Search_Button");

				if (Build.VERSION.SDK_INT > 7) {
//					WebSocketSingleton.getInstance().connect();
				} else {
//					activity.onSearchRequested();
					MenuItemCompat.collapseActionView(searchItem);
				}
			}
		});

		if (Build.VERSION.SDK_INT > 7) {
//			searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
		}
	}
}
