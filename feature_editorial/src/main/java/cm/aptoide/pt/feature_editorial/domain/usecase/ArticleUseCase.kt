package cm.aptoide.pt.feature_editorial.domain.usecase

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
) {
  suspend fun getDetails(articleId: String): Article =
    urlsCache.get(id = ARTICLE_CACHE_ID_PREFIX + articleId)
      ?.let { editorialRepository.getArticle(editorialUrl = it) }
      ?: throw IllegalStateException()
}
