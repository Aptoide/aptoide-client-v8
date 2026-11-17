package com.aptoide.android.aptoidegames.play_and_earn.presentation.overlays

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.aptoide.pt.campaigns.data.paeMission1
import cm.aptoide.pt.campaigns.data.paeMission2
import cm.aptoide.pt.campaigns.domain.PaEMission
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getSmallCoinIcon
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.FixedColors
import com.aptoide.android.aptoidegames.theme.Palette
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.random.Random

/** Where the Claim of a completed mission stands; driven by [PaEOverlayViewManager]. */
enum class MissionClaimPhase {
  /** The Claim button is waiting for a tap (or for the automatic claim). */
  WAITING,

  /** Claimed: the coins fly from the button to the balance. */
  CLAIMED,

  /** The coins landed: the balance counts up. */
  COUNTING,
}

/**
 * What the two windows of the mission-completed overlay share: the banner (touchable, it holds
 * the Claim button) and the effects layer (never touchable: the balance chip and the flying
 * coins, which cross the screen over the game).
 */
@Stable
class MissionCompletedOverlayState(
  val mission: PaEMission,
  val nextMission: PaEMission?,
) {
  var visible by mutableStateOf(false)
  var phase by mutableStateOf(MissionClaimPhase.WAITING)

  /** The wallet's units once the mission was rewarded; null until read, or when unreadable. */
  var balanceAfter by mutableStateOf<Long?>(null)

  /** Centre of the Claim button on screen, in px: where the coins leave from. */
  internal var claimButtonCenter by mutableStateOf<Offset?>(null)

  private val claimTap = CompletableDeferred<Unit>()

  fun claim() {
    claimTap.complete(Unit)
  }

  suspend fun awaitClaimTap() = claimTap.await()
}

private val slideEasing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
private val coinEasing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
private val mutedText = FixedColors.White.copy(alpha = 0.65f)

@Composable
private fun SlideFromTop(
  visible: Boolean,
  content: @Composable () -> Unit
) = AnimatedVisibility(
  visible = visible,
  enter = slideInVertically(tween(SLIDE_MILLIS, easing = slideEasing)) { -it } +
    fadeIn(tween(SLIDE_MILLIS)),
  exit = slideOutVertically(tween(SLIDE_MILLIS, easing = slideEasing)) { -it } +
    fadeOut(tween(SLIDE_MILLIS)),
) {
  content()
}

private fun LayoutCoordinates.centerOnScreen(): Offset =
  positionOnScreen() + Offset(size.width / 2f, size.height / 2f)

/**
 * The mission-completed banner drawn over the game while it is being played, for players who
 * allowed the app to draw over other apps; the others get the notification AND-880 moved this
 * to (see PaEMissionCompletedAnnouncer). Hackathon 2026 (T5): the designer's banner, with a
 * Claim button and the game's next mission. The Claim is cosmetic: the server credited the
 * reward already, the tap only plays the coins. A window over a game looks the same in every
 * theme, hence the fixed colours.
 */
@Composable
fun MissionCompletedOverlayView(state: MissionCompletedOverlayState) {
  SlideFromTop(visible = state.visible) {
    MissionCompletedBanner(
      mission = state.mission,
      nextMission = state.nextMission,
      claimed = state.phase != MissionClaimPhase.WAITING,
      onClaim = state::claim,
      onClaimButtonPositioned = { state.claimButtonCenter = it.centerOnScreen() }
    )
  }
}

@Composable
private fun MissionCompletedBanner(
  mission: PaEMission,
  nextMission: PaEMission?,
  claimed: Boolean,
  onClaim: () -> Unit,
  onClaimButtonPositioned: (LayoutCoordinates) -> Unit = {}
) {
  val shape = RoundedCornerShape(16.dp)
  Column(
    modifier = Modifier
      .padding(horizontal = 10.dp)
      .widthIn(max = 360.dp)
      .fillMaxWidth()
      .clip(shape)
      .background(FixedColors.Dark.copy(alpha = 0.82f))
      .border(width = 1.dp, color = FixedColors.White.copy(alpha = 0.08f), shape = shape)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Image(
        modifier = Modifier.size(36.dp),
        imageVector = getSmallCoinIcon(),
        contentDescription = null
      )
      Text(
        modifier = Modifier.weight(1f),
        text = mission.title,
        style = AGTypography.InputsL.copy(fontSize = 15.sp),
        color = FixedColors.White,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )
      Text(
        modifier = Modifier
          .onGloballyPositioned(onClaimButtonPositioned)
          .clip(RoundedCornerShape(10.dp))
          .background(Palette.Primary.copy(alpha = if (claimed) 0.6f else 1f))
          .clickable(enabled = !claimed, onClick = onClaim)
          .padding(horizontal = 16.dp, vertical = 8.dp),
        text = if (claimed) {
          stringResource(R.string.play_and_earn_mission_completed_claimed_button)
        } else {
          stringResource(R.string.play_and_earn_mission_completed_claim_button, mission.units)
        },
        style = AGTypography.InputsS.copy(fontSize = 13.sp),
        color = FixedColors.Dark,
        maxLines = 1
      )
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(FixedColors.White.copy(alpha = 0.1f))
    )

    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      if (nextMission != null) {
        Text(
          text = stringResource(R.string.play_and_earn_mission_completed_next_title),
          style = AGTypography.SmallGames,
          color = mutedText,
          maxLines = 1
        )
        Text(
          modifier = Modifier.weight(1f),
          text = nextMission.title,
          style = AGTypography.SmallGames.copy(fontWeight = FontWeight.Medium),
          color = FixedColors.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = stringResource(
            R.string.play_and_earn_mission_completed_next_reward,
            nextMission.units
          ),
          style = AGTypography.SmallGames.copy(fontWeight = FontWeight.Medium),
          color = Palette.Primary,
          maxLines = 1
        )
      } else {
        Text(
          modifier = Modifier.weight(1f),
          text = stringResource(R.string.play_and_earn_mission_completed_all_done),
          style = AGTypography.SmallGames,
          color = mutedText,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

/**
 * The layer the banner's effects play on: the units balance in the top end corner, shown only
 * with the banner, and the coins that fly to it from the Claim button once claimed. It spans the
 * screen's width over the game, so its window never takes a touch. [topInset] puts the balance
 * level with the banner.
 */
@Composable
fun MissionCompletedEffectsView(
  state: MissionCompletedOverlayState,
  topInset: Int
) {
  var origin by remember { mutableStateOf(Offset.Zero) }
  var balanceCoinCenter by remember { mutableStateOf<Offset?>(null) }
  val count = claimBalanceCount(state.balanceAfter, state.mission.units)
  val countProgress = remember { Animatable(0f) }

  LaunchedEffect(state.phase) {
    if (state.phase == MissionClaimPhase.COUNTING) {
      countProgress.animateTo(1f, tween(BALANCE_COUNT_MILLIS, easing = LinearEasing))
    }
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(with(LocalDensity.current) { topInset.toDp() } + EFFECTS_HEIGHT)
      .onGloballyPositioned { origin = it.positionOnScreen() }
  ) {
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(top = with(LocalDensity.current) { topInset.toDp() }, end = 10.dp)
    ) {
      SlideFromTop(visible = state.visible) {
        UnitsBalanceChip(
          text = count.at(countProgress.value).let {
            val amount = String.format(Locale.getDefault(), "%,d", it)
            if (count.isWalletBalance) amount else "+$amount"
          },
          onCoinPositioned = { balanceCoinCenter = it.centerOnScreen() }
        )
      }
    }

    val from = state.claimButtonCenter
    val to = balanceCoinCenter
    if (state.phase != MissionClaimPhase.WAITING && from != null && to != null) {
      repeat(CLAIM_COINS) { index ->
        FlyingCoin(index = index, from = from - origin, to = to - origin)
      }
    }
  }
}

@Composable
private fun UnitsBalanceChip(
  text: String,
  onCoinPositioned: (LayoutCoordinates) -> Unit = {}
) {
  Row(
    modifier = Modifier
      .clip(CircleShape)
      .background(FixedColors.Scrim.copy(alpha = 0.35f))
      .padding(start = 6.dp, top = 4.dp, end = 10.dp, bottom = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Image(
      modifier = Modifier
        .size(18.dp)
        .onGloballyPositioned(onCoinPositioned),
      imageVector = getSmallCoinIcon(),
      contentDescription = null
    )
    Text(
      text = text,
      style = AGTypography.InputsS.copy(fontSize = 13.sp),
      color = FixedColors.White,
      maxLines = 1
    )
  }
}

/** One coin, from the Claim button at [from] to the balance at [to] (px, inside the layer). */
@Composable
private fun FlyingCoin(
  index: Int,
  from: Offset,
  to: Offset
) {
  val plan = remember { coinFlightPlan(index, CLAIM_COINS, Random.Default) }
  val progress = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    delay(plan.delayMillis.toLong())
    progress.animateTo(1f, tween(plan.durationMillis, easing = LinearEasing))
  }

  val density = LocalDensity.current.density
  val half = COIN_SIZE.value * density / 2f
  Image(
    modifier = Modifier
      .size(COIN_SIZE)
      .graphicsLayer {
        // The plan is in dp, the positions in px.
        val frame = coinFrame(
          progress = progress.value,
          plan = plan,
          targetX = (to.x - from.x) / density,
          targetY = (to.y - from.y) / density,
          easing = coinEasing::transform
        )
        translationX = from.x - half + frame.x * density
        translationY = from.y - half + frame.y * density
        scaleX = frame.scale
        scaleY = frame.scale
        alpha = frame.alpha
      },
    imageVector = getSmallCoinIcon(),
    contentDescription = null
  )
}

private val COIN_SIZE = 20.dp

// Room under the banner for the coins to spread before they fly up to the balance.
private val EFFECTS_HEIGHT = 200.dp

@Preview
@Composable
private fun MissionCompletedBannerPreview() {
  MissionCompletedBanner(paeMission1, paeMission2, claimed = false, onClaim = {})
}

@Preview
@Composable
private fun MissionCompletedBannerLastMissionPreview() {
  MissionCompletedBanner(paeMission1, nextMission = null, claimed = true, onClaim = {})
}

@Preview
@Composable
private fun UnitsBalanceChipPreview() {
  UnitsBalanceChip(text = "1,340")
}
