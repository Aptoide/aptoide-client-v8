package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.AptoideOutlinedText
import com.aptoide.android.aptoidegames.drawables.icons.getPromoSection
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun BonusSectionView() {
  Box(
    modifier = Modifier.padding(vertical = 24.dp)
  ) {
    Image(
      imageVector = getPromoSection(
        iconColor = Palette.Primary,
        outlineColor = Palette.Black,
        backgroundColor = Palette.Secondary,
        themeColor = Palette.Black
      ),
      contentDescription = "Bonus Section",
      modifier = Modifier
        .fillMaxWidth(),
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
        text = "Enjoy a bonus in all purchases made on selected games.", //TODO Hardcoded String
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
