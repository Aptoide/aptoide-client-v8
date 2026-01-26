package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun CompanionGameChoice(
  title: String,
  games: List<GameCompanion>,
  onGameClick: (GameCompanion) -> Unit,
  modifier: Modifier = Modifier,
) {
  AnimatedVisibility(
    visible = games.isNotEmpty(),
    enter = slideInHorizontally { it },
    exit = slideOutHorizontally { it },
    modifier = modifier
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp, bottom = 16.dp)
        .height(172.dp)
        .background(color = Palette.Primary.copy(alpha = 0.1f))
        .border(width = 1.dp, color = Palette.Primary)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Image(
            imageVector = getCompanionChessIcon(),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
          )
          Text(
            text = title,
            style = AGTypography.Chat,
            color = Palette.Primary
          )
        }

        if (games.size == 1) {
          CompanionDropDownSingleGame(
            game = games.first(),
            onGameClick = onGameClick,
            textStyle = AGTypography.SmallGames.copy(color = Palette.Primary),
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 16.dp)
          )
        } else {
          CompanionDropDownMultipleGames(
            games = games,
            onGameClick = onGameClick,
            textStyle = AGTypography.SmallGames.copy(color = Palette.Primary),
            modifier = Modifier
              .fillMaxWidth()
              .height(100.dp)
              .padding(top = 16.dp)
          )
        }
      }
    }
  }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun CompanionGameChoiceSinglePreview() {
  val mockGame = GameCompanion(
    name = "Epic Adventure",
    packageName = "com.example.epicadventure",
    versionName = "1.0.0",
    image = null
  )
  AptoideTheme {
    CompanionGameChoice(
      title = "Top Pick for You",
      games = listOf(mockGame),
      onGameClick = { }
    )
  }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun CompanionGameChoiceMultiplePreview() {
  val mockGames = listOf(
    GameCompanion(
      name = "Epic Adventure", packageName = "com.example.epic", versionName = "1.0", image = null
    ),
    GameCompanion(
      name = "Racing Legends", packageName = "com.example.racing", versionName = "2.1", image = null
    ),
    GameCompanion(
      name = "Puzzle Master", packageName = "com.example.puzzle", versionName = "1.5", image = null
    )
  )
  AptoideTheme {
    CompanionGameChoice(
      title = "Games You Might Like",
      games = mockGames,
      onGameClick = { }
    )
  }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun CompanionGameChoiceManyPreview() {
  val mockGames = listOf(
    GameCompanion(
      name = "Epic Adventure", packageName = "com.example.epic", versionName = "1.0", image = null
    ),
    GameCompanion(
      name = "Racing Legends", packageName = "com.example.racing", versionName = "2.1", image = null
    ),
    GameCompanion(
      name = "Puzzle Master", packageName = "com.example.puzzle", versionName = "1.5", image = null
    ),
    GameCompanion(
      name = "Space Shooter", packageName = "com.example.space", versionName = "3.0", image = null
    ),
    GameCompanion(
      name = "Kingdom Builder", packageName = "com.example.kingdom", versionName = "1.2",
      image = null
    )
  )
  AptoideTheme {
    CompanionGameChoice(
      title = "More Games to Explore",
      games = mockGames,
      onGameClick = { }
    )
  }
}
