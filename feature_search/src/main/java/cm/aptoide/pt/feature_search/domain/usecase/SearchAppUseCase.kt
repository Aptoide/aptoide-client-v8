package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.Result
import cm.aptoide.pt.feature_search.domain.model.SearchApp
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@ViewModelScoped
class SearchAppUseCase @Inject constructor(private val searchRepository: SearchRepository) {

  suspend fun searchApp(keyword: String): Result<List<SearchApp>> {
    return try {
      Result.Success(searchRepository.searchApp(keyword))
    } catch (e: Exception) {
      Result.Error(e)
    }
  }
}