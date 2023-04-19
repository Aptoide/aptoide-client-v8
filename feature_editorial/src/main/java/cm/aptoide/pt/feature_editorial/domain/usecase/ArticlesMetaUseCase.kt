package cm.aptoide.pt.feature_editorial.domain.usecase

import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import dagger.hilt.android.scopes.ViewModelScoped
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class ArticlesMetaUseCase @Inject constructor(
  private val editorialRepository: EditorialRepository
) {
  suspend fun getArticlesMeta(editorialWidgetUrl: String, subtype: String?): List<ArticleMeta> =
    try {
      editorialRepository.getArticlesMeta(editorialWidgetUrl, subtype)
    } catch (t: Throwable) {
      Timber.w(t)
      emptyList()
    }
}
