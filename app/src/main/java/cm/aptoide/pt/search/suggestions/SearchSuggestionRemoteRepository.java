package cm.aptoide.pt.search.suggestions;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Single;

public interface SearchSuggestionRemoteRepository {
  @GET("/v1/suggestion/app/{query}") Single<Suggestions> getSuggestion(
      @Path("query") String query);

  @GET("/v1/suggestion/stores/{query}") Single<Suggestions> getSuggestionForStore(
      @Path("query") String query);
}
