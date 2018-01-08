package cm.aptoide.pt.search.suggestions;

import java.util.List;
import rx.Single;

public class SearchSuggestionService {

  private final SearchSuggestionRemoteRepository repository;

  public SearchSuggestionService(SearchSuggestionRemoteRepository repository) {
    this.repository = repository;
  }

  public Single<List<String>> getAppSuggestionsForQuery(String query) {
    return repository.getSuggestionForApp(query)
        .map(model -> model.getData());
  }

  public Single<List<String>> getStoreSuggestionsForQuery(String query) {
    return repository.getSuggestionForStore(query)
        .map(model -> model.getData());
  }
}
