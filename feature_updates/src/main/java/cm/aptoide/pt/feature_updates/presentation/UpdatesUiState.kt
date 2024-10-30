package cm.aptoide.pt.feature_updates.presentation

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import kotlin.random.Random
import kotlin.random.nextInt

sealed class UpdatesUiState {
  data class Idle(val updatesList: List<App>) : UpdatesUiState()
  object Empty : UpdatesUiState()
  object Loading : UpdatesUiState()
}

class UpdatesUiStateProvider : PreviewParameterProvider<UpdatesUiState> {
  override val values: Sequence<UpdatesUiState> = sequenceOf(
    UpdatesUiState.Idle(List(Random.nextInt(1..12)) { randomApp }),
    UpdatesUiState.Empty,
    UpdatesUiState.Loading,
  )
}
