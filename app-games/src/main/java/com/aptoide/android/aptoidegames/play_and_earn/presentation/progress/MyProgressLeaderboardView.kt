package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getSmallCoinIcon
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.FixedColors
import com.aptoide.android.aptoidegames.theme.Palette
import java.util.Locale

/** What the screen shows under the game selector. */
enum class ProgressView { MISSIONS, LEADERBOARD }

private val RowShape = RoundedCornerShape(12.dp)

private fun units(value: Long): String = String.format(Locale.getDefault(), "%,d", value)

/**
 * The Missions | Leaderboard switch: one control with two segments, one always selected. It does
 * not answer while a Claim's coins are flying ([enabled] false), so the animation is not cut.
 */
@Composable
internal fun ProgressViewSwitch(
  selected: ProgressView,
  enabled: Boolean,
  onSelect: (ProgressView) -> Unit,
) {
  Row(
    modifier = Modifier
      .padding(start = 16.dp, top = 12.dp, end = 16.dp)
      .fillMaxWidth()
      .clip(RowShape)
      .background(Palette.GreyDark)
      .padding(3.dp),
  ) {
    ProgressView.entries.forEach { view ->
      val isSelected = view == selected
      Text(
        text = stringResource(
          when (view) {
            ProgressView.MISSIONS -> R.string.play_and_earn_progress_view_missions
            ProgressView.LEADERBOARD -> R.string.play_and_earn_progress_view_leaderboard
          }
        ),
        style = AGTypography.InputsS.copy(fontSize = 13.sp),
        // On the brand accent the label keeps one color in both themes.
        color = if (isSelected) FixedColors.Dark else Palette.GreyLight,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(10.dp))
          .background(if (isSelected) Palette.Primary else Palette.GreyDark)
          .clickable(enabled = enabled && !isSelected) { onSelect(view) }
          .padding(vertical = 8.dp),
      )
    }
  }
}

@Composable
internal fun LeaderboardContent(
  gameName: String,
  board: LeaderboardView,
  period: LeaderboardPeriod,
  onPeriod: (LeaderboardPeriod) -> Unit,
) {
  Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)) {
    Text(
      text = stringResource(R.string.play_and_earn_progress_leaderboard_title, gameName),
      style = AGTypography.SubHeadingM,
      color = Palette.White,
    )
    Text(
      text = stringResource(R.string.play_and_earn_progress_leaderboard_subtitle, gameName),
      style = AGTypography.InputsS,
      color = Palette.GreyLight,
      modifier = Modifier.padding(top = 2.dp),
    )
  }
  Row(
    modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    LeaderboardPeriod.entries.forEach { option ->
      val isSelected = option == period
      Text(
        text = stringResource(
          when (option) {
            LeaderboardPeriod.WEEK -> R.string.play_and_earn_progress_leaderboard_week
            LeaderboardPeriod.MONTH -> R.string.play_and_earn_progress_leaderboard_month
            LeaderboardPeriod.ALL_TIME -> R.string.play_and_earn_progress_leaderboard_all_time
          }
        ),
        style = AGTypography.InputsXS,
        color = if (isSelected) FixedColors.Dark else Palette.White,
        modifier = Modifier
          .clip(CircleShape)
          .background(if (isSelected) Palette.Primary else Palette.Black)
          .border(1.dp, if (isSelected) Palette.Primary else Palette.Grey, CircleShape)
          .clickable(enabled = !isSelected) { onPeriod(option) }
          .padding(horizontal = 12.dp, vertical = 6.dp),
      )
    }
  }
  Podium(board.podium)
  Column(
    modifier = Modifier.padding(start = 16.dp, top = 10.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    board.rows.forEach { LeaderboardRow(it) }
  }
  if (board.isMeInTop) Note(tipText(board.tip))
  Note(stringResource(R.string.play_and_earn_progress_leaderboard_note))
}

/**
 * The player's own row when they are below the top ten: pinned to the bottom of the screen, with
 * how far the top ten is. The list fades out behind it.
 */
@Composable
internal fun PinnedLeaderboardRow(
  board: LeaderboardView,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(
        Brush.verticalGradient(
          0f to Palette.Black.copy(alpha = 0f),
          0.25f to Palette.Black,
          1f to Palette.Black,
        )
      )
      .padding(start = 16.dp, top = 22.dp, end = 16.dp, bottom = 12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    LeaderboardRow(board.me)
    Text(
      text = tipText(board.tip),
      style = AGTypography.InputsS,
      color = Palette.GreyLight,
      textAlign = TextAlign.Center,
    )
  }
}

/** Room to leave under the list for [PinnedLeaderboardRow]. */
internal val PinnedLeaderboardRowHeight = 110.dp

// Second, first, third: the winner stands in the middle, on the tallest base.
@Composable
private fun Podium(podium: List<RankedPlayer>) {
  val order = listOfNotNull(podium.getOrNull(1), podium.getOrNull(0), podium.getOrNull(2))
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.Bottom,
  ) {
    order.forEach { place ->
      val isFirst = place.rank == 1
      Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Box {
          Avatar(
            player = place.player,
            size = if (isFirst) 68.dp else 52.dp,
            fontSize = if (isFirst) 26 else 20,
            ring = isFirst,
          )
          Text(
            text = place.rank.toString(),
            style = AGTypography.InputsXS.copy(fontSize = 10.sp),
            color = if (isFirst) FixedColors.Dark else Palette.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .offset(x = 4.dp, y = 6.dp)
              .size(20.dp)
              .clip(CircleShape)
              .background(if (isFirst) Palette.Primary else Palette.Black)
              .border(2.dp, if (isFirst) Palette.Black else Palette.Grey, CircleShape)
              .padding(top = 3.dp),
          )
        }
        Text(
          text = place.player.name,
          style = AGTypography.InputsS,
          color = if (place.player.isMe) Palette.Primary else Palette.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.padding(top = 6.dp),
        )
        UnitsAmount(place.player.units, color = Palette.Primary, coin = 14.dp)
        Box(
          modifier = Modifier
            .padding(top = 4.dp)
            .fillMaxWidth()
            .height(
              when (place.rank) {
                1 -> 44.dp
                2 -> 30.dp
                else -> 20.dp
              }
            )
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            .background(Palette.GreyDark),
        )
      }
    }
  }
}

@Composable
private fun LeaderboardRow(place: RankedPlayer) {
  val isMe = place.player.isMe
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RowShape)
      .background(Palette.GreyDark)
      // The player's own row is tinted with the brand accent.
      .background(if (isMe) Palette.Primary.copy(alpha = 0.12f) else Color.Transparent)
      .border(1.dp, if (isMe) Palette.Primary else Color.Transparent, RowShape)
      .padding(horizontal = 12.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = place.rank.toString(),
      style = AGTypography.InputsS.copy(fontSize = 13.sp),
      color = if (isMe) Palette.Primary else Palette.GreyLight,
      textAlign = TextAlign.Center,
      modifier = Modifier.width(26.dp),
    )
    Avatar(player = place.player, size = 32.dp, fontSize = 13, ring = false)
    Text(
      text = place.player.name,
      style = AGTypography.InputsS.copy(fontSize = 13.sp),
      color = if (isMe) Palette.Primary else Palette.White,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.weight(1f),
    )
    UnitsAmount(place.player.units, color = Palette.White, coin = 16.dp)
  }
}

// Players have no picture: their initial on one of a few colors.
@Composable
private fun Avatar(
  player: LeaderboardPlayer,
  size: Dp,
  fontSize: Int,
  ring: Boolean,
) {
  val colors = listOf(
    Palette.Primary,
    Palette.SecondaryLight,
    Palette.Yellow100,
    Palette.Green,
    Palette.Yellow50,
  )
  Box(
    modifier = Modifier
      .size(size)
      .clip(CircleShape)
      .background(if (player.isMe) Palette.Primary else colors[player.color % colors.size])
      .border(2.dp, if (ring) Palette.Primary else Color.Transparent, CircleShape),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = player.name.take(1).uppercase(),
      style = AGTypography.InputsL.copy(fontSize = fontSize.sp),
      color = FixedColors.Dark,
    )
  }
}

@Composable
private fun UnitsAmount(
  amount: Long,
  color: Color,
  coin: Dp,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Image(
      imageVector = getSmallCoinIcon(),
      contentDescription = null,
      modifier = Modifier.size(coin),
    )
    Text(
      text = units(amount),
      style = AGTypography.InputsS.copy(fontSize = 13.sp),
      color = color,
      maxLines = 1,
    )
  }
}

@Composable
private fun Note(text: String) {
  Text(
    text = text,
    style = AGTypography.InputsS,
    color = Palette.GreyLight,
    textAlign = TextAlign.Center,
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, top = 12.dp, end = 16.dp),
  )
}

@Composable
private fun tipText(tip: LeaderboardTip): String = when (tip) {
  LeaderboardTip.First -> stringResource(R.string.play_and_earn_progress_leaderboard_tip_first)
  is LeaderboardTip.ReachRank -> stringResource(
    R.string.play_and_earn_progress_leaderboard_tip_rank,
    units(tip.units),
    tip.rank,
  )

  is LeaderboardTip.ReachTop -> stringResource(
    R.string.play_and_earn_progress_leaderboard_tip_top,
    units(tip.units),
  )
}
