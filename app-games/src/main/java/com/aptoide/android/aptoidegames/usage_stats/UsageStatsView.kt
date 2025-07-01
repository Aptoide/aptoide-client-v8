package com.aptoide.android.aptoidegames.usage_stats

import android.content.Context
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner

class SimpleLifecycleOwner : LifecycleOwner {
  override val lifecycle = LifecycleRegistry(this)

  init {
    lifecycle.currentState = Lifecycle.State.STARTED
  }

  fun destroy() {
    lifecycle.currentState = Lifecycle.State.DESTROYED
  }
}

fun getUsageStatsView(context: Context, timeInForeground: Long): View {
  val view = ComposeView(context).apply {
    setViewTreeLifecycleOwner(SimpleLifecycleOwner())
    setContent {
      UsageStatsView(timeInForeground)
    }
  }
  return view
}

@Composable
fun UsageStatsView(timeInForeground: Long) {
  Box(
    modifier = Modifier
      .wrapContentSize()
      .clip(CircleShape)
      .background(color = Color.LightGray),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Text(text = "Time in foreground: $timeInForeground")
    }
  }
}

@Preview
@Composable
fun UsageStatsViewPreview() {
  UsageStatsView(timeInForeground = 1000)
}
