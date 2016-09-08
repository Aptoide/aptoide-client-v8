/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.v8engine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.model.v7.store.GetStoreTabs;
import cm.aptoide.pt.v8engine.fragment.implementations.AppsTimelineFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.DownloadsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.LatestReviewsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.SubscribedStoresFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.UpdatesFragment;
import cm.aptoide.pt.v8engine.util.Translator;

/**
 * Created by neuro on 28-04-2016.
 */
public class StorePagerAdapter extends FragmentStatePagerAdapter {

	private final List<GetStoreTabs.Tab> tabs;
	private final EnumMap<Event.Name,Integer> availableEventsMap = new EnumMap<>(Event.Name.class);
	private String storeTheme;
	private long storeId;

	public StorePagerAdapter(FragmentManager fm, GetStore getStore) {
		super(fm);
		this.storeId = getStore.getNodes().getMeta().getData().getId();
		tabs = getStore.getNodes().getTabs().getList();
		translateTabs(tabs);
		if (getStore.getNodes().getMeta().getData().getId() != 15) {
			storeTheme = getStore.getNodes().getMeta().getData().getAppearance().getTheme();
		}
		validateGetStore();

		fillAvailableEventsMap(getStore);
	}

	private void translateTabs(List<GetStoreTabs.Tab> tabs){
		for(GetStoreTabs.Tab t : tabs)
			t.setLabel(Translator.translate(t.getLabel()));
	}

	private void fillAvailableEventsMap(GetStore getStore) {
		List<GetStoreTabs.Tab> list = getStore.getNodes().getTabs().getList();
		for (int i = 0 ; i < list.size() ; i++) {
			Event event = list.get(i).getEvent();

			if (!containsEventName(event.getName())) {
				availableEventsMap.put(event.getName(), i);
			}
		}

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

		GetStoreTabs.Tab tab = tabs.get(position);
		Event event = tab.getEvent();

		switch (event.getType()) {
			case API:
				return caseAPI(tab);
			case CLIENT:
				return caseClient(event);
			case v3:
				return caseV3(event);
			default:
				// Safe to throw exception as the tab should be filtered prior to getting here.
				throw new RuntimeException("Fragment type not implemented!");
		}
	}

	public Event.Name getEventName(int position) {
		return tabs.get(position).getEvent().getName();
	}

	private Fragment caseAPI(GetStoreTabs.Tab tab) {
		Event event = tab.getEvent();
		switch (event.getName()) {
			case getUserTimeline:
				return AppsTimelineFragment.newInstance(event.getAction());
			default:
				return StoreTabGridRecyclerFragment.newInstance(event, tab.getLabel(), storeTheme);
		}
	}

	public boolean containsEventName(Event.Name name) {
		return availableEventsMap.containsKey(name);
	}

	public Integer getEventNamePosition(Event.Name name) {
		return availableEventsMap.get(name);
	}

	private Fragment caseClient(Event event) {
		switch (event.getName()) {
			case myStores:
				return SubscribedStoresFragment.newInstance();
			case myUpdates:
				return UpdatesFragment.newInstance();
			case myDownloads:
				return DownloadsFragment.newInstance();
			default:
				// Safe to throw exception as the tab should be filtered prior to getting here.
				throw new RuntimeException("Fragment type not implemented!");
		}
	}

	private Fragment caseV3(Event event) {
		switch (event.getName()) {
			case getReviews:
				return LatestReviewsFragment.newInstance(storeId);
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
