package cm.aptoide.pt.feature_editorial.data

import cm.aptoide.pt.feature_editorial.domain.ArticleDetail
import kotlinx.coroutines.flow.Flow

interface EditorialRepository {
  fun getLatestArticle(): Flow<List<Article>>
  fun getArticleDetail(articleId: String): Flow<ArticleDetail>
  fun getArticlesMeta(editorialWidgetUrl: String, subtype: String?): Flow<List<Article>>
  fun getRelatedArticlesMeta(packageName: String): Flow<List<Article>>
}
