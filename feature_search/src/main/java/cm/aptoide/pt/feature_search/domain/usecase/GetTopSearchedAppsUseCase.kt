package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.Result
import cm.aptoide.pt.feature_search.domain.model.TopSearchApp
import java.util.*


//class GetTopSearchedAppsUseCase(private val topSearchRepository: TopSearchRepository) {
class GetTopSearchedAppsUseCase {

  suspend fun getTopSearchApps(): Result<List<TopSearchApp>> {
    return try {
      //Result.Success(topSearchRepository.getTopSearchedApps())
      Result.Success(Collections.emptyList())
    } catch (e: Exception) {
      Result.Error(e)
    }
  }
}