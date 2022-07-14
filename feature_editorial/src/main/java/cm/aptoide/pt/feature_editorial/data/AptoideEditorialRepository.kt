package cm.aptoide.pt.feature_editorial.data

import cm.aptoide.pt.aptoide_network.di.RetrofitV7ActionItem
import cm.aptoide.pt.feature_editorial.data.network.EditorialRemoteService
import cm.aptoide.pt.feature_editorial.data.network.model.EditorialJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AptoideEditorialRepository @Inject constructor(
  @RetrofitV7ActionItem private val editorialRemoteService: EditorialRemoteService,
) : EditorialRepository {
  override fun getLatestArticle(): Flow<EditorialRepository.EditorialResult> = flow {

    val latestEditorialResponse = editorialRemoteService.getLatestEditorial()

    if (latestEditorialResponse.isSuccessful) {
      latestEditorialResponse.body()?.datalist?.list?.first().let {
        if (it != null) {
          emit(EditorialRepository.EditorialResult.Success(it.toDomainModel()))
        } else {
          emit(EditorialRepository.EditorialResult.Error(IllegalStateException()))
        }
      }
    } else {
      emit(EditorialRepository.EditorialResult.Error(IllegalStateException()))
    }
  }
}

private fun EditorialJson.toDomainModel(): Article {
  return Article(
    this.title,
    ArticleType.GAME_OF_THE_WEEK,
    this.summary,
    this.icon,
    this.date,
    this.views
  )
}
