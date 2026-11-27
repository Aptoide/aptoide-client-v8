package com.aptoide.android.aptoidegames.play_and_earn.presentation.restricted

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewAll
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.SupportActivity
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.drawables.icons.getGenericError
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

/**
 * Shown instead of a Play & Earn screen to an account the backend restricted for fraud
 * (AND-951), with a way to reach support.
 *
 * It paints its own background, the theme's screen colour ([Palette.Black]), and writes on it in
 * the theme's text colours ([Palette.White], [Palette.AccentText]): it replaces screens drawn on
 * the usual background, on the redeem flow's purple gradient and full-bleed, and reads the same
 * on each, in Aptoide Games and in both Vanilla themes. It fills what it is given: the whole
 * screen, or the height of its content inside a scrolling one (the game's Rewards tab).
 *
 * @param fullBleed it replaces a screen drawn behind the system bars: its content keeps clear of
 * them, its background does not.
 * @param scrollable it scrolls on its own (a full screen, so large fonts never clip the button);
 * false inside a parent that already scrolls.
 * @param navigateBack draws the app's back arrow above it, when non-null.
 */
@Composable
fun PaERestrictedView(
  modifier: Modifier = Modifier,
  fullBleed: Boolean = false,
  scrollable: Boolean = true,
  navigateBack: (() -> Unit)? = null,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Palette.Black)
      .then(if (fullBleed) Modifier.systemBarsPadding() else Modifier)
  ) {
    navigateBack?.let { AppGamesTopBar(navigateBack = it, title = "") }
    if (scrollable) {
      // Centred while it fits, scrolling when it does not (small screens, large fonts).
      BoxWithConstraints(modifier = Modifier.weight(1f)) {
        RestrictedContent(
          modifier = Modifier
            .verticalScroll(rememberScrollState())
            .heightIn(min = maxHeight)
        )
      }
    } else {
      RestrictedContent()
    }
  }
}

@Composable
private fun RestrictedContent(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Image(
      imageVector = getGenericError(Palette.Primary, Palette.GreyLight, Palette.White),
      contentDescription = null,
      modifier = Modifier.size(144.dp),
    )
    Spacer(modifier = Modifier.height(32.dp))
    Text(
      text = stringResource(R.string.play_and_earn_exchange_error_restricted_title),
      modifier = Modifier.semantics { heading() },
      style = AGTypography.Title,
      color = Palette.AccentText,
      textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = stringResource(R.string.play_and_earn_exchange_error_restricted_body),
      style = AGTypography.DescriptionGames,
      color = Palette.White,
      textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(32.dp))
    PrimaryButton(
      // The support e-mail form, written to the brand's support address, with what support
      // needs to find the account.
      onClick = { SupportActivity.openForRestrictedAccount(context) },
      modifier = Modifier.fillMaxWidth(),
      title = stringResource(R.string.contact_support_title),
    )
  }
}

@PreviewAll
@Composable
private fun PaERestrictedViewPreview() {
  AptoideTheme {
    PaERestrictedView(navigateBack = {})
  }
}
