package cm.aptoide.pt.feature_editorial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_editorial.domain.usecase.EditorialsMetaUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EditorialsMetaViewModel(
  editorialWidgetUrl: String,
  editorialsMetaUseCase: EditorialsMetaUseCase
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow(EditorialsMetaUiState(editorialsMetas = emptyList()))

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      editorialsMetaUseCase.getEditorialsMeta(editorialWidgetUrl)
        .catch { throwable -> throwable.printStackTrace() }
        .collect { metaList -> viewModelState.update { it.copy(editorialsMetas = metaList) } }
    }
  }
}
