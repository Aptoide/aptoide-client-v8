package cm.aptoide.pt.search.suggestions;

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

  public Single<List<String>> getTrendingSuggestions() {
    return trendingService.getTrendingApps(SUGGESTION_COUNT, SUGGESTION_STORE_ID)
        .map(response -> response.getDataList()
            .getList())
        .flatMapIterable(list -> list)
        .map(app -> app.getName())
        .toList()
        .first()
        .toSingle();
  }
}
