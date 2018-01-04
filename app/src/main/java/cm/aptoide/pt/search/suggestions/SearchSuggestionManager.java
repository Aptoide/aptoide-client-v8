package cm.aptoide.pt.search.suggestions;

import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Scheduler;
import rx.Single;

public class SearchSuggestionManager {

  private final SearchSuggestionService service;
  private final Scheduler ioScheduler;
  private final int timeout;
  private final TimeUnit timeoutTimeUnit;

  public SearchSuggestionManager(SearchSuggestionService service, Scheduler ioScheduler) {
    this.service = service;
    this.ioScheduler = ioScheduler;
    this.timeout = 10;
    this.timeoutTimeUnit = TimeUnit.SECONDS;
  }

  public SearchSuggestionManager(SearchSuggestionService service, int timeout,
      TimeUnit timeoutTimeUnit, Scheduler ioScheduler) {
    this.service = service;
    this.ioScheduler = ioScheduler;
    this.timeout = timeout;
    this.timeoutTimeUnit = timeoutTimeUnit;
  }

  public Single<List<String>> getSuggestionsForApp(String query) {
    return service.getAppSuggestionsForQuery(query)
        .timeout(timeout, timeoutTimeUnit)
        .subscribeOn(ioScheduler);
  }

  public Single<List<String>> getSuggestionsForStore(String query) {
    return service.getStoreSuggestionsForQuery(query)
        .timeout(timeout, timeoutTimeUnit)
        .subscribeOn(ioScheduler);
  }
}
