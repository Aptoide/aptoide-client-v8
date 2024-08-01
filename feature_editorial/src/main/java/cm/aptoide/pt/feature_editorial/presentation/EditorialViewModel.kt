package cm.aptoide.pt.feature_editorial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_editorial.domain.usecase.ArticleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class EditorialViewModel(
  private val articleId: String,
  private val articleUseCase: ArticleUseCase,
) :
  ViewModel() {
  private val viewModelState = MutableStateFlow<EditorialUiState>(EditorialUiState.Loading)

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
      viewModelState.update { EditorialUiState.Loading }
      try {
        val result = articleUseCase.getDetails(articleId)
        viewModelState.update { EditorialUiState.Idle(article = result) }
      } catch (t: Throwable) {
        Timber.w(t)
        viewModelState.update {
          when (t) {
            is IOException -> EditorialUiState.NoConnection
            else -> EditorialUiState.Error
          }
        }
      }
    }
  }
}
