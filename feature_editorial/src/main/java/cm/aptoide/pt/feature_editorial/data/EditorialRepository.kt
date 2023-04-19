package cm.aptoide.pt.feature_editorial.data

import cm.aptoide.pt.feature_editorial.domain.Article
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta

interface EditorialRepository {
  suspend fun getLatestArticle(): List<ArticleMeta>
  suspend fun getArticle(widgetUrl: String): Article
  suspend fun getArticlesMeta(editorialWidgetUrl: String, subtype: String?): List<ArticleMeta>
  suspend fun getRelatedArticlesMeta(packageName: String): List<ArticleMeta>
}
