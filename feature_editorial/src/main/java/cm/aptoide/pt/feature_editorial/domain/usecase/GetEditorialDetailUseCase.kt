package cm.aptoide.pt.feature_editorial.domain.usecase

import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetEditorialDetailUseCase @Inject constructor(private val editorialRepository: EditorialRepository) {
  fun getEditorialInfo(articleId: String): Flow<EditorialRepository.EditorialDetailResult> {
    return editorialRepository.getArticleDetail(articleId)
  }
}