package com.aptoide.android.aptoidegames.play_and_earn.presentation.components.app_items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun PaERewardBadge(
  amount: BigDecimal,
  modifier: Modifier = Modifier,
) {
  Text(
    text = stringResource(R.string.play_and_earn_earn_amount_badge, amount.format()),
    style = AGTypography.BodyBold,
    color = Palette.Black,
    modifier = modifier
      .background(Palette.Yellow100)
      .padding(horizontal = 8.dp, vertical = 4.dp),
  )
}

private fun BigDecimal.format(): String =
  "$${setScale(2, RoundingMode.HALF_UP).toPlainString()}"

@Preview
@Composable
private fun PaERewardBadgePreview() {
  PaERewardBadge(amount = BigDecimal("4.50"))
}
