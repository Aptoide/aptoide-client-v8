package cm.aptoide.pt.feature_editorial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.domain.usecase.ArticlesMetaUseCase
import cm.aptoide.pt.feature_editorial.domain.usecase.RelatedArticlesMetaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class EditorialsCardViewModel(
  editorialWidgetUrl: String,
  subtype: String?,
  articlesMetaUseCase: ArticlesMetaUseCase
) : ViewModel() {

  val adListId = UUID.randomUUID().toString()
  // Implicitly null = loading
  private val viewModelState = MutableStateFlow<List<ArticleMeta>?>(null)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      val metaList = articlesMetaUseCase.getArticlesMeta(editorialWidgetUrl, subtype)
      viewModelState.update { metaList }
    }
  }
}

class RelatedEditorialsCardViewModel(
  packageName: String,
  relatedArticlesMetaUseCase: RelatedArticlesMetaUseCase
) : ViewModel() {

  val adListId = UUID.randomUUID().toString()
  // Implicitly null = loading
  private val viewModelState = MutableStateFlow<List<ArticleMeta>?>(null)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      val metaList = relatedArticlesMetaUseCase.getRelatedArticlesMeta(packageName)
      viewModelState.update { metaList }
    }
  }
}
