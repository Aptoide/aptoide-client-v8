package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository.SearchAppResult
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@ViewModelScoped
class SearchAppUseCase @Inject constructor(private val searchRepository: SearchRepository) {

  fun searchApp(keyword: String): Flow<SearchAppResult> {
    return searchRepository.searchApp(keyword)
  }
}