package cm.aptoide.pt.campaigns.presentation

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import cm.aptoide.pt.campaigns.domain.PaEBundles
import cm.aptoide.pt.campaigns.domain.randomPaEBundles

sealed class PaEBundlesUiState {
  data class Idle(val bundles: PaEBundles) : PaEBundlesUiState()
  object Loading : PaEBundlesUiState()
  object Empty : PaEBundlesUiState()
  object NoConnection : PaEBundlesUiState()
  object Error : PaEBundlesUiState()
}

class PaEBundlesUiStateProvider : PreviewParameterProvider<PaEBundlesUiState> {
  override val values: Sequence<PaEBundlesUiState> = sequenceOf(
    PaEBundlesUiState.Idle(randomPaEBundles),
    PaEBundlesUiState.Loading,
    PaEBundlesUiState.Empty,
    PaEBundlesUiState.NoConnection,
    PaEBundlesUiState.Error
  )
}
