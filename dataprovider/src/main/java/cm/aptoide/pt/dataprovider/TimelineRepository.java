/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import cm.aptoide.pt.dataprovider.ws.v7.GetUserTimelineRequest;
import cm.aptoide.pt.model.v7.Datalist;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.File;
import cm.aptoide.pt.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.model.v7.timeline.TimelineItem;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by marcelobenites on 7/5/16.
 */
public class TimelineRepository {

	private final TimelineCardDuplicateFilter duplicateFilter;
	private String action;

	public TimelineRepository(String action, TimelineCardDuplicateFilter duplicateFilter) {
		this.action = action;
		this.duplicateFilter = duplicateFilter;
	}

	public Observable<Datalist<TimelineCard>> getTimelineCards(Integer limit, int offset, List<String> packageNames, boolean refresh) {
		return GetUserTimelineRequest.of(action, limit, offset, packageNames)
				.observe(refresh)
				.doOnNext(item -> duplicateFilter.clear())
				.map(getUserTimeline -> getUserTimeline.getDatalist())
				.flatMap(itemDataList -> Observable.from(getTimelineList(itemDataList))
						.filter(timelineItem -> timelineItem != null)
						.<TimelineCard>map(timelineItem -> timelineItem.getData())
						.filter(duplicateFilter)
						.toList()
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
		list.add(getMockedAppUpdate());
		cardDataList.setList(list);
		return cardDataList;
	}

	private TimelineCard getMockedAppUpdate() {
		AppUpdate app = new AppUpdate("1234");
		app.setId(19347406);
		app.setName("Clash of Clans");
		app.setPackageName("com.supercell.clashofclans");
		File file = new File();
		file.setVername("8.3332.14");
		file.setVercode(774);
		file.setPath("http://pool.apk.aptoide.com/milaupv/com-supercell-clashofclans-774-19621630-e308cca924efb5e52545fea11e5eae9a.apk");
		app.setFile(file);
		app.setUpdated(new Date());
		app.setIcon("http://cdn6.aptoide.com/imgs/a/a/e/aae8e02f62bf4a4008769ddb14b8fd89_icon_96x96.png");
		return app;
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
