/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.v8engine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Iterator;
import java.util.List;

import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.model.v7.store.GetStoreTabs;
import cm.aptoide.pt.v8engine.fragment.implementations.AppsTimelineFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.SubscribedStoresFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.UpdatesFragment;

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

			//TODO Do NOT push this, only for testing while web service is not ready.
			if (next.getLabel().equals("Social Timeline")) {
				final Event event = new Event();
				event.setName(Event.Name.mySocialTimeline);
				event.setType(Event.Type.CLIENT);
				next.setEvent(event);
			}

			if (next.getEvent().getName() == null || next.getEvent().getType() == null) {
				iterator.remove();
			}
		}
	}

	@Override
	public Fragment getItem(int position) {

		Event event = tabs.get(position).getEvent();

		switch (event.getType()) {
			case API:
				return StoreTabGridRecyclerFragment.newInstance(event, tabs.get(position)
						.getLabel());
			case CLIENT:
				return caseClient(event);
			default:
				// Safe to throw exception as the tab should be filtered prior to getting here.
				throw new RuntimeException("Fragment type not implemented!");
		}
	}

	private Fragment caseClient(Event event) {
		switch (event.getName()) {
			case myStores:
				return SubscribedStoresFragment.newInstance();

			case myUpdates:
				return UpdatesFragment.newInstance();

			case mySocialTimeline:
				return AppsTimelineFragment.newInstance();

			default:
				// Safe to throw exception as the tab should be filtered prior to getting here.
				throw new RuntimeException("Fragment type not implemented!");
		}
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
