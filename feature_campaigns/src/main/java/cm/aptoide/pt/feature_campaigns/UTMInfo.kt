package cm.aptoide.pt.feature_campaigns

import android.net.Uri.encode
import java.nio.charset.StandardCharsets

/**
 * Represents UTM tracking parameters for MMP campaign attribution.
 * All fields are nullable to allow partial definitions that can be merged or overridden.
 */
data class UTMInfo(
  val utmSource: String? = "aptoide",
  val utmMedium: String? = null,
  val utmCampaign: String? = null,
  val utmContent: String? = null,
  val utmTerm: String? = null
) {
  /**
   * Serializes UTMInfo to a URL-safe string for navigation.
   * Uses ~ as separator, empty string for null values.
   */
  override fun toString(): String = encode(
    "${utmSource.orEmpty()}~${utmMedium.orEmpty()}~${utmCampaign.orEmpty()}~${utmContent.orEmpty()}~${utmTerm.orEmpty()}",
    StandardCharsets.UTF_8.toString()
  )

  companion object {
    /**
     * Deserializes UTMInfo from a navigation parameter string.
     * Returns null if the string is invalid or all values are empty.
     */
    fun fromString(source: String): UTMInfo? {
      val parts = source.split("~")
      if (parts.size < 5) return null

      val utmInfo = UTMInfo(
        utmSource = parts[0].takeIf { it.isNotEmpty() },
        utmMedium = parts[1].takeIf { it.isNotEmpty() },
        utmCampaign = parts[2].takeIf { it.isNotEmpty() },
        utmContent = parts[3].takeIf { it.isNotEmpty() },
        utmTerm = parts[4].takeIf { it.isNotEmpty() }
      )

      // Return null if all values are null (empty UTMInfo)
      return utmInfo.takeIf {
        it.utmSource != null || it.utmMedium != null || it.utmCampaign != null ||
          it.utmContent != null || it.utmTerm != null
      }
    }
  }
}
