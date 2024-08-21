package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.AptoideOutlinedText
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIcon
import com.aptoide.android.aptoidegames.drawables.icons.getPromoSection
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun BonusSectionView() {
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
    Column(modifier = Modifier.padding(start = 16.dp, top = 44.dp)) {
      AptoideOutlinedText(
        text = "Up to 20% Bonus", //TODO Hardcoded String,
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
}

@PreviewDark
@Composable
fun PreviewBonusSection() {
  BonusSectionView()
}
