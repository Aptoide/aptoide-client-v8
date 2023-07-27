package cm.aptoide.pt.search

import cm.aptoide.pt.feature_search.data.AutoCompleteSuggestionsRepository
import cm.aptoide.pt.feature_search.data.network.response.SearchAutoCompleteSuggestionsResponse
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.AutoCompleteResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import javax.inject.Inject

class AptoideAutoCompleteSuggestionsRepository @Inject constructor(
  private val autoCompleteSearchSuggestionsService: AutoCompleteSearchRetrofitService,
) : AutoCompleteSuggestionsRepository {

  override fun getAutoCompleteSuggestions(keyword: String): Flow<AutoCompleteResult> =
    flow {
      val autoCompleteResponse =
        autoCompleteSearchSuggestionsService.getAutoCompleteSuggestions(keyword)
      if (autoCompleteResponse.isSuccessful) {
        autoCompleteResponse.body()?.data?.let {
          emit(AutoCompleteResult.Success(it))
        }
      } else {
        emit(AutoCompleteResult.Error(IllegalStateException()))
      }
    }

  interface AutoCompleteSearchRetrofitService {
    @GET("/v1/suggestion/app/{query}")
    suspend fun getAutoCompleteSuggestions(
      @Path(value = "query", encoded = true) query: String,
    ): Response<SearchAutoCompleteSuggestionsResponse>
  }
}
