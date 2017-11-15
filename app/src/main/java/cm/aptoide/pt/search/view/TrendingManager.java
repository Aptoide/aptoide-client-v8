package cm.aptoide.pt.search.view;

import cm.aptoide.pt.search.TrendingService;
import java.util.List;
import rx.Single;

/**
 * Created by franciscocalado on 11/9/17.
 */

public class TrendingManager {
  private static final int SUGGESTION_COUNT = 5;
  private final TrendingService trendingService;

  public TrendingManager(TrendingService trendingService) {
    this.trendingService = trendingService;
  }

  public Single<List<String>> getTrendingSuggestions() {
    return trendingService.getTrendingApps(SUGGESTION_COUNT)
        .map(response -> response.getDataList()
            .getList())
        .flatMapIterable(list -> list)    // Transform list, element by element
        .map(app -> app.getName())
        .toList()                         // Elements' names back to a list
        .first()
        .toSingle();
  }
}
