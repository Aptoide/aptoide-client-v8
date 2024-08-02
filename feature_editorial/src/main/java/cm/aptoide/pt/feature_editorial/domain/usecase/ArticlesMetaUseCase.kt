package cm.aptoide.pt.feature_editorial.domain.usecase

import cm.aptoide.pt.aptoide_network.domain.UrlsCache
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.di.DefaultEditorialUrl
import cm.aptoide.pt.feature_editorial.domain.ARTICLE_CACHE_ID_PREFIX
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.domain.EDITORIAL_DEFAULT_TAG
import cm.aptoide.pt.feature_editorial.domain.RELATED_ARTICLE_CACHE_ID_PREFIX
import dagger.hilt.android.scopes.ViewModelScoped
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class ArticlesMetaUseCase @Inject constructor(
  private val editorialRepository: EditorialRepository,
  private val urlsCache: UrlsCache,
  @DefaultEditorialUrl private val defaultEditorialUrl: String,
) {
  suspend fun getArticlesMeta(
    tag: String,
    subtype: String?,
  ): List<ArticleMeta> =
    try {
      val url = urlsCache.get(id = tag).takeIf { !it.isNullOrEmpty() } ?: defaultEditorialUrl
      editorialRepository.getArticlesMeta(url, subtype)
        .also { urlsCache.putAll(it.tagsUrls(url) + (EDITORIAL_DEFAULT_TAG to url)) }
    } catch (t: Throwable) {
      Timber.w(t)
      emptyList()
    }
}

fun List<ArticleMeta>.tagsUrls(relatedUrl: String?): Map<String, String> = map {
  listOf(
    ARTICLE_CACHE_ID_PREFIX + it.id to it.url,
    RELATED_ARTICLE_CACHE_ID_PREFIX + it.id to (relatedUrl ?: "")
  )
}
  .flatten()
  .toMap()
