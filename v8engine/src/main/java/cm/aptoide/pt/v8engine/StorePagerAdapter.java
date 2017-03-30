/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.v8engine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.store.GetStoreTabs;
import cm.aptoide.pt.v8engine.util.Translator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by neuro on 28-04-2016.
 */
public class StorePagerAdapter extends FragmentStatePagerAdapter {

  private final List<GetStoreTabs.Tab> tabs;
  private final StoreContext storeContext;
  private final EnumMap<Event.Name, Integer> availableEventsMap = new EnumMap<>(Event.Name.class);
  private String storeTheme;
  private Long storeId;

  public StorePagerAdapter(FragmentManager fm, List<GetStoreTabs.Tab> tabs,
      StoreContext storeContext, Long storeId, String storeTheme) {
    super(fm);
    this.storeId = storeId;
    if (storeId != null && storeId != 15) {
      this.storeTheme = storeTheme;
    }
    this.tabs = tabs;
    this.storeContext = storeContext;
    translateTabs(this.tabs);
    validateGetStore();

    fillAvailableEventsMap(tabs);
  }

  private void translateTabs(List<GetStoreTabs.Tab> tabs) {
    for (GetStoreTabs.Tab t : tabs)
      t.setLabel(Translator.translate(t.getLabel()));
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

  private void fillAvailableEventsMap(List<GetStoreTabs.Tab> list) {
    for (int i = 0; i < list.size(); i++) {
      Event event = list.get(i).getEvent();

      if (!containsEventName(event.getName())) {
        availableEventsMap.put(event.getName(), i);
      }
    }
  }

  public boolean containsEventName(Event.Name name) {
    return availableEventsMap.containsKey(name);
  }

  @Override public Fragment getItem(int position) {

    GetStoreTabs.Tab tab = tabs.get(position);
    Event event = tab.getEvent();

    Fragment fragment;
    switch (event.getType()) {
      case API: {
        fragment = caseAPI(tab);
        break;
      }
      case CLIENT: {
        fragment = caseClient(event, tab);
        break;
      }
      case v3: {
        fragment = caseV3(event);
        break;
      }
      default:
        // Safe to throw exception as the tab should be filtered prior to getting here.
        throw new RuntimeException("Fragment type not implemented!");
    }
    return fragment;
  }

  private Fragment caseAPI(GetStoreTabs.Tab tab) {
    Event event = tab.getEvent();
    switch (event.getName()) {
      case getUserTimeline:
        Long userId = null;
        if (event.getData() != null && event.getData().getUser() != null) {
          userId = event.getData().getUser().getId();
        }
        return V8Engine.getFragmentProvider()
            .newAppsTimelineFragment(event.getAction(), userId, storeId, storeContext);
      default:
        return V8Engine.getFragmentProvider()
            .newStoreTabGridRecyclerFragment(event, storeTheme, tab.getTag(), storeContext);
    }
  }

  private Fragment caseClient(Event event, GetStoreTabs.Tab tab) {
    switch (event.getName()) {
      case myUpdates:
        return V8Engine.getFragmentProvider().newUpdatesFragment();
      case myDownloads:
        return V8Engine.getFragmentProvider().newDownloadsFragment();
      case mySpotShare:
        return V8Engine.getFragmentProvider().newSpotShareFragment(false);
      case myStores:
        return V8Engine.getFragmentProvider()
            .newSubscribedStoresFragment(event, storeTheme, tab.getTag());
      default:
        // Safe to throw exception as the tab should be filtered prior to getting here.
        throw new RuntimeException("Fragment type not implemented!");
    }
  }

  private Fragment caseV3(Event event) {
    switch (event.getName()) {
      case getReviews:
        return V8Engine.getFragmentProvider().newLatestReviewsFragment(storeId);
      default:
        // Safe to throw exception as the tab should be filtered prior to getting here.
        throw new RuntimeException("Fragment type not implemented!");
    }
  }

  public Event.Name getEventName(int position) {
    return tabs.get(position).getEvent().getName();
  }

  /**
   * Returns the position of an Event, given a name.
   *
   * @param name name of the Event {@link Event.Name}
   *
   * @return returns a positive integer 0...X if there is an Event with requested name, else returns
   * -1.
   */
  public int getEventNamePosition(Event.Name name) {
    final Integer integer = availableEventsMap.get(name);
    if (integer == null) {
      return -1;
    }
    return integer;
  }

  @Override public int getCount() {
    return tabs.size();
  }

  @Override public CharSequence getPageTitle(int position) {
    return tabs.get(position).getLabel();
  }
}
