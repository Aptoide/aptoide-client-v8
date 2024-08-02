package cm.aptoide.pt.bonus

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.format
import cm.aptoide.pt.theme.AppTheme
import cm.aptoide.pt.theme.appCoinsButtonGradient

@Preview(name = "Feature Graphic Item")
@Composable
fun BonusBanner(bonus: Float? = 8.5f) {
  bonus?.let {
    Box(
      modifier = Modifier.size(64.dp)
        .clip(
          shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 16.dp,
            bottomEnd = 16.dp,
            bottomStart = 0.dp
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      Image(
        imageVector = AppTheme.icons.BonusBackground,
        contentDescription = "BonusBackground",
        modifier = Modifier.fillMaxSize()
      )
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-6).dp)
      ) {
        Text(
          text = "up to",
          modifier = Modifier
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache(onBuildDrawCache),
          style = AppTheme.typography.bold_XXS
        )
        Row(
          modifier = Modifier.padding(start = 5.dp, bottom = 2.dp)
        ) {
          Text(
            text = it.format(1),
            modifier = Modifier
              .alignByBaseline()
              .graphicsLayer(alpha = 0.99f)
              .drawWithCache(onBuildDrawCache),
            style = AppTheme.typography.bold_XL,
          )
          Text(
            text = "%",
            modifier = Modifier
              .alignByBaseline()
              .graphicsLayer(alpha = 0.99f)
              .drawWithCache(onBuildDrawCache),
            style = AppTheme.typography.bold_XS,
          )
        }
        Text(
          text = "BONUS",
          modifier = Modifier
            .padding(start = 5.dp)
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache(onBuildDrawCache),
          style = AppTheme.typography.bold_XXS,
        )
      }
    }
  }
}

val onBuildDrawCache: CacheDrawScope.() -> DrawResult = {
  onDrawWithContent {
    drawContent()
    drawRect(
      brush = appCoinsButtonGradient,
      blendMode = BlendMode.SrcAtop
    )
  }
}

