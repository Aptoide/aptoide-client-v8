package cm.aptoide.pt.feature_reactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_reactions.data.Reactions
import cm.aptoide.pt.feature_reactions.domain.usecase.ReactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReactionsViewModel(
  private val id: String,
  private val reactionsUseCase: ReactionsUseCase,
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow(ReactionsUiState(reactions = Reactions(-1, emptyList())))

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      try {
        val data = reactionsUseCase.getReactions(id)
        viewModelState.update { it.copy(reactions = data) }
      } catch (throwable: Throwable) {
        throwable.printStackTrace()
      }
    }
  }

  fun onClickReactionDelete() {
    viewModelScope.launch {
      try {
        reactionsUseCase.deleteReaction(id)
      } catch (exception: Exception) {
        exception.printStackTrace()
      }
    }
  }
}
