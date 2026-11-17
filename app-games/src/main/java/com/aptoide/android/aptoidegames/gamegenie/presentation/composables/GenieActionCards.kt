package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

// HACKATHON: not localized. Cards for the Genie purchase flow (gamegenie/purchase).

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.design_system.PrimaryTextButton
import com.aptoide.android.aptoidegames.gamegenie.purchase.GenieAction
import com.aptoide.android.aptoidegames.gamegenie.purchase.PayState
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.FixedColors
import com.aptoide.android.aptoidegames.theme.Palette

/** What the purchase cards can ask the Genie screen to do. */
class GenieActionHandler(
  val onSignIn: () -> Unit,
  val onContinueAsDemoUser: () -> Unit,
  val onPay: (GenieAction.PurchaseReady) -> Unit,
)

val LocalGenieActionHandler = staticCompositionLocalOf<GenieActionHandler?> { null }

@Composable
fun GenieActionCard(
  action: GenieAction,
  modifier: Modifier = Modifier,
) {
  val handler = LocalGenieActionHandler.current
  when (action) {
    GenieAction.Note -> Unit
    is GenieAction.LoginRequired -> GenieLoginCard(
      action = action,
      onSignIn = { handler?.onSignIn?.invoke() },
      onContinueAsDemoUser = { handler?.onContinueAsDemoUser?.invoke() },
      modifier = modifier,
    )

    is GenieAction.Readiness -> GenieReadinessCard(action = action, modifier = modifier)
    is GenieAction.PurchaseReady -> GeniePurchaseCard(
      action = action,
      onPay = { handler?.onPay?.invoke(action) },
      modifier = modifier,
    )

    is GenieAction.PurchaseResult -> GeniePurchaseResultCard(action = action, modifier = modifier)
  }
}

@Composable
private fun GenieCardContainer(
  modifier: Modifier = Modifier,
  highlighted: Boolean = false,
  content: @Composable () -> Unit,
) {
  Column(
    modifier = modifier
      .padding(top = 12.dp)
      .fillMaxWidth()
      .background(Palette.Black)
      .border(
        width = if (highlighted) 1.dp else 0.5.dp,
        color = if (highlighted) Palette.Primary else Palette.Primary.copy(alpha = 0.4f)
      )
      .animateContentSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    content()
  }
}

@Composable
private fun GenieLoginCard(
  action: GenieAction.LoginRequired,
  onSignIn: () -> Unit,
  onContinueAsDemoUser: () -> Unit,
  modifier: Modifier = Modifier,
) {
  GenieCardContainer(modifier = modifier, highlighted = !action.completed) {
    Text(
      text = "Sign in to your Aptoide wallet",
      style = AGTypography.InputsL,
      color = Palette.White,
    )
    if (action.completed) {
      CheckRow(label = "Signed in", passed = true)
    } else {
      Text(
        text = "Your wallet pays for ${action.appName} purchases. It takes one tap.",
        style = AGTypography.Chat,
        color = Palette.GreyLight,
      )
      Spacer(modifier = Modifier.height(4.dp))
      PrimaryButton(
        onClick = onSignIn,
        modifier = Modifier.fillMaxWidth(),
        title = "Sign in",
      )
      if (BuildConfig.DEBUG) {
        // HACKATHON: debug-only demo path, no Google sign-in and nothing written to the wallet.
        PrimaryTextButton(
          onClick = onContinueAsDemoUser,
          text = "Continue as demo user",
          color = Palette.GreyLight,
        )
      }
    }
  }
}

@Composable
private fun GenieReadinessCard(
  action: GenieAction.Readiness,
  modifier: Modifier = Modifier,
) {
  GenieCardContainer(modifier = modifier) {
    Text(
      text = "Getting ${action.appName} ready",
      style = AGTypography.InputsM,
      color = Palette.White,
    )
    CheckRow(label = "Installed on this device", passed = action.installed)
    CheckRow(label = "Aptoide build", passed = action.aptoideBuild)
    CheckRow(label = "Up to date", passed = action.upToDate)
  }
}

@Composable
private fun CheckRow(
  label: String,
  passed: Boolean?,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.height(24.dp),
  ) {
    Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
      AnimatedContent(
        targetState = passed,
        transitionSpec = { (fadeIn() + scaleIn()) togetherWith fadeOut() },
        label = "genieCheck",
      ) { state ->
        when (state) {
          null -> CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = Palette.Primary,
            strokeWidth = 2.dp,
          )

          true -> Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = Palette.Primary,
            modifier = Modifier.size(20.dp),
          )

          false -> Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = null,
            tint = Palette.Error,
            modifier = Modifier.size(20.dp),
          )
        }
      }
    }
    Spacer(modifier = Modifier.width(12.dp))
    Text(
      text = label,
      style = AGTypography.Chat,
      color = when (passed) {
        null -> Palette.GreyLight
        true -> Palette.White
        false -> Palette.Error
      },
    )
  }
}

@Composable
private fun GeniePurchaseCard(
  action: GenieAction.PurchaseReady,
  onPay: () -> Unit,
  modifier: Modifier = Modifier,
) {
  GenieCardContainer(modifier = modifier, highlighted = true) {
    Text(
      text = action.appName.uppercase(),
      style = AGTypography.InputsXS,
      color = Palette.Primary,
    )
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
        text = action.title,
        style = AGTypography.Title,
        color = Palette.White,
        modifier = Modifier.weight(1f),
      )
      Text(
        text = action.price,
        style = AGTypography.Title,
        color = Palette.Primary,
      )
    }
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(0.5.dp)
        .background(Palette.Primary.copy(alpha = 0.4f))
    )
    LabelValueRow(label = "Paying with", value = action.walletLabel)
    action.balanceLabel?.let { LabelValueRow(label = "Balance", value = it) }
    Spacer(modifier = Modifier.height(4.dp))
    when (action.state) {
      PayState.IDLE -> PrimaryButton(
        onClick = onPay,
        modifier = Modifier.fillMaxWidth(),
        title = "Pay ${action.price}",
      )

      PayState.WAITING_FOR_GAME -> PrimaryButton(
        onClick = {},
        enabled = false,
        modifier = Modifier.fillMaxWidth(),
        title = "Finishing in ${action.appName}…",
      )

      PayState.PAID -> Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .background(Palette.Primary.copy(alpha = 0.1f))
          .border(0.5.dp, Palette.Primary),
      ) {
        Icon(
          imageVector = Icons.Filled.Check,
          contentDescription = null,
          tint = Palette.Primary,
          modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Paid", style = AGTypography.InputsL, color = Palette.Primary)
      }
    }
  }
}

@Composable
private fun LabelValueRow(
  label: String,
  value: String,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Text(
      text = label,
      style = AGTypography.Chat,
      color = Palette.GreyLight,
      modifier = Modifier.weight(1f),
    )
    Text(
      text = value,
      style = AGTypography.ChatBold,
      color = Palette.White,
      maxLines = 1,
    )
  }
}

@Composable
private fun GeniePurchaseResultCard(
  action: GenieAction.PurchaseResult,
  modifier: Modifier = Modifier,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .padding(top = 12.dp)
      .fillMaxWidth()
      .background(Palette.Primary)
      .padding(horizontal = 16.dp, vertical = 12.dp),
  ) {
    Icon(
      imageVector = Icons.Filled.Check,
      contentDescription = null,
      tint = FixedColors.Dark,
      modifier = Modifier.size(24.dp),
    )
    Spacer(modifier = Modifier.width(12.dp))
    Text(
      text = "Purchase complete",
      style = AGTypography.InputsL,
      color = FixedColors.Dark,
    )
  }
}
