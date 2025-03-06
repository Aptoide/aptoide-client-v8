package com.aptoide.android.aptoidegames.feature_ad

import android.view.View
import cm.aptoide.pt.feature_apps.data.App

data class MintegralAdApp(val app: App, val register: (View) -> Unit)
