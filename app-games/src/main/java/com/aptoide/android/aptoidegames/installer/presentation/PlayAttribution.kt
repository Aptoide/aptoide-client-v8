package com.aptoide.android.aptoidegames.installer.presentation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.installer.PlayCatalogChecker
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Optional
import javax.inject.Inject

@HiltViewModel
class PlayCatalogInjectionsProvider @Inject constructor(
  val playCatalogChecker: Optional<PlayCatalogChecker>,
) : ViewModel()

/**
 * Whether [app] installs via Google Play, resolved before the user taps Install so the
 * mandatory "Google Play" attribution can be shown (Play Catalog Access Program). Always
 * false where no [PlayCatalogChecker] is bound (non-Play distributions) and in previews.
 * [prefetch] triggers the catalog lookup; leave it false on list cards, which label lazily
 * from the shared cache.
 */
@Composable
fun rememberIsPlayCatalog(
  app: App,
  prefetch: Boolean = false,
): Boolean = runPreviewable(
  preview = { false },
  real = {
    val checker = hiltViewModel<PlayCatalogInjectionsProvider>().playCatalogChecker
      .orElse(null)
    if (checker == null) {
      false
    } else {
      if (prefetch) {
        LaunchedEffect(app.packageName) { checker.prefetch(app.packageName) }
      }
      val isPlayCatalog by remember(app.packageName) {
        checker.observeIsPlayCatalog(app.packageName)
      }.collectAsState(initial = false)
      isPlayCatalog
    }
  }
)

/**
 * The pre-tap states whose action can divert into a Google Play inline install - the
 * "Google Play" attribution must be visible alongside their buttons.
 */
fun DownloadUiState?.canTriggerInlineInstall(): Boolean = when (this) {
  is DownloadUiState.Install,
  is DownloadUiState.Outdated,
  is DownloadUiState.Migrate,
  is DownloadUiState.MigrateAlias,
  is DownloadUiState.Error,
    -> true

  // Listed rather than folded into an else, so a new DownloadUiState is a compile error here
  // and has to be classified deliberately instead of silently losing the attribution
  null,
  is DownloadUiState.Waiting,
  is DownloadUiState.Downloading,
  is DownloadUiState.ReadyToInstall,
  is DownloadUiState.Installing,
  is DownloadUiState.Uninstalling,
  is DownloadUiState.Installed,
    -> false
}

/**
 * The click to run instead of this state's own install action, or null to keep the action as
 * it is. Feed and carousel cards cannot afford a catalog lookup each, so in Play-distributed
 * builds ([com.aptoide.android.aptoidegames.installer.FEED_INSTALL_DIVERTS_TO_APPVIEW]) they
 * send the user to AppView - which prefetches and labels - rather than starting an install
 * that could divert into an unlabeled Play inline install. Deliberately gated on the same
 * states as [canTriggerInlineInstall], so downloads keep their progress and cancel, and
 * installed apps keep their open button.
 */
fun DownloadUiState?.appViewDiversion(
  onNavigateToAppView: (() -> Unit)?,
  divertsToAppView: Boolean,
): (() -> Unit)? = onNavigateToAppView?.takeIf { divertsToAppView && canTriggerInlineInstall() }

/**
 * Plain-text "Google Play" attribution (Inline Install Brand Guidelines, option 3: the
 * words typed out in the same font and style as the surrounding text - no logo assets).
 */
@Composable
fun PlayAttributionLabel(
  modifier: Modifier = Modifier,
  style: TextStyle = AGTypography.InputsXSRegular,
  color: Color = Palette.GreyLight,
) {
  Text(
    text = stringResource(R.string.install_google_play_attribution),
    style = style,
    color = color,
    maxLines = 1,
    modifier = modifier,
  )
}
