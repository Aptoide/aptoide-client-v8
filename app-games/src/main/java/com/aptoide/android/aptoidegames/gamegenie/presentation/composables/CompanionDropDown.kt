package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

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
fun CompanionDropDown(
  games: List<GameCompanion>,
  onGameClick: (GameCompanion) -> Unit,
  modifier: Modifier = Modifier
) {
  if (games.isEmpty()) return

  Column(
    verticalArrangement = Arrangement.Center,
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .height(116.dp)
      .background(Palette.Black)
  ) {
    if (games.size == 1) {
      CompanionDropDownSingleGame(
        game = games.first(),
        onGameClick = onGameClick,
        textStyle = AGTypography.SmallGames
      )
    } else {
      CompanionDropDownMultipleGames(
        games = games,
        onGameClick = onGameClick,
        textStyle = AGTypography.SmallGames
      )
    }
  }
}

@Composable
internal fun CompanionDropDownSingleGame(
  game: GameCompanion,
  onGameClick: (GameCompanion) -> Unit,
  textStyle: androidx.compose.ui.text.TextStyle,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(modifier = Modifier.height(100.dp)) {
      GameCompanionIcon(
        game = game,
        onClick = onGameClick,
        textStyle = textStyle,
        textSize = textStyle.fontSize,
        imageSize = 56,
        textHeight = 36,
        showAnimation = false,
        showBorder = false
      )
    }
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Image(
        painter = painterResource(R.drawable.gamegenie_companion_show_more),
        contentDescription = null,
        modifier = Modifier.height(95.dp)
      )
      Spacer(modifier = Modifier.width(16.dp))
      Text(
        text = stringResource(R.string.game_genie_more_installed),
        style = AGTypography.ChatBold,
        color = Palette.GreyLight,
        modifier = Modifier.height(100.dp),
      )
    }
  }
}

@Composable
internal fun CompanionDropDownMultipleGames(
  games: List<GameCompanion>,
  onGameClick: (GameCompanion) -> Unit,
  textStyle: androidx.compose.ui.text.TextStyle,
  modifier: Modifier = Modifier,
) {
  LazyRow(
    modifier = modifier
      .fillMaxWidth()
      .height(100.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    items(games) { game ->
      GameCompanionIcon(
        game = game,
        onClick = onGameClick,
        textStyle = textStyle,
        textSize = textStyle.fontSize,
        imageSize = 56,
        textHeight = 36,
        showAnimation = false,
        showBorder = false
      )
    }
  }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun CompanionDropDownSinglePreview() {
  val mockGame = GameCompanion(
    name = "Epic Adventure",
    packageName = "com.example.epicadventure",
    versionName = "1.0.0",
    image = null
  )
  AptoideTheme {
    CompanionDropDown(
      games = listOf(mockGame),
      onGameClick = { }
    )
  }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun CompanionDropDownMultiplePreview() {
  val mockGames = listOf(
    GameCompanion(name = "Epic Adventure", packageName = "com.example.epic", versionName = "1.0", image = null),
    GameCompanion(name = "Racing Legends", packageName = "com.example.racing", versionName = "2.1", image = null),
    GameCompanion(name = "Puzzle Master", packageName = "com.example.puzzle", versionName = "1.5", image = null)
  )
  AptoideTheme {
    CompanionDropDown(
      games = mockGames,
      onGameClick = { }
    )
  }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun CompanionDropDownManyPreview() {
  val mockGames = listOf(
    GameCompanion(name = "Epic Adventure", packageName = "com.example.epic", versionName = "1.0", image = null),
    GameCompanion(name = "Racing Legends", packageName = "com.example.racing", versionName = "2.1", image = null),
    GameCompanion(name = "Puzzle Master", packageName = "com.example.puzzle", versionName = "1.5", image = null),
    GameCompanion(name = "Space Shooter", packageName = "com.example.space", versionName = "3.0", image = null),
    GameCompanion(name = "Kingdom Builder", packageName = "com.example.kingdom", versionName = "1.2", image = null)
  )
  AptoideTheme {
    CompanionDropDown(
      games = mockGames,
      onGameClick = { }
    )
  }
}
