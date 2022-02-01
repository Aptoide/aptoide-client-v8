package cm.aptoide.pt.feature_search.data

import cm.aptoide.pt.feature_search.data.network.service.SearchRetrofitRepository
import cm.aptoide.pt.feature_search.domain.model.SearchApp
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository

class RemoteSearchAppRepository(private val searchRetrofitRepository: SearchRetrofitRepository) :
  SearchRepository {

  override suspend fun searchApp(keyword: String): List<SearchApp> {
    return searchRetrofitRepository.searchApp(keyword).results.searchResultsList.map {
      SearchApp(
        it,
        it,
        0.0,
        10
      )
    }
  }
}