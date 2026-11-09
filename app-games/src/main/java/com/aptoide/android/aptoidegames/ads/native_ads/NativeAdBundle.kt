package com.aptoide.android.aptoidegames.ads.native_ads

import android.view.View
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.applovin.mediation.MaxAd
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.home.BundleHeader
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.FixedColors
import com.aptoide.android.aptoidegames.theme.Palette

/**
 * Theme colors applied to the inflated MAX template so the card follows the brand palette and,
 * on Vanilla, the light/dark theme. Resolved in composition (Palette is composable) and pushed
 * into the Android views by id.
 */
data class NativeAdCardColors(
  val cardBackground: Color,
  val mediaBackground: Color,
  val textPrimary: Color,
  val textSecondary: Color,
  val accent: Color,
  val onAccent: Color,
) {
  companion object {
    @Composable
    fun fromTheme(): NativeAdCardColors = NativeAdCardColors(
      cardBackground = Palette.GreyDark,
      // Overlays and letterboxing on the creative are theme-invariant (CLAUDE.md: FixedColors
      // for labels/overlays on images); Palette.Black would turn white in Vanilla's light theme.
      mediaBackground = FixedColors.Dark,
      textPrimary = Palette.White,
      textSecondary = Palette.GreyLight,
      accent = Palette.Primary,
      onAccent = FixedColors.Dark,
    )
  }
}

/**
 * The ad card itself: one [MaxNativeAdView] per loaded [ad]. The view is keyed on the ad
 * identity, so a replacement ad (reload after expiry) gets a fresh view and the old one is
 * recycled, instead of stale assets lingering in a view that was bound to a destroyed ad.
 */
@Composable
fun NativeAdCard(
  ad: MaxAd,
  template: NativeAdTemplate,
  onRender: (MaxNativeAdView, MaxAd) -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = NativeAdCardColors.fromTheme()
  val sponsoredLabel = stringResource(R.string.native_ad_title)
  key(ad) {
    AndroidView(
      modifier = modifier
        .fillMaxWidth()
        .semantics { contentDescription = sponsoredLabel },
      factory = { context ->
        MaxNativeAdView(nativeAdViewBinder(template), context).also { view ->
          view.applyColors(colors)
          onRender(view, ad)
        }
      },
      // A retained ad follows a theme change (Vanilla light/dark): only the colours are
      // reapplied; the ad is neither re-rendered nor re-requested.
      update = { view -> view.applyColors(colors) },
      onRelease = { view -> view.recycle() },
    )
  }
}

/**
 * A native ad rendered with the app's bundle anatomy: the same header row as every other bundle,
 * the ad card below it, and a trailing gap. Used by the home feed and the Search landing.
 */
@Composable
fun NativeAdBundle(
  ad: MaxAd,
  template: NativeAdTemplate,
  onRender: (MaxNativeAdView, MaxAd) -> Unit,
  modifier: Modifier = Modifier,
  spaceBy: Int = 32,
) {
  Column(modifier = modifier.fillMaxWidth()) {
    BundleHeader(
      title = stringResource(R.string.native_ad_title),
      icon = null,
      hasMoreAction = false,
    )
    NativeAdCard(
      ad = ad,
      template = template,
      onRender = onRender,
      modifier = Modifier.padding(horizontal = 16.dp),
    )
    Spacer(modifier = Modifier.height(spaceBy.dp))
  }
}

/**
 * A native ad rendered as a row inside a browsing list (category, Top Charts, See All): a small
 * "Sponsored" label and the compact card, with the list's own horizontal padding already applied
 * by the caller.
 */
@Composable
fun NativeAdListCard(
  ad: MaxAd,
  template: NativeAdTemplate,
  onRender: (MaxNativeAdView, MaxAd) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = stringResource(R.string.native_ad_title),
      style = AGTypography.InputsS,
      color = Palette.GreyLight,
      modifier = Modifier.padding(bottom = 8.dp),
    )
    NativeAdCard(ad = ad, template = template, onRender = onRender)
  }
}

/** True while the host (screen / activity) lifecycle is at least RESUMED. */
@Composable
fun rememberHostResumed(): Boolean {
  val owner = LocalLifecycleOwner.current
  val state by owner.lifecycle.currentStateFlow.collectAsState()
  return state.isAtLeast(Lifecycle.State.RESUMED)
}

/**
 * Reports, on every layout of this element, whether it is near the viewport (see
 * [NativeAdVisibility]). Used by every ad slot, whatever its format, so all of them share one
 * definition of "near". An element that is not laid out (scrolled far out of a lazy list) is
 * reported by the caller's disposal, not by this modifier.
 */
@Composable
fun Modifier.trackNearViewport(
  preloadDistanceDp: Int = 400,
  onNearChanged: (Boolean) -> Unit,
): Modifier {
  val density = LocalDensity.current
  val screenHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
  val preloadDistancePx = with(density) { preloadDistanceDp.dp.toPx() }
  val latestCallback by rememberUpdatedState(onNearChanged)
  var near by remember { mutableStateOf(false) }
  return this.onGloballyPositioned { coordinates ->
    val isNear = NativeAdVisibility.isNearViewport(
      top = coordinates.positionInRoot().y,
      height = coordinates.size.height.toFloat(),
      screenHeight = screenHeightPx,
      preload = preloadDistancePx,
    )
    if (isNear != near) {
      near = isNear
      latestCallback(isNear)
    }
  }
}

/**
 * A native ad slot for long, non-lazy pages: takes no space until an ad is loaded, and only asks
 * [viewModel] to load once the slot comes within [preloadDistanceDp] of the bottom of the viewport.
 * Pages that are left before the user scrolls that far never request an ad. The slot reports
 * itself active while attached so deferred retries only run on a visible page.
 *
 * @param contentLength length of the content the slot belongs to (e.g. a description); forwarded to
 *   [NativeAdViewModel.startLoading] so a placement can skip short content via Remote Config.
 */
@Composable
fun NativeAdSlot(
  viewModel: NativeAdViewModel,
  modifier: Modifier = Modifier,
  preloadDistanceDp: Int = 400,
  contentLength: Int = Int.MAX_VALUE,
) {
  val state by viewModel.uiState.collectAsState()
  var nearViewport by remember { mutableStateOf(false) }
  val resumed = rememberHostResumed()

  // Active means near the viewport right now AND the host resumed: scrolling past the slot or
  // backgrounding the screen deactivates it, so deferred retries and expiry reloads wait.
  LaunchedEffect(viewModel, nearViewport, resumed) {
    viewModel.setSlotActive(nearViewport && resumed)
    if (nearViewport) viewModel.startLoading(contentLength)
  }
  DisposableEffect(viewModel) {
    onDispose { viewModel.setSlotActive(false) }
  }

  // The outer column is the measurable (zero-height until loaded) location; the caller's
  // spacing is applied only around a shown ad, so a hidden, disabled or skipped slot adds nothing.
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .trackNearViewport(preloadDistanceDp) { nearViewport = it },
  ) {
    (state as? NativeAdUiState.Loaded)?.let { loaded ->
      NativeAdBundle(
        ad = loaded.ad,
        template = viewModel.placement.template,
        onRender = viewModel::render,
        modifier = modifier,
      )
    }
  }
}

/** Manual native templates: bind the MAX asset ids to the views of the chosen layout. */
internal fun nativeAdViewBinder(template: NativeAdTemplate): MaxNativeAdViewBinder {
  val layout = when (template) {
    NativeAdTemplate.STANDARD -> R.layout.native_ad_view
    NativeAdTemplate.COMPACT -> R.layout.native_ad_compact_view
  }
  return MaxNativeAdViewBinder.Builder(layout)
    .setTitleTextViewId(R.id.native_ad_title)
    .setBodyTextViewId(R.id.native_ad_body)
    .setAdvertiserTextViewId(R.id.native_ad_advertiser)
    .setIconImageViewId(R.id.native_ad_icon)
    .setMediaContentViewGroupId(R.id.native_ad_media)
    .setOptionsContentViewGroupId(R.id.native_ad_options)
    .setStarRatingContentViewGroupId(R.id.native_ad_stars)
    .setCallToActionButtonId(R.id.native_ad_cta)
    .build()
}

private fun MaxNativeAdView.applyColors(colors: NativeAdCardColors) {
  findViewById<View>(R.id.native_ad_card)?.setBackgroundColor(colors.cardBackground.toArgb())
  findViewById<View>(R.id.native_ad_media)?.setBackgroundColor(colors.mediaBackground.toArgb())
  findViewById<View>(R.id.native_ad_badge)?.setBackgroundColor(colors.mediaBackground.toArgb())
  findViewById<TextView>(R.id.native_ad_badge)?.setTextColor(colors.accent.toArgb())
  findViewById<TextView>(R.id.native_ad_title)?.setTextColor(colors.textPrimary.toArgb())
  findViewById<TextView>(R.id.native_ad_body)?.setTextColor(colors.textSecondary.toArgb())
  findViewById<TextView>(R.id.native_ad_advertiser)?.setTextColor(colors.textSecondary.toArgb())
  findViewById<TextView>(R.id.native_ad_cta)?.let { cta ->
    cta.setBackgroundColor(colors.accent.toArgb())
    cta.setTextColor(colors.onAccent.toArgb())
  }
}
