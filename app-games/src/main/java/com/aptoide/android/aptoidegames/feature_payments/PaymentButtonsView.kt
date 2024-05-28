package com.aptoide.android.aptoidegames.feature_payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random

@PreviewDark
@Composable
private fun PreviewPaymentButtons() {
  val onBuyClickEnabled = Random.nextBoolean()
  AptoideTheme {
    PaymentButtons(
      onBuyClickEnabled = onBuyClickEnabled,
      onBuyClick = { },
      onOtherPaymentMethodsClick = { }
    )
  }
}

@Composable
fun PaymentButtons(
  modifier: Modifier = Modifier,
  onBuyClickEnabled: Boolean = true,
  onBuyClick: () -> Unit,
  onOtherPaymentMethodsClick: () -> Unit,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Bottom
  ) {
    PrimaryButton(
      modifier = Modifier.fillMaxWidth(),
      onClick = onBuyClick,
      title = "Buy", // TODO hardcoded string
      enabled = onBuyClickEnabled
    )
    AppGamesOtherPaymentMethodsButton(
      onOtherPaymentMethodsClick = onOtherPaymentMethodsClick
    )
  }
}

@Composable
fun AppGamesOtherPaymentMethodsButton(
  modifier: Modifier = Modifier,
  onOtherPaymentMethodsClick: () -> Unit,
) {
  TextButton(
    onClick = onOtherPaymentMethodsClick,
    modifier = modifier
      .fillMaxWidth()
      .height(48.dp)
  ) {
    Text(
      text = "Other payment methods", // TODO hardcoded string
      style = AGTypography.InputsM,
      color = Palette.Black,
      textDecoration = TextDecoration.Underline
    )
  }
}
