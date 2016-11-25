package cm.aptoide.pt.aptoidesdk.parser;

import cm.aptoide.pt.aptoidesdk.entities.EntitiesFactory;
import cm.aptoide.pt.aptoidesdk.entities.SearchResult;
import cm.aptoide.pt.model.v7.ListSearchApps;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;

/**
 * Created by neuro on 10-11-2016.
 */

public class Parsers {

  public static Observable<List<SearchResult>> parse(
      Observable<ListSearchApps> searchAppsObservable) {

    return searchAppsObservable.flatMap(listSearchApps -> {
      LinkedList<SearchResult> searchResults = new LinkedList<>();
      for (ListSearchApps.SearchAppsApp searchAppsApp : listSearchApps.getDatalist().getList()) {
        searchResults.add(EntitiesFactory.createSearchResult(searchAppsApp));
      }

      return Observable.from(searchResults).toList();
    }).onErrorReturn(throwable -> new LinkedList<>());
  }

  public static List<SearchResult> parse(ListSearchApps listSearchApps) {

    LinkedList<SearchResult> searchResults = new LinkedList<>();
    for (ListSearchApps.SearchAppsApp searchAppsApp : listSearchApps.getDatalist().getList()) {
      searchResults.add(EntitiesFactory.createSearchResult(searchAppsApp));
    }

    return searchResults;
  }
}
