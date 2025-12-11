package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PlaySmarterBox(
  modifier: Modifier = Modifier,
  onPlayClick: () -> Unit = {},
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(72.dp)
      .background(Palette.Secondary.copy(alpha = 0.2f))
      .border(width = 1.dp, color = Palette.Secondary)
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Text(
      text = stringResource(id = R.string.gamegenie_companion_play_smarter),
      style = AGTypography.Chat.copy(color = Palette.White),
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.weight(1f)
    )

    Box(
      modifier = Modifier
        .wrapContentSize()
    ) {
      Box(
        modifier = Modifier
          .height(40.dp)
          .width(73.dp)
          .background(Palette.Secondary)
          .clickable { onPlayClick() },
        contentAlignment = Alignment.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = stringResource(id = R.string.gamegenie_companion_play),
            style = AGTypography.ChatBold,
            color = Color.White
          )
          Spacer(modifier = Modifier.size(4.dp))
          Image(
            imageVector = getRightArrow(
              Palette.White,
              bgColor = Color.Transparent
            ),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Box(
        modifier = Modifier
          .matchParentSize()
          .align(Alignment.Center)
      ) {
        AnimationComposable(
          modifier = Modifier
            .width(100.dp)
            .height(40.dp),
          resId = R.raw.game_genie_play_companion,
        )
      }
    }
  }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun PlaySmarterBoxPreview() {
  MaterialTheme {
    Box(Modifier.padding(16.dp)) {
      PlaySmarterBox()
    }
  }
}