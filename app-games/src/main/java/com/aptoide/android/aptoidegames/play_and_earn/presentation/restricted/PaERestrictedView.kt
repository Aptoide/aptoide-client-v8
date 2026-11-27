package com.aptoide.android.aptoidegames.play_and_earn.presentation.restricted

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
 * @param fullBleed it replaces a screen drawn behind the system bars: its text keeps clear of
 * them, its background does not.
 */
@Composable
fun PaERestrictedView(
  modifier: Modifier = Modifier,
  fullBleed: Boolean = false,
) {
  val context = LocalContext.current
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Palette.Black)
      .then(if (fullBleed) Modifier.systemBarsPadding() else Modifier)
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
      // The screen Settings > Support opens, written to the brand's support address.
      onClick = { SupportActivity.openForFeedBack(context) },
      modifier = Modifier.fillMaxWidth(),
      title = stringResource(R.string.contact_support_title),
    )
  }
}

@PreviewAll
@Composable
private fun PaERestrictedViewPreview() {
  AptoideTheme {
    PaERestrictedView()
  }
}
