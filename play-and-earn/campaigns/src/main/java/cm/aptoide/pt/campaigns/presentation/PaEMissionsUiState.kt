package cm.aptoide.pt.campaigns.presentation

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import cm.aptoide.pt.campaigns.data.paeMissions
import cm.aptoide.pt.campaigns.domain.PaEMissions

sealed class PaEMissionsUiState {
  data class Idle(val paeMissions: PaEMissions) : PaEMissionsUiState()
  object Loading : PaEMissionsUiState()
  object Empty : PaEMissionsUiState()
  object NoConnection : PaEMissionsUiState()
  object Error : PaEMissionsUiState()
}

class PaEMissionsUiStateProvider : PreviewParameterProvider<PaEMissionsUiState> {
  override val values: Sequence<PaEMissionsUiState> = sequenceOf(
    PaEMissionsUiState.Idle(paeMissions),
    PaEMissionsUiState.Loading,
    PaEMissionsUiState.Empty,
    PaEMissionsUiState.NoConnection,
    PaEMissionsUiState.Error
  )
}