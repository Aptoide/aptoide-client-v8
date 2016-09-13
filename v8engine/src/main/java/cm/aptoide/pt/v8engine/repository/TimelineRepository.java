/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.support.annotation.NonNull;

import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cm.aptoide.pt.dataprovider.ws.v7.GetUserTimelineRequest;
import cm.aptoide.pt.model.v7.Datalist;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.model.v7.timeline.TimelineItem;
import rx.Observable;

/**
 * Created by marcelobenites on 7/5/16.
 */
public class TimelineRepository {

  private final String action;
  private final TimelineCardFilter filter;

  public TimelineRepository(String action, TimelineCardFilter filter) {
    this.action = action;
    this.filter = filter;
  }

	public Observable<Datalist<TimelineCard>> getTimelineCards(Integer limit, int offset, List<String> packageNames, boolean refresh) {
		return GetUserTimelineRequest.of(action, limit, offset, packageNames)
				.observe(refresh)
				.doOnNext(item -> filter.clear())
				.map(getUserTimeline -> getUserTimeline.getDatalist())
				.flatMap(itemDataList -> Observable.from(getTimelineList(itemDataList))
            .flatMap(item -> filter.filter(item)).toList()
						.<Datalist<TimelineCard>>map(list -> getTimelineCardDatalist(itemDataList, list)));
	}

  @NonNull
	private Datalist<TimelineCard> getTimelineCardDatalist(Datalist<TimelineItem<TimelineCard>> itemDataList, List<TimelineCard> list) {
		Datalist<TimelineCard> cardDataList = new Datalist<>();
		cardDataList.setCount(itemDataList.getCount());
		cardDataList.setOffset(itemDataList.getOffset());
		cardDataList.setTotal(itemDataList.getTotal());
		cardDataList.setHidden(itemDataList.getHidden());
		cardDataList.setLoaded(itemDataList.isLoaded());
		cardDataList.setLimit(itemDataList.getLimit());
		cardDataList.setNext(itemDataList.getNext());
		cardDataList.setList(list);
		return cardDataList;
	}

	private List<TimelineItem<TimelineCard>> getTimelineList(Datalist<TimelineItem<TimelineCard>> datalist) {
		List<TimelineItem<TimelineCard>> items;
		if (datalist == null) {
			items = new ArrayList<>();
		} else {
			items = datalist.getList();
		}
		return items;
	}
}
