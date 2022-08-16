package cm.aptoide.pt.feature_editorial.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_editorial.data.ArticleDetail
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.domain.usecase.GetEditorialDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorialViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val getEditorialDetailUseCase: GetEditorialDetailUseCase,
) :
  ViewModel() {
  private val viewModelState = MutableStateFlow(EditorialDetailViewModelState())
  private val articleId: String? = savedStateHandle.get("articleId")
  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    viewModelScope.launch {
      articleId?.let {
        getEditorialDetailUseCase.getEditorialInfo(it)
          .catch { throwable ->
            throwable.printStackTrace()
          }.collect { editorialResult ->
            viewModelState.update {
              when (editorialResult) {
                is EditorialRepository.EditorialDetailResult.Success -> it.copy(editorialResult.data,
                  false)
                is EditorialRepository.EditorialDetailResult.Error -> it.copy()
              }
            }
          }
      }
    }
  }


  private data class EditorialDetailViewModelState(
    val article: ArticleDetail? = null,
    val isLoading: Boolean = false,
  ) {

    fun toUiState(): EditorialDetailUiState =
      EditorialDetailUiState(
        article,
        isLoading,
      )
  }
}
