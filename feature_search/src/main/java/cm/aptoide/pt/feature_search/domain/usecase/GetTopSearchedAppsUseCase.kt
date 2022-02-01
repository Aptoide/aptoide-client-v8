package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.Result
import cm.aptoide.pt.feature_search.domain.model.TopSearchApp
import cm.aptoide.pt.feature_search.domain.repository.TopSearchRepository


class GetTopSearchedAppsUseCase(private val topSearchRepository: TopSearchRepository) {

  suspend fun getTopSearchApps(): Result<List<TopSearchApp>> {
    return try {
      Result.Success(topSearchRepository.getTopSearchedApps())
    } catch (e: Exception) {
      Result.Error(e)
    }
  }
}