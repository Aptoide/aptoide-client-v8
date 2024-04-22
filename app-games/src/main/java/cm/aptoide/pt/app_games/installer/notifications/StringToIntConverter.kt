package cm.aptoide.pt.app_games.installer.notifications

import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class StringToIntConverter @Inject constructor() {
  private val uniqueIdentifier = "InstallerNotification"

  fun getStringId(inputString: String): Int {
    val combinedString = "$uniqueIdentifier$inputString"
    return abs(calculateHash(combinedString))
  }

  private fun calculateHash(inputString: String): Int {
    val bytes = MessageDigest.getInstance("SHA-256").digest(inputString.toByteArray())
    return bytes.fold(0) { acc, byte -> (acc shl 8) or (byte.toInt() and 0xFF) }
  }
}
