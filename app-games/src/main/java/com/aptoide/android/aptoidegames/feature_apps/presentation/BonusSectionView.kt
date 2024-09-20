package com.aptoide.android.aptoidegames.feature_apps.presentation

import android.content.Context
import android.net.Uri.encode
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
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIcon
import com.aptoide.android.aptoidegames.drawables.icons.getForward
import com.aptoide.android.aptoidegames.drawables.icons.getPromoSection
import com.aptoide.android.aptoidegames.home.analytics.meta
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
    modifier = Modifier
      .clickable(
        enabled = true,
        onClick = getBonusRouteNavigation(
          context = context,
          bundle = bundle,
          navigate = navigate
        )
      )
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
        annotatedString = annotatedString,
        inlineContent = inlineContent
      )
      Image(
        modifier = Modifier
          .padding(top = 58.dp)
          .minimumInteractiveComponentSize()
          .size(18.dp),
        imageVector = getForward(Palette.White),
        contentDescription = null,
      )
    }
  }
}

@Composable
private fun BonusBannerText(
  annotatedString: AnnotatedString,
  inlineContent: Map<String, InlineTextContent>,
) {
  Column(
    modifier = Modifier
      .padding(start = 16.dp, top = 44.dp)
      .width(240.dp)
  ) {
    AptoideOutlinedText(
      text = stringResource(
        id = R.string.bonus_banner_title,
        "20"
      ), //TODO Hardcoded value (should come from backend in the future)
      style = AGTypography.Title,
      outlineWidth = 17f,
      outlineColor = Palette.Black,
      textColor = Palette.Primary,
    )
    Text(
      text = annotatedString,
      inlineContent = inlineContent,
      style = AGTypography.InputsM,
      color = Palette.White,
      maxLines = 2,
    )
  }
}

@Composable
fun getBonusRouteNavigation(
  context: Context,
  bundle: Bundle,
  navigate: (String) -> Unit,
): () -> Unit = {
  bundle.url
    ?.takeUnless(String::isBlank)
    ?.let { UrlActivity.open(context = context, url = it) }
    ?: navigate(
      buildSeeMoreBonusRoute(encode(bundle.title), "${bundle.tag}-more")
        .withBundleMeta(bundle.meta.copy(tag = "${bundle.tag}-more"))
    )
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
