package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.AptoideOutlinedText
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.UrlActivity
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIcon
import com.aptoide.android.aptoidegames.drawables.icons.getForward
import com.aptoide.android.aptoidegames.drawables.icons.getPromoSection
import com.aptoide.android.aptoidegames.home.getSeeMoreBonusRouteNavigation
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun BonusSectionView(
  bundle: Bundle,
  navigate: (String) -> Unit,
) {
  val context = LocalContext.current
  Box(
    modifier = Modifier.padding(vertical = 24.dp)
  ) {
    val splitText = stringResource(id = R.string.bonus_banner_body).split("%s")
    val annotatedString = buildAnnotatedString {
      append(splitText[0])
      appendInlineContent(id = "%s")
      append(splitText[1])
    }
    val inlineContent = mapOf(
      "%s" to InlineTextContent(
        placeholder = Placeholder(
          width = 16.sp,
          height = 16.sp,
          placeholderVerticalAlign = PlaceholderVerticalAlign.TextBottom
        ),
        children = {
          Image(
            imageVector = getBonusIcon(
              outlineColor = Palette.Black,
              giftColor = Palette.Primary,
            ),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
        }
      )
    )
    Image(
      imageVector = getPromoSection(
        iconColor = Palette.Primary,
        outlineColor = Palette.Black,
        backgroundColor = Palette.Secondary,
        themeColor = Palette.Black
      ),
      contentDescription = "Bonus Section",
      modifier = Modifier.fillMaxWidth(),
      contentScale = ContentScale.FillWidth,
    )
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      BonusBannerText(
        title = bundle.title,
        annotatedString = annotatedString,
        inlineContent = inlineContent
      )
      ForwardButton(
        onClick = bundle.url
          ?.takeIf { it.isNotBlank() }
          ?.let {
            { UrlActivity.open(context = context, it) }
          } ?: getSeeMoreBonusRouteNavigation(bundle = bundle, navigate = navigate)
      )
    }
  }
}

@Composable
private fun ForwardButton(
  onClick: () -> Unit,
) {
  Image(
    modifier = Modifier
      .padding(top = 58.dp)
      .clickable { onClick() }
      .minimumInteractiveComponentSize(),
    imageVector = getForward(Palette.White),
    contentDescription = null,
  )
}

@Composable
private fun BonusBannerText(
  title: String,
  annotatedString: AnnotatedString,
  inlineContent: Map<String, InlineTextContent>,
) {
  Column(modifier = Modifier.padding(start = 16.dp, top = 44.dp)) {
    AptoideOutlinedText(
      text = title,
      style = AGTypography.Title,
      outlineWidth = 17f,
      outlineColor = Palette.Black,
      textColor = Palette.Primary,
      modifier = Modifier.width(162.dp)
    )
    Text(
      text = annotatedString,
      inlineContent = inlineContent,
      style = AGTypography.BodyBold,
      color = Palette.White,
      maxLines = 2,
      modifier = Modifier.width(240.dp)
    )
  }
}

@PreviewDark
@Composable
private fun RealBonusBundlePreview() {
  AptoideTheme {
    BonusSectionView(
      bundle = randomBundle,
      navigate = {}
    )
  }
}
