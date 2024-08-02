package cm.aptoide.pt.feature_editorial.domain.usecase

import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.domain.ARTICLE_CACHE_ID_PREFIX
import cm.aptoide.pt.feature_editorial.domain.Article
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ArticleUseCase @Inject constructor(
  private val editorialRepository: EditorialRepository,
  private val urlsCache: UrlsCache,
  @StoreName private val storeName: String,
) {
  suspend fun getDetails(articleId: String): Article {
    val editorialUrl = urlsCache.get(id = ARTICLE_CACHE_ID_PREFIX + articleId)
      ?: "card/get/id=$articleId/store_name=$storeName"
    return editorialRepository.getArticle(editorialUrl)
  }
}
