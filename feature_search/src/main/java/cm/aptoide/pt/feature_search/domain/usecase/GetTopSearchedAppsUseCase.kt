package cm.aptoide.pt.feature_search.domain.usecase

import cm.aptoide.pt.feature_search.domain.Result
import cm.aptoide.pt.feature_search.domain.model.TopSearchApp
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.*
import javax.inject.Inject


//class GetTopSearchedAppsUseCase(private val topSearchRepository: TopSearchRepository) {
@ViewModelScoped
class GetTopSearchedAppsUseCase @Inject constructor() {

  suspend fun getTopSearchApps(): Result<List<TopSearchApp>> {
    return try {
      //Result.Success(topSearchRepository.getTopSearchedApps())
      Result.Success(Collections.emptyList())
    } catch (e: Exception) {
      Result.Error(e)
    }
  }
}