package com.aptoide.android.aptoidegames.feature_apps.presentation

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cm.aptoide.pt.extensions.getAppIconDrawable
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.R

@Composable
fun rememberAppIconDrawable(
  packageName: String,
  context: Context,
): Drawable? =
  runPreviewable(preview = {
    remember(
      key1 = packageName,
      key2 = context
    ) {
      AppCompatResources.getDrawable(context, R.mipmap.ic_launcher)
    }
  }, real = {
    remember(
      key1 = packageName,
      key2 = context
    ) {
      context.getAppIconDrawable(packageName)
    }
  })
