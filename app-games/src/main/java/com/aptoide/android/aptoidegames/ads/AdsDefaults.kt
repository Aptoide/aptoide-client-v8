package com.aptoide.android.aptoidegames.ads

/** Defaults shared by every MAX ad placement. Remote Config values take precedence. */
object AdsDefaults {

  /**
   * Geos where no mediated ad is requested until a consent flow (CMP) exists:
   * US, Canada, UK and the EU/EEA member states.
   */
  val EXCLUDED_GEOS: Set<String> = setOf(
    "US", "CA", "GB", "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR",
    "DE", "GR", "HU", "IE", "IT", "LV", "LT", "LU", "MT", "NL", "PL", "PT", "RO",
    "SK", "SI", "ES", "SE"
  )
}
