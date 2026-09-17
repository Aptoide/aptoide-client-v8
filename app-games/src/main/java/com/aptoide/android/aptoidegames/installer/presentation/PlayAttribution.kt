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
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.installer.FEED_INSTALL_DIVERTS_TO_APPVIEW
import com.aptoide.android.aptoidegames.installer.PlayCatalogChecker
import com.aptoide.android.aptoidegames.installer.excludedFromPlayCatalog
import com.aptoide.android.aptoidegames.mmp.UTMContext
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
 * from the shared cache. BDS (Catappult) apps are always false and never trigger the lookup -
 * see [excludedFromPlayCatalog].
 */
@Composable
fun rememberIsPlayCatalog(
  app: App,
  prefetch: Boolean = false,
): Boolean = runPreviewable(
  preview = { false },
  real = {
    // Decided before the checker is even resolved, so a BDS app never reaches prefetch nor the
    // cache: no request, no tag, whatever token the backend would have returned
    if (app.excludedFromPlayCatalog()) return@runPreviewable false
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
 * builds ([FEED_INSTALL_DIVERTS_TO_APPVIEW]) they send the user to AppView - which prefetches
 * and labels - rather than starting an install that could divert into an unlabeled Play inline
 * install. Deliberately gated on the same
 * states as [canTriggerInlineInstall], so downloads keep their progress and cancel, and
 * installed apps keep their open button.
 */
fun DownloadUiState?.appViewDiversion(
  onNavigateToAppView: (() -> Unit)?,
  divertsToAppView: Boolean,
): (() -> Unit)? = onNavigateToAppView?.takeIf { divertsToAppView && canTriggerInlineInstall() }

/**
 * This navigation plus the campaign click the install path reported before the tap was
 * diverted. AppView will not report it - the install path is guarded on
 * `currentScreen != "AppView"` - and nothing reports one on AppView entry, so without this the
 * click is lost outright.
 *
 * Only for surfaces whose card tap does not already report a click of its own; applying it
 * where one is already sent (top charts, more, bonus list, publisher takeover) double counts.
 */
@Composable
fun (() -> Unit)?.reportingCampaignClick(app: App): (() -> Unit)? {
  // Distributions that never divert keep the navigation untouched, so they take on neither the
  // wrapper nor a recomposition dependency on the UTM context they would never report with.
  // A compile-time constant, so the branch is fixed for the whole build.
  if (!FEED_INSTALL_DIVERTS_TO_APPVIEW) return this

  val utmContext = UTMContext.current
  return this?.let { navigate ->
    {
      app.campaigns?.toAptoideMMPCampaign()?.sendClickEvent(utmInfo = utmContext)
      navigate()
    }
  }
}

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
