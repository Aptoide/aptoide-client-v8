package com.aptoide.android.aptoidegames.feature_ad.presentation

import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import com.aptoide.android.aptoidegames.feature_ad.MintegralAd

@Composable
fun MintegralAdWrapper(
  ad: MintegralAd,
  content: @Composable () -> Unit
) {
  AndroidView(
    factory = { context ->
      val container = FrameLayout(context).apply {
        layoutParams = LayoutParams(
          LayoutParams.MATCH_PARENT,
          LayoutParams.WRAP_CONTENT
        )
      }

      val composeView = ComposeView(context).apply {
        setContent(content)
      }
      container.addView(
        composeView,
        FrameLayout.LayoutParams(
          FrameLayout.LayoutParams.MATCH_PARENT,
          FrameLayout.LayoutParams.WRAP_CONTENT
        )
      )
      ad.register(container)

      container
    }
  )
}
