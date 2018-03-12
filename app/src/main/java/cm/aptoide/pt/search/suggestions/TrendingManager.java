package cm.aptoide.pt.search.suggestions;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.search.model.Suggestion;
import java.util.List;
import rx.Single;

/**
 * Created by franciscocalado on 11/9/17.
 */

public class TrendingManager {
  private static final int SUGGESTION_COUNT = 5;
  private static final int SUGGESTION_STORE_ID = 15;
  private final TrendingService trendingService;

  public TrendingManager(TrendingService trendingService) {
    this.trendingService = trendingService;
  }

  public Single<List<Suggestion>> getTrendingListSuggestions() {
    return trendingService.getTrendingApps(SUGGESTION_COUNT, SUGGESTION_STORE_ID)
        .map(response -> response.getDataList()
            .getList())
        .flatMapIterable(list -> list)
        .map(app -> mapToSuggestion(app))
        .toList()
        .first()
        .toSingle();
  }

  public Single<List<String>> getTrendingCursorSuggestions() {
    return trendingService.getTrendingApps(SUGGESTION_COUNT, SUGGESTION_STORE_ID)
        .map(response -> response.getDataList()
            .getList())
        .flatMapIterable(list -> list)
        .map(app -> app.getName())
        .toList()
        .first()
        .toSingle();
  }

  private Suggestion mapToSuggestion(App app) {
    return new Suggestion(app.getName(), app.getIcon());
  }
}
