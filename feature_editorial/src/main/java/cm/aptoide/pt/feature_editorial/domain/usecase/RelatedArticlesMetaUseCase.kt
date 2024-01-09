package cm.aptoide.pt.feature_editorial.domain.usecase

import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import dagger.hilt.android.scopes.ViewModelScoped
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class RelatedArticlesMetaUseCase @Inject constructor(
  private val editorialRepository: EditorialRepository,
  private val urlsCache: UrlsCache,
) {
  suspend fun getRelatedArticlesMeta(packageName: String): List<ArticleMeta> =
    try {
      editorialRepository.getRelatedArticlesMeta(packageName)
        .onEach { it.cacheUrls(urlsCache::set) }
    } catch (t: Throwable) {
      Timber.w(t)
      emptyList()
    }
}
