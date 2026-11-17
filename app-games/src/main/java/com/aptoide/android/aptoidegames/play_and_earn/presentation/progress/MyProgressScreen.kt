package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getSmallCoinIcon
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.rememberPaEPlayTap
import com.aptoide.android.aptoidegames.play_and_earn.presentation.overlays.BALANCE_COUNT_MILLIS
import com.aptoide.android.aptoidegames.play_and_earn.presentation.overlays.CLAIM_COINS
import com.aptoide.android.aptoidegames.play_and_earn.presentation.overlays.COINS_FLIGHT_MILLIS
import com.aptoide.android.aptoidegames.play_and_earn.presentation.overlays.FlyingCoin
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.FixedColors
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import kotlinx.coroutines.delay
import java.util.Locale

const val myProgressRoute = "myProgress"

fun myProgressScreen() = ScreenData.withAnalytics(
  route = myProgressRoute,
  screenAnalyticsName = "MyProgress",
) { _, navigate, navigateBack ->
  val (uiState, claim) = rememberMyProgress()
  MyProgressScreen(
    uiState = uiState,
    navigateBack = navigateBack,
    onClaim = claim,
    navigate = navigate,
  )
}

private val CardShape = RoundedCornerShape(14.dp)
private val TileShape = RoundedCornerShape(12.dp)

@Composable
fun MyProgressScreen(
  uiState: MyProgressUiState,
  navigateBack: () -> Unit,
  onClaim: (String) -> Unit = {},
  navigate: ((String) -> Unit)? = null,
) {
  Column(modifier = Modifier.fillMaxSize()) {
    AppGamesTopBar(
      navigateBack = navigateBack,
      title = stringResource(R.string.play_and_earn_progress_title),
    )
    when (uiState) {
      MyProgressUiState.Loading -> Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        IndeterminateCircularLoading(color = Palette.Primary)
      }

      is MyProgressUiState.Error -> GenericErrorView(onRetryClick = uiState.retry)

      is MyProgressUiState.Idle -> MyProgressContent(
        state = uiState,
        onClaim = onClaim,
        navigate = navigate,
      )
    }
  }
}

/** A Claim in flight: the coins are on their way from the button to the balance. */
private data class ClaimFlight(val id: String, val from: Offset)

private fun LayoutCoordinates.centerInWindow(): Offset =
  positionInWindow() + Offset(size.width / 2f, size.height / 2f)

@Composable
private fun MyProgressContent(
  state: MyProgressUiState.Idle,
  onClaim: (String) -> Unit,
  navigate: ((String) -> Unit)?,
) {
  var selected by rememberSaveable { mutableStateOf<String?>(null) }
  val game = state.games.find { it.packageName == selected }
    ?: state.games.find { it.packageName == defaultGame(state.games, state.claimed) }

  var view by rememberSaveable { mutableStateOf(ProgressView.MISSIONS) }
  var period by rememberSaveable { mutableStateOf(LeaderboardPeriod.WEEK) }
  val myName = stringResource(R.string.play_and_earn_progress_leaderboard_you)
  // The chosen game's ranking, while the Leaderboard view is showing. The other players are demo
  // data for now; the player is ranked by what the game's missions really paid them in the
  // period, and is not in it when that is nothing.
  val board = if (SHOW_DEMO_DATA && game != null && view == ProgressView.LEADERBOARD) {
    remember(game, period, state.claimed, myName) {
      leaderboardView(
        others = demoLeaderboardPlayers(game.packageName, period),
        myUnits = leaderboardUnits(game, period, state.claimed),
        myName = myName,
      )
    }
  } else {
    null
  }

  var origin by remember { mutableStateOf(Offset.Zero) }
  var balanceCenter by remember { mutableStateOf<Offset?>(null) }
  var flight by remember { mutableStateOf<ClaimFlight?>(null) }

  // The reward only counts as collected once the coins have landed, as in the prototype.
  LaunchedEffect(flight) {
    flight?.let {
      delay(COINS_FLIGHT_MILLIS.toLong())
      onClaim(it.id)
      flight = null
    }
  }
  val claim: (String, Offset) -> Unit = { id, from ->
    if (flight == null) {
      if (balanceCenter == null) onClaim(id) else flight = ClaimFlight(id, from)
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .onGloballyPositioned { origin = it.positionInWindow() }
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      TitleRow(
        balance = shownBalance(state.walletBalance, state.allCards, state.claimed),
        onCoinPositioned = { balanceCenter = it.centerInWindow() },
      )
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(
            bottom = if (board?.isMeBelowTop == true) PinnedLeaderboardRowHeight else 32.dp
          ),
      ) {
        Text(
          text = stringResource(
            if (state.games.isEmpty()) {
              R.string.play_and_earn_progress_games_empty
            } else {
              R.string.play_and_earn_progress_games_hint
            }
          ),
          style = AGTypography.InputsS,
          color = Palette.GreyLight,
          modifier = Modifier.padding(horizontal = 16.dp),
        )
        if (state.games.isNotEmpty()) {
          GameSelector(
            games = state.games,
            selected = game?.packageName,
            claimed = state.claimed,
            onSelect = { selected = it },
          )
        }
        if (game != null && SHOW_DEMO_DATA) {
          ProgressViewSwitch(
            selected = view,
            enabled = flight == null,
            onSelect = { view = it },
          )
        }
        if (board != null && game != null) {
          LeaderboardContent(
            gameName = game.name,
            board = board,
            period = period,
            onPeriod = { period = it },
          )
        } else {
          game?.let {
            key(it.packageName) { PlayGameButton(game = it, navigate = navigate) }
            StreakCard(streak = it.streak)
            GameMissions(game = it, claimed = state.claimed, onClaim = claim)
          }
          SectionHeader(title = stringResource(R.string.play_and_earn_progress_totals_title))
          TotalsRow(
            lifetimePlaySeconds = state.lifetimePlaySeconds,
            lifetimeUnits = state.lifetimeUnits,
          )
        }
      }
    }

    val me = board?.me
    val tip = board?.tip
    if (board?.isMeBelowTop == true && me != null && tip != null) {
      PinnedLeaderboardRow(me = me, tip = tip, modifier = Modifier.align(Alignment.BottomCenter))
    }

    val from = flight?.from
    val to = balanceCenter
    if (from != null && to != null) {
      key(flight?.id) {
        repeat(CLAIM_COINS) { index ->
          FlyingCoin(index = index, from = from - origin, to = to - origin)
        }
      }
    }
  }
}

@Composable
private fun TitleRow(
  balance: Long?,
  onCoinPositioned: (LayoutCoordinates) -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = stringResource(R.string.play_and_earn_progress_missions_heading),
      style = AGTypography.Title,
      color = Palette.White,
      modifier = Modifier.weight(1f),
    )
    if (balance != null) {
      val shown by animateIntAsState(
        targetValue = balance.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
        animationSpec = tween(BALANCE_COUNT_MILLIS),
        label = "balance",
      )
      Row(
        modifier = Modifier
          .clip(CircleShape)
          .background(Palette.GreyDark)
          .padding(start = 6.dp, top = 4.dp, end = 10.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Image(
          imageVector = getSmallCoinIcon(),
          contentDescription = null,
          modifier = Modifier
            .size(18.dp)
            .onGloballyPositioned(onCoinPositioned),
        )
        Text(
          text = String.format(Locale.getDefault(), "%,d", shown),
          style = AGTypography.InputsS,
          color = Palette.White,
          maxLines = 1,
        )
      }
    }
  }
}

@Composable
private fun GameSelector(
  games: List<GameProgress>,
  selected: String?,
  claimed: Set<String>,
  onSelect: (String) -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    games.forEach { game ->
      val isSelected = game.packageName == selected
      val ready = game.readyCount(claimed)
      Column(
        modifier = Modifier
          .width(76.dp)
          .clickable { onSelect(game.packageName) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Box(modifier = Modifier.padding(top = 6.dp)) {
          Box(
            modifier = Modifier
              .size(68.dp)
              .border(
                width = 2.dp,
                color = if (isSelected) Palette.Primary else Palette.Black,
                shape = RoundedCornerShape(16.dp),
              )
              .padding(4.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Palette.GreyDark),
          ) {
            when {
              game.iconRes != null -> Image(
                painter = painterResource(game.iconRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
              )

              game.icon != null -> AptoideAsyncImage(
                data = game.icon,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
              )
            }
          }
          if (ready > 0) {
            Text(
              text = ready.toString(),
              style = AGTypography.InputsXS,
              // On the brand accent the label keeps one color in both themes.
              color = FixedColors.Dark,
              textAlign = TextAlign.Center,
              modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 6.dp, y = (-6).dp)
                .defaultMinSize(minWidth = 20.dp)
                .clip(CircleShape)
                .background(Palette.Primary)
                .padding(horizontal = 5.dp, vertical = 2.dp),
            )
          }
        }
        Text(
          text = game.name,
          style = AGTypography.InputsXSRegular,
          color = if (isSelected) Palette.White else Palette.GreyLight,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

/**
 * Opens the chosen game the way the Play button of its page does: signed in, with the play-time
 * tracking (or the MMP status heartbeats) the game's campaign needs, so what is played from here
 * counts towards the missions and the streak below. The demo game has no campaign to track for:
 * it is just opened. Not shown for a game that is not installed.
 */
@Composable
private fun PlayGameButton(
  game: GameProgress,
  navigate: ((String) -> Unit)?,
) {
  val context = LocalContext.current
  val launchIntent = remember(game.packageName) {
    context.packageManager.getLaunchIntentForPackage(game.packageName)
  } ?: return
  val playTap = rememberPaEPlayTap(packageName = game.packageName, navigate = navigate)

  val open = { context.startActivity(launchIntent) }
  PrimaryButton(
    onClick = if (game.isDemo) open else playTap.guard(open),
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, top = 16.dp, end = 16.dp),
    title = stringResource(R.string.play_and_earn_progress_play_game, game.name),
  )
}

@Composable
private fun StreakCard(streak: GameWeekStreak) {
  val tiles = streakTiles(streak)
  Column(
    modifier = Modifier
      .padding(start = 16.dp, top = 16.dp, end = 16.dp)
      .fillMaxWidth()
      .clip(CardShape)
      .background(Palette.GreyDark)
      .padding(12.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = stringResource(R.string.play_and_earn_progress_streak_title),
        style = AGTypography.SubHeadingM,
        color = Palette.White,
        modifier = Modifier.weight(1f),
      )
      Text(
        text = stringResource(
          R.string.play_and_earn_progress_streak_days,
          streak.daysPlayed,
          DAYS_IN_STREAK,
        ),
        style = AGTypography.InputsS,
        color = Palette.GreyLight,
      )
    }
    Spacer(modifier = Modifier.height(12.dp))
    // Four days above, three below with the last one twice as wide.
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      tiles.take(4).forEach { StreakTileView(it, Modifier.weight(1f)) }
    }
    Spacer(modifier = Modifier.height(14.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      tiles.drop(4).forEachIndexed { index, tile ->
        StreakTileView(tile, Modifier.weight(if (index == 2) 2f else 1f))
      }
    }
    Spacer(modifier = Modifier.height(14.dp))
    Text(
      text = streakLine(streak, tiles),
      style = AGTypography.InputsS,
      color = Palette.GreyLight,
    )
  }
}

@Composable
private fun streakLine(
  streak: GameWeekStreak,
  tiles: List<StreakTile>,
): String {
  val today = tiles.find { it.state == StreakTileState.TODAY }
  return when {
    streak.isComplete -> stringResource(R.string.play_and_earn_progress_streak_complete)
    today == null -> stringResource(R.string.play_and_earn_progress_streak_tomorrow)
    else -> when (val reward = today.reward) {
      is StreakReward.Units -> stringResource(
        R.string.play_and_earn_progress_streak_today_reward,
        today.day,
        reward.units,
      )

      is StreakReward.Item -> stringResource(
        R.string.play_and_earn_progress_streak_today_item,
        today.day,
        reward.label,
      )

      null -> stringResource(R.string.play_and_earn_progress_streak_today, today.day)
    }
  }
}

@Composable
private fun StreakTileView(
  tile: StreakTile,
  modifier: Modifier = Modifier,
) {
  val played = tile.state == StreakTileState.PLAYED
  val isToday = tile.state == StreakTileState.TODAY
  Box(
    modifier = modifier
      .height(78.dp)
      .clip(TileShape)
      .background(Palette.Black)
      // Only the day the player is on stands out.
      .border(1.dp, if (isToday) Palette.Primary else Palette.Black, TileShape),
    contentAlignment = Alignment.Center,
  ) {
    if (played) {
      // A day whose reward was collected steps back, so today's is what the eye lands on.
      Column(
        modifier = Modifier.alpha(0.55f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(
          text = "✓",
          style = AGTypography.InputsS,
          color = FixedColors.Dark,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Palette.Primary)
            .padding(top = 3.dp),
        )
        Text(
          text = stringResource(R.string.play_and_earn_progress_streak_claimed),
          style = AGTypography.InputsXS,
          color = Palette.Primary,
        )
      }
    } else {
      val item = tile.reward as? StreakReward.Item
      if (item?.glow == true) StreakGlow()
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
      ) {
        // The reward worth showing off takes the whole tile, with no text around it.
        if (item?.glow != true) {
          Text(
            text = stringResource(R.string.play_and_earn_progress_streak_day, tile.day),
            style = AGTypography.InputsXS,
            color = if (isToday) Palette.Primary else Palette.GreyLight,
            maxLines = 1,
          )
        }
        when (val reward = tile.reward) {
          is StreakReward.Item -> Image(
            painter = painterResource(reward.imageRes),
            contentDescription = reward.label,
            modifier = Modifier.height(if (reward.glow) 54.dp else 34.dp),
          )

          else -> Image(
            imageVector = getSmallCoinIcon(),
            contentDescription = null,
            modifier = Modifier
              .size(24.dp)
              .alpha(if (reward != null) 1f else 0.25f),
          )
        }
        val value = when (val reward = tile.reward) {
          is StreakReward.Units -> reward.units.toString()
          is StreakReward.Item -> reward.shortLabel
          null -> null
        }
        value?.let {
          Text(
            text = it,
            style = AGTypography.InputsXS,
            color = Palette.White,
            maxLines = 1,
          )
        }
      }
    }
  }
}

/** The golden light pulsing behind the streak's best reward, as in the prototype. */
@Composable
private fun StreakGlow() {
  val pulse by rememberInfiniteTransition(label = "glow").animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
    label = "glow",
  )
  val gold = Palette.Yellow100
  Box(
    modifier = Modifier
      .size(120.dp)
      .graphicsLayer {
        alpha = 0.7f + 0.3f * pulse
        scaleX = 0.9f + 0.2f * pulse
        scaleY = 0.9f + 0.2f * pulse
      }
      .background(
        Brush.radialGradient(
          0f to gold.copy(alpha = 0.85f),
          0.4f to gold.copy(alpha = 0.35f),
          0.7f to gold.copy(alpha = 0f),
        )
      ),
  )
}

@Composable
private fun GameMissions(
  game: GameProgress,
  claimed: Set<String>,
  onClaim: (String, Offset) -> Unit,
) {
  if (game.missions.isEmpty()) {
    Text(
      text = stringResource(R.string.play_and_earn_progress_game_missions_empty),
      style = AGTypography.InputsS,
      color = Palette.GreyLight,
      modifier = Modifier.padding(start = 16.dp, top = 20.dp, end = 16.dp),
    )
    return
  }
  MissionKind.entries.forEach { kind ->
    val cards = game.missions.filter { it.kind == kind }.sortedForDisplay(claimed)
    if (cards.isNotEmpty()) {
      SectionHeader(
        title = stringResource(
          when (kind) {
            MissionKind.IN_GAME -> R.string.play_and_earn_progress_section_in_game
            MissionKind.PURCHASE -> R.string.play_and_earn_progress_section_purchases
            MissionKind.PLAY_TIME -> R.string.play_and_earn_progress_section_play_time
          }
        ),
        subtitle = stringResource(
          when (kind) {
            MissionKind.IN_GAME -> R.string.play_and_earn_progress_section_in_game_sub
            MissionKind.PURCHASE -> R.string.play_and_earn_progress_section_purchases_sub
            MissionKind.PLAY_TIME -> R.string.play_and_earn_progress_section_play_time_sub
          }
        ),
        iconRes = when (kind) {
          MissionKind.IN_GAME -> R.drawable.pae_section_ingame_missions
          MissionKind.PURCHASE -> R.drawable.pae_section_purchases
          MissionKind.PLAY_TIME -> R.drawable.pae_section_play_time
        },
      )
      CardList {
        cards.forEach { card ->
          key(card.id) { MissionCardView(card, card.state(claimed), onClaim) }
        }
      }
    }
  }
}

@Composable
private fun SectionHeader(
  title: String,
  subtitle: String? = null,
  iconRes: Int? = null,
) {
  Row(
    modifier = Modifier.padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    iconRes?.let {
      Image(
        painter = painterResource(it),
        contentDescription = null,
        modifier = Modifier.size(34.dp),
      )
    }
    Text(
      text = title,
      style = AGTypography.SubHeadingM,
      color = Palette.White,
    )
    subtitle?.let {
      Text(
        text = it,
        style = AGTypography.InputsS,
        color = Palette.GreyLight,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
private fun CardList(content: @Composable () -> Unit) {
  Column(
    modifier = Modifier.padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    content()
  }
}

@Composable
private fun MissionCardView(
  card: MissionCard,
  state: CardState,
  onClaim: (String, Offset) -> Unit,
) {
  var buttonCenter by remember { mutableStateOf(Offset.Zero) }
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .alpha(if (state == CardState.CLAIMED) 0.55f else 1f)
      .clip(CardShape)
      .background(Palette.GreyDark)
      .border(
        width = 1.dp,
        color = if (state == CardState.READY) Palette.Primary else Palette.GreyDark,
        shape = CardShape,
      )
      .padding(12.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = card.title,
          style = AGTypography.InputsL,
          color = Palette.White,
        )
        card.description?.let {
          Text(
            text = it,
            style = AGTypography.InputsS,
            color = Palette.GreyLight,
            modifier = Modifier.padding(top = 2.dp),
          )
        }
      }
      if (state == CardState.CLAIMED) {
        Text(
          text = stringResource(R.string.play_and_earn_progress_mission_claimed),
          style = AGTypography.InputsS,
          color = Palette.Primary,
          maxLines = 1,
        )
      } else {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Image(
            imageVector = getSmallCoinIcon(),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
          Text(
            text = "+${card.units}",
            style = AGTypography.InputsS,
            color = Palette.Primary,
            maxLines = 1,
          )
        }
      }
    }
    if (state == CardState.CLAIMED) return@Column
    run {
      Box(
        modifier = Modifier
          .padding(top = 10.dp)
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(Palette.Grey),
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(card.fraction)
            .fillMaxHeight()
            .clip(RoundedCornerShape(3.dp))
            .background(Palette.Primary),
        )
      }
    }
    Row(
      modifier = Modifier
        .padding(top = 6.dp)
        .fillMaxWidth()
        .defaultMinSize(minHeight = 32.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = stringResource(
          R.string.play_and_earn_progress_mission_amount,
          card.amounts?.current ?: amountText(card, card.current),
          card.amounts?.target ?: amountText(card, card.target),
        ),
        style = AGTypography.InputsS,
        color = Palette.White,
        modifier = Modifier.weight(1f),
      )
      when (state) {
        CardState.READY -> Text(
          text = stringResource(R.string.play_and_earn_progress_mission_claim, card.units),
          style = AGTypography.InputsS,
          color = FixedColors.Dark,
          modifier = Modifier
            .onGloballyPositioned { buttonCenter = it.centerInWindow() }
            .clip(RoundedCornerShape(10.dp))
            .background(Palette.Primary)
            .clickable { onClaim(card.id, buttonCenter) }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        )

        CardState.CLAIMED -> Unit

        CardState.IN_PROGRESS -> Text(
          text = stringResource(
            R.string.play_and_earn_progress_mission_to_go,
            card.amounts?.toGo ?: amountText(card, (card.target - card.current).coerceAtLeast(0)),
          ),
          style = AGTypography.InputsS,
          color = Palette.GreyLight,
        )
      }
    }
  }
}

@Composable
private fun TotalsRow(
  lifetimePlaySeconds: Long,
  lifetimeUnits: Long,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    TotalCard(
      label = stringResource(R.string.play_and_earn_progress_units_earned_label),
      value = String.format(Locale.getDefault(), "%,d", lifetimeUnits),
      modifier = Modifier.weight(1f),
    )
    TotalCard(
      label = stringResource(R.string.play_and_earn_progress_play_time_label),
      value = playTimeText(lifetimePlaySeconds),
      modifier = Modifier.weight(1f),
    )
  }
}

@Composable
private fun TotalCard(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .clip(CardShape)
      .background(Palette.GreyDark)
      .padding(12.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Text(
      text = value,
      style = AGTypography.SubHeadingM,
      color = Palette.Primary,
      maxLines = 1,
    )
    Text(
      text = label,
      style = AGTypography.InputsS,
      color = Palette.GreyLight,
    )
  }
}

@Composable
private fun playTimeText(seconds: Long): String {
  val time = playTimeOf(seconds)
  return if (time.hours > 0) {
    stringResource(R.string.play_and_earn_progress_play_time_hours, time.hours, time.minutes)
  } else {
    stringResource(R.string.play_and_earn_progress_play_time_minutes, time.minutes)
  }
}

/** A mission's amount in its own unit: time for play time, a plain number otherwise. */
@Composable
private fun amountText(card: MissionCard, amount: Int): String = when (card.unit) {
  PaEMissionProgressType.SECONDS -> when (val duration = durationOf(amount.toLong())) {
    is Duration.Minutes -> stringResource(
      R.string.play_and_earn_progress_play_time_minutes,
      duration.minutes,
    )

    is Duration.Hours -> stringResource(
      R.string.play_and_earn_progress_duration_hours,
      duration.text,
    )
  }

  PaEMissionProgressType.COUNT,
  PaEMissionProgressType.GXP -> amount.toString()
}

@PreviewDark
@Composable
private fun MyProgressScreenPreview(
  @PreviewParameter(MyProgressUiStateProvider::class) uiState: MyProgressUiState.Idle,
) {
  MyProgressScreen(
    uiState = uiState,
    navigateBack = {},
  )
}
