/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/06/2016.
 */

package cm.aptoide.pt.v8engine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import cm.aptoide.pt.v8engine.fragment.implementations.SearchPagerFragment;

/**
 * Created by neuro on 28-04-2016.
 */
public class SearchPagerAdapter extends FragmentStatePagerAdapter {

	private final String query;

	public SearchPagerAdapter(FragmentManager fm, String query) {
		super(fm);
		this.query = query;
	}

	@Override
	public Fragment getItem(int position) {
		if (position == 0) {
			return SearchPagerFragment.newInstance(query, true);
		} else if (position == 1) {
			return SearchPagerFragment.newInstance(query, false);
		} else {
			throw new IllegalArgumentException("SearchPagerAdapter should have 2 and only 2 pages!");
		}
	}

	@Override
	public int getCount() {
		return 2;
	}
}
