package com.aptoide.android.aptoidegames.mmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import cm.aptoide.pt.feature_campaigns.UTMInfo
import com.aptoide.android.aptoidegames.analytics.presentation.withUtmInfo

internal val LocalUTMInfo = compositionLocalOf { UTMInfo() }

/**
 * Provides access to UTM tracking information within the composition tree.
 * Use [UTMContext.current] to read the current UTM info.
 */
object UTMContext {
  /**
   * Returns the current [UTMInfo] provided in the composition, or null if none is provided.
   */
  val current: UTMInfo
    @Composable
    @ReadOnlyComposable
    get() = LocalUTMInfo.current
}

@Composable
fun WithUTM(
  source: String? = null,
  medium: String? = null,
  campaign: String? = null,
  content: String? = null,
  term: String? = null,
  shouldSendClickEvents: Boolean = false,
  navigate: (String) -> Unit,
  block: @Composable ((String) -> Unit) -> Unit,
) {
  val parentUtmInfo = UTMContext.current
  val mergedUtmInfo = UTMInfo(
    utmSource = source ?: parentUtmInfo.utmSource,
    utmMedium = medium ?: parentUtmInfo.utmMedium,
    utmCampaign = campaign ?: parentUtmInfo.utmCampaign,
    utmContent = content ?: parentUtmInfo.utmContent,
    utmTerm = term ?: parentUtmInfo.utmTerm,
    //shouldSendClickEvents shouldn't default to parent's value. It should only send clicks at this scope. This avoids unnecessary click events if a parent UTM context as the wrong flag value.
    shouldSendClickEvents = shouldSendClickEvents
  )
  CompositionLocalProvider(LocalUTMInfo provides mergedUtmInfo) {
    block {
      navigate(it.withUtmInfo(mergedUtmInfo))
    }
  }
}
