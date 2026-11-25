package com.aptoide.android.aptoidegames.feature_flags

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class ConfigBucketTest {

  @ParameterizedTest(name = "{0} with salt \"{1}\" is bucket {2}")
  @MethodSource("pinnedVectors")
  fun `The bucket matches the vectors pinned by the server`(
    bucketId: String,
    salt: String,
    expected: Int,
  ) {
    assertEquals(expected, ConfigBucket.bucketOf(bucketId = bucketId, salt = salt))
  }

  companion object {
    // From APT-182, mirroring test_clientconfig_resolve.py in the app-store repo
    @JvmStatic
    fun pinnedVectors() = listOf(
      Arguments.of("550e8400-e29b-41d4-a716-446655440000", "exp-2026-q4", 59),
      Arguments.of("550e8400-e29b-41d4-a716-446655440000", "other-salt", 45),
      Arguments.of("123e4567-e89b-12d3-a456-426614174000", "exp-2026-q4", 84),
      Arguments.of("abc", "", 57),
      Arguments.of("café-install-λ", "exposé-2026", 63),
      Arguments.of("550e8400-e29b-41d4-a716-446655440000", "exposé", 66),
    )
  }
}
