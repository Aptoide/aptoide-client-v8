package cm.aptoide.pt.feature_editorial.data

import cm.aptoide.pt.aptoide_network.di.RetrofitV7ActionItem
import cm.aptoide.pt.feature_editorial.data.network.ContentJSON
import cm.aptoide.pt.feature_editorial.data.network.Data
import cm.aptoide.pt.feature_editorial.data.network.EditorialRemoteService
import cm.aptoide.pt.feature_editorial.data.network.model.EditorialJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideEditorialRepository @Inject constructor(
  @RetrofitV7ActionItem private val editorialRemoteService: EditorialRemoteService,
) : EditorialRepository {
  override fun getLatestArticle(): Flow<EditorialRepository.EditorialResult> = flow {

    val latestEditorialResponse = editorialRemoteService.getLatestEditorial()

    if (latestEditorialResponse.isSuccessful) {
      latestEditorialResponse.body()?.datalist?.list
        ?.map(EditorialJson::toDomainModel)
        ?.let {
          emit(EditorialRepository.EditorialResult.Success(it))
        }
        ?: emit(EditorialRepository.EditorialResult.Error(IllegalStateException()))
    } else {
      emit(EditorialRepository.EditorialResult.Error(IllegalStateException()))
    }
  }

  override fun getArticleDetail(articleId: String): Flow<EditorialRepository.EditorialDetailResult> =
    flow {
      val articleDetailResponse = editorialRemoteService.getEditorialDetail(articleId)
      if (articleDetailResponse.isSuccessful) {
        articleDetailResponse.body()?.data?.let {
          emit(EditorialRepository.EditorialDetailResult.Success(it.toDomainModel()))
        }
      } else {
        emit(EditorialRepository.EditorialDetailResult.Error(java.lang.IllegalStateException()))
      }
    }

  override fun getArticleMeta(editorialWidgetUrl: String): Flow<EditorialRepository.EditorialResult> =
    flow {
      if (editorialWidgetUrl.contains("cards/")) {
        val editorialMetaResponse =
          editorialRemoteService.getArticleMeta(editorialWidgetUrl.split("cards/")[1])

        if (editorialMetaResponse.isSuccessful) {
          editorialMetaResponse.body()?.datalist?.list
            ?.map(EditorialJson::toDomainModel)
            ?.let {
              emit(EditorialRepository.EditorialResult.Success(it))
            }
            ?: emit(EditorialRepository.EditorialResult.Error(IllegalStateException()))
        } else {
          emit(EditorialRepository.EditorialResult.Error(IllegalStateException()))
        }
      } else {
        emit(EditorialRepository.EditorialResult.Error(IllegalStateException()))
      }
    }
}

private fun Data.toDomainModel(): ArticleDetail {
  return ArticleDetail(
    this.title,
    ArticleType.GAME_OF_THE_WEEK,
    this.background,
    this.date,
    this.views,
    map(this.content)
  )
}

fun map(content: List<ContentJSON>): List<ArticleContent> {
  val contentList = ArrayList<ArticleContent>()

  content.forEach {
    contentList.add(ArticleContent(it.title, it.message, it.action, it.media, it.app))
  }

  return contentList
}

private fun EditorialJson.toDomainModel(): Article {
  return Article(
    this.id,
    this.title,
    ArticleType.GAME_OF_THE_WEEK,
    this.summary,
    this.icon,
    this.date,
    this.views
  )
}
