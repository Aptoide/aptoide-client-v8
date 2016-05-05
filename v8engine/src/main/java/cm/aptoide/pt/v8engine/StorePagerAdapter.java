/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 05/05/2016.
 */

package cm.aptoide.pt.v8engine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Iterator;
import java.util.List;

import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.model.v7.store.GetStoreTabs;
import cm.aptoide.pt.v8engine.fragments.implementations.StoreTabGridRecyclerFragment;

/**
 * Created by neuro on 28-04-2016.
 */
public class StorePagerAdapter extends FragmentStatePagerAdapter {

	private final List<GetStoreTabs.Tab> tabs;

	public StorePagerAdapter(FragmentManager fm, GetStore getStore) {
		super(fm);
		tabs = getStore.getNodes().getTabs().getList();
		validateGetStore();
	}

	private void validateGetStore() {
		Iterator<GetStoreTabs.Tab> iterator = tabs.iterator();
		while (iterator.hasNext()) {
			GetStoreTabs.Tab next = iterator.next();
			if (next.getEvent().getName() == null || next.getEvent().getType() == null) {
				iterator.remove();
			}
		}
	}

	@Override
	public Fragment getItem(int position) {
		return StoreTabGridRecyclerFragment.newInstance(tabs.get(position).getEvent());
	}

	@Override
	public int getCount() {
		return tabs.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabs.get(position).getLabel();
	}
}
