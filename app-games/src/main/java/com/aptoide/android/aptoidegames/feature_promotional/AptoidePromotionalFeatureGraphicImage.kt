package com.aptoide.android.aptoidegames.feature_promotional

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.getBonusPromotional
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.FixedColors
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun AptoidePromotionalFeatureGraphicImage(
  featureGraphic: String?,
  label: String,
  hasAppCoins: Boolean = false,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .padding(bottom = 8.dp)
      .fillMaxWidth()
  ) {
    AptoideFeatureGraphicImage(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(328 / 160f),
      data = featureGraphic,
      contentDescription = null
    )
    val isVanilla = BuildConfig.FLAVOR_brand == "vanilla"
    Text(
      text = label,
      style = AGTypography.BodyBold,
      color = if (isVanilla) FixedColors.White else Palette.Primary,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier
        .padding(start = 8.dp, top = 8.dp)
        .let { if (isVanilla) it.clip(RoundedCornerShape(16.dp)) else it }
        .background(color = if (isVanilla) FixedColors.Dark else Palette.Black)
        .padding(horizontal = 6.dp, vertical = 4.dp)
    )

    if (hasAppCoins) {
      if (isVanilla) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            .background(FixedColors.VanillaOrange)
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            text = stringResource(R.string.promotional_bonus_up_to),
            style = AGTypography.InputsXSRegular,
            color = FixedColors.Dark,
          )
          Text(
            text = stringResource(R.string.promotional_bonus_amount),
            style = AGTypography.Title,
            color = FixedColors.Dark,
          )
          Text(
            text = stringResource(R.string.promotional_bonus_label),
            style = AGTypography.InputsXS,
            color = FixedColors.Dark,
          )
        }
      } else {
        Image(
          imageVector = getBonusPromotional(Palette.Primary, Palette.Secondary, Palette.Black),
          contentDescription = null,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .size(72.dp, 80.dp)
        )
      }
    }
  }
}
