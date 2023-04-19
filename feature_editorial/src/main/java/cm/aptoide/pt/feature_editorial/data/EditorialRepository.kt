package cm.aptoide.pt.feature_editorial.data

import cm.aptoide.pt.feature_editorial.domain.Article
import kotlinx.coroutines.flow.Flow

interface EditorialRepository {
  fun getLatestArticle(): Flow<List<ArticleJson>>
  fun getArticle(widgetUrl: String): Flow<Article>
  fun getArticlesMeta(editorialWidgetUrl: String, subtype: String?): Flow<List<ArticleJson>>
  fun getRelatedArticlesMeta(packageName: String): Flow<List<ArticleJson>>
}
