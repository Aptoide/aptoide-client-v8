package com.aptoide.android.aptoidegames.feature_apps.presentation

import android.content.Context
import android.net.Uri.encode
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.AptoideOutlinedText
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.UrlActivity
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIcon
import com.aptoide.android.aptoidegames.drawables.icons.getForward
import com.aptoide.android.aptoidegames.drawables.icons.getPromoSection
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.analytics.meta
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun BonusSectionView(
  bundle: Bundle,
  navigate: (String) -> Unit,
  spaceBy: Int = 0,
) {
  val context = LocalContext.current
  Column(modifier = Modifier.fillMaxWidth()) {
    BonusSectionHeader(
      onClick = getBonusRouteNavigation(
        context = context,
        bundle = bundle,
        navigate = navigate
      )
    )
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
    Text(
      modifier = Modifier.padding(start = 16.dp, end = 40.dp, top = 8.dp, bottom = 8.dp),
      text = annotatedString,
      inlineContent = inlineContent,
      style = AGTypography.InputsM,
      color = Palette.White,
      maxLines = 2,
    )
    BonusBundleView(
      bundle = bundle,
      navigate = navigate
    )
    Spacer(Modifier.size(spaceBy.dp))
  }
}

@Composable
fun BonusSectionHeader(
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .height(40.dp)
      .clickable(
        enabled = true,
        onClick = onClick
      ),
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Box(modifier = Modifier.weight(1f, fill = false)) {
      Image(
        imageVector = getPromoSection(
          backgroundColor = Palette.Secondary,
          themeColor = Palette.Black
        ),
        contentDescription = "Bonus Section",
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.FillWidth
      )
      Row(
        modifier = Modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
      ) {
        Image(
          modifier = Modifier.padding(horizontal = 16.dp),
          imageVector = getBonusIcon(
            giftColor = Palette.Primary,
            outlineColor = Palette.Black,
          ),
          contentDescription = null,
        )
        AptoideOutlinedText(
          text = stringResource(
            id = R.string.bonus_banner_title,
            "20" //TODO Hardcoded value (should come from backend in the future)
          ),
          style = AGTypography.Title,
          outlineWidth = 10f,
          outlineColor = Palette.Black,
          textColor = Palette.Primary
        )
      }
    }
    Spacer(Modifier.width(40.dp))
    Column {
      Spacer(Modifier.height(16.dp))
      Image(
        imageVector = getForward(Palette.Primary),
        modifier = Modifier.size(18.dp),
        contentDescription = null,
      )
    }

    Spacer(Modifier.width(16.dp))
  }
}

@Composable
fun BonusBundleView(
  bundle: Bundle,
  navigate: (String) -> Unit
) {
  val (uiState, _) = rememberAppsByTag(bundle.tag)
  when (uiState) {
    is AppsListUiState.Idle -> AppsRowView(
      appsList = uiState.apps,
      navigate = navigate,
    )

    AppsListUiState.Empty,
    AppsListUiState.Error,
    AppsListUiState.NoConnection,
      -> Unit

    AppsListUiState.Loading -> LoadingBundleView(height = 184.dp)
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
