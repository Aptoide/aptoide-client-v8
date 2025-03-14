package com.aptoide.android.aptoidegames.feature_ad

import android.view.View
import cm.aptoide.pt.feature_apps.data.App

data class MintegralAd(val app: App, val register: (View) -> Unit)

sealed class MintegralAdEvent {
  data class AdClick(val packageName: String) : MintegralAdEvent()
}
