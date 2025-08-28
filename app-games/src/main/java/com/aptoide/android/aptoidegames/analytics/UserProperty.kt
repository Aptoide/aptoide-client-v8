package com.aptoide.android.aptoidegames.analytics

import androidx.annotation.Size

data class UserProperty(
  @Size(min = 1L, max = 24L) val name: String,
  @Size(min = 1L, max = 36L) val value: Any?
)
