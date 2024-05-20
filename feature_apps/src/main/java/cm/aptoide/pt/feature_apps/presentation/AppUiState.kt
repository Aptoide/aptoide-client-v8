package cm.aptoide.pt.feature_apps.presentation

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp

sealed class AppUiState {
  data class Idle(val app: App) : AppUiState()
  object Loading : AppUiState()
  object NoConnection : AppUiState()
  object Error : AppUiState()
}

class AppUiStateProvider : PreviewParameterProvider<AppUiState> {
  override val values: Sequence<AppUiState> = sequenceOf(
    AppUiState.Idle(randomApp),
    AppUiState.Loading,
    AppUiState.NoConnection,
    AppUiState.Error
  )
}
