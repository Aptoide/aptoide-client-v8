package com.aptoide.android.aptoidegames.feature_flags

import com.aptoide.android.aptoidegames.BuildConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConfigVariantTest {

  @Test
  fun `Each brand reads its own config template`() {
    val expected = when (BuildConfig.FLAVOR_brand) {
      "vanilla" -> "aptoide"
      else -> "aptoide-games"
    }

    assertEquals(expected, CONFIG_VARIANT)
  }
}
