/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 05/07/2016.
 */

package cm.aptoide.pt.dataprovider;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cm.aptoide.pt.dataprovider.ws.v7.GetUserTimelineRequest;
import cm.aptoide.pt.model.v7.DataList;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.Feature;
import cm.aptoide.pt.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.model.v7.timeline.TimelineItem;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by marcelobenites on 7/5/16.
 */
public class TimelineRepository {

	private String action;
	private final TimelineCardDuplicateFilter duplicateFilter;

	public TimelineRepository(String action, TimelineCardDuplicateFilter duplicateFilter) {
		this.action = action;
		this.duplicateFilter = duplicateFilter;
	}

	public Observable<DataList<TimelineCard>> getTimelineCards(int limit, int offset, List<String> packageNames, boolean refresh) {
		return GetUserTimelineRequest.of(action, limit, offset, packageNames)
				.observe(refresh)
				.doOnNext(item -> duplicateFilter.clear())
				.map(getUserTimeline -> getUserTimeline.getDatalist())
				.flatMap(itemDataList -> Observable.<TimelineItem<TimelineCard>>from(getTimelineList(itemDataList))
						.filter(timelineItem -> timelineItem != null)
						.<TimelineCard>map(timelineItem -> timelineItem.getData())
						.filter(duplicateFilter)
						.toList()
						.<DataList<TimelineCard>>map(list -> getTimelineCardDatalist(itemDataList, list)));
	}

	@NonNull
	private DataList<TimelineCard> getTimelineCardDatalist(DataList<TimelineItem<TimelineCard>> itemDataList, List<TimelineCard> list) {
		DataList<TimelineCard> cardDataList = new DataList<>();
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

	private List<TimelineItem<TimelineCard>> getTimelineList(DataList<TimelineItem<TimelineCard>> datalist) {
		List<TimelineItem<TimelineCard>> items;
		if (datalist == null) {
			items = new ArrayList<>();
		} else {
			items = datalist.getList();
		}
		return items;
	}

	public static class TimelineCardDuplicateFilter implements Func1<TimelineCard, Boolean> {

		private final Set<String> cardIds;

		public TimelineCardDuplicateFilter(Set<String> cardIds) {
			this.cardIds = cardIds;
		}

		public void clear() {
			cardIds.clear();
		}

		@Override
		public Boolean call(TimelineCard card) {
			return cardIds.add(card.getCardId());
		}
	}
	
}
