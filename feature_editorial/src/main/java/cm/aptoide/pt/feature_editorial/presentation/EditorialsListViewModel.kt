package cm.aptoide.pt.feature_editorial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_editorial.domain.usecase.ArticlesMetaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class EditorialsListViewModel(
  private val tag: String,
  private val subtype: String?,
  private val articlesMetaUseCase: ArticlesMetaUseCase
) : ViewModel() {
  private val viewModelState = MutableStateFlow<ArticleListUiState>(ArticleListUiState.Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      viewModelState.update { ArticleListUiState.Loading }
      try {
        val result = articlesMetaUseCase.getArticlesMeta(tag, subtype)
        viewModelState.update {
          if (result.isEmpty()) {
            ArticleListUiState.Empty
          } else {
            ArticleListUiState.Idle(articles = result)
          }
        }
      } catch (t: Throwable) {
        Timber.w(t)
        viewModelState.update {
          when (t) {
            is IOException -> ArticleListUiState.NoConnection
            else -> ArticleListUiState.Error
          }
        }
      }
    }
  }
}
