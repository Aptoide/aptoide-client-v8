package cm.aptoide.pt.feature_apps.presentation

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import kotlin.random.Random
import kotlin.random.nextInt

sealed class AppsListUiState {
  data class Idle(val apps: List<App>) : AppsListUiState()
  object Loading : AppsListUiState()
  object Empty : AppsListUiState()
  object NoConnection : AppsListUiState()
  object Error : AppsListUiState()
}

/**
 * Terminal states with no content to show. When a bundle ends up in one of these,
 * neither its content nor its title/header should be displayed.
 */
val AppsListUiState.isEmptyOrError: Boolean
  get() = this is AppsListUiState.Empty
    || this is AppsListUiState.Error
    || this is AppsListUiState.NoConnection

class AppsListUiStateProvider : PreviewParameterProvider<AppsListUiState> {
  override val values: Sequence<AppsListUiState> = sequenceOf(
    AppsListUiState.Idle(List(Random.nextInt(1..12)) { randomApp }),
    AppsListUiState.Loading,
    AppsListUiState.Empty,
    AppsListUiState.NoConnection,
    AppsListUiState.Error
  )
}

val previewAppsListIdleState
  get() = AppsListUiState.Idle(List(Random.nextInt(1..12)) { randomApp })
