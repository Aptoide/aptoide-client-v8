/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.timeline;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.dataprovider.model.v7.TimelineStats;
import cm.aptoide.pt.dataprovider.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.TimelineItem;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetTimelineStatsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetUserTimelineRequest;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by marcelobenites on 7/5/16.
 */
public class TimelineRepository {

  private final String action;
  private final TimelineCardFilter filter;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public TimelineRepository(String action, TimelineCardFilter filter,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.action = action;
    this.filter = filter;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public Observable<DataList<TimelineCard>> getTimelineCards(Integer limit, int offset,
      List<String> packageNames, boolean refresh, String cardId) {
    return GetUserTimelineRequest.of(action, limit, offset, packageNames, bodyInterceptor,
        httpClient, converterFactory, cardId, tokenInvalidator, sharedPreferences)
        .observe(refresh)
        .flatMap(response -> {
          if (response.isOk()) {
            return Observable.just(response);
          }
          return Observable.error(
              new RepositoryItemNotFoundException("Could not retrieve timeline."));
        })
        .doOnNext(item -> filter.clear())
        .map(getUserTimeline -> getUserTimeline.getDataList())
        .flatMap(itemDataList -> Observable.from(getTimelineList(itemDataList))
            .concatMap(item -> filter.filter(item))
            .toList()
            .map(list -> getTimelineCardDatalist(itemDataList, list)));
  }

  private List<TimelineItem<TimelineCard>> getTimelineList(
      DataList<TimelineItem<TimelineCard>> datalist) {
    List<TimelineItem<TimelineCard>> items;
    if (datalist == null) {
      items = new ArrayList<>();
    } else {
      items = datalist.getList();
    }
    return items;
  }

  @NonNull private DataList<TimelineCard> getTimelineCardDatalist(
      DataList<TimelineItem<TimelineCard>> itemDataList, List<TimelineCard> list) {
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

  public Observable<TimelineStats> getTimelineStats(boolean byPassCache, Long userId) {
    return GetTimelineStatsRequest.of(bodyInterceptor, userId, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(byPassCache);
  }
}
