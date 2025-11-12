package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.getForward
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getPaECoins
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getPaESmallLogo
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PaECard(
  onClick: () -> Unit = {}
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(4.09f)
  ) {
    Box(
      modifier = Modifier
        .padding(top = 8.dp)
        .clickable(onClick = onClick)
    ) {
      Image(
        modifier = Modifier.fillMaxWidth(),
        painter = painterResource(id = R.drawable.play_and_earn_bg),
        contentDescription = null,
        contentScale = ContentScale.FillWidth
      )

      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(
          verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Image(
              imageVector = getPaESmallLogo(Palette.White, Palette.Secondary),
              contentDescription = null,
              contentScale = ContentScale.FillHeight
            )

            Text(
              text = stringResource(R.string.play_and_earn_title),
              style = AGTypography.InputsL,
              color = Palette.White
            )
          }

          Text(
            text = stringResource(R.string.play_and_earn_earn_rewards_by_playing_body),
            style = AGTypography.BodyBold,
            color = Palette.White
          )
        }

        Image(
          modifier = Modifier.size(18.dp),
          imageVector = getForward(Palette.White, Palette.Secondary),
          contentDescription = null,
          contentScale = ContentScale.FillHeight
        )
      }
    }


    Image(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 48.dp),
      imageVector = getPaECoins(),
      contentDescription = null,
      contentScale = ContentScale.FillHeight
    )
  }
}

@Preview
@Composable
private fun PaECardPreview() {
  Box(
    modifier = Modifier.width(360.dp),
    contentAlignment = Alignment.Center
  ) {
    PaECard()
  }
}
