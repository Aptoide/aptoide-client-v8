package cm.aptoide.pt.feature_campaigns.data

import android.content.Context
import android.os.Build
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import java.math.BigInteger
import java.security.MessageDigest
import java.time.Instant
import java.util.*

class CampaignUrlNormalizer(context: Context) {

  private val advertisingId by lazy {
    AdvertisingIdClient(context.applicationContext).let { advertisingIdClient ->
      advertisingIdClient.start()
      val adIdInfo = advertisingIdClient.info
      advertisingIdClient.zza()
      adIdInfo.id
    } ?: UUID.randomUUID().toString()
  }

  val normalize: suspend (String, String) -> String = { url, adListId ->
    val campaignId = url.split("campaignId=")[1].split("&")[0]
    val bidId = calculateBidId(campaignId)
    val impressionId = calculateImpressionId(campaignId)
    val aaid = calculateAAID()
    val sha1Aaid = calculateSHA1(advertisingId)

    val newUrl: String = url.replace("{{BID_ID}}", bidId)
      .replace("{{IMPRESSION_ID}}", impressionId)
      .replace("{{AD_LIST_ID}}", adListId)
      .replace("{{AAID}}", aaid)
      .replace("{{AAID_SHA1}}", sha1Aaid)

    newUrl
  }

  private fun calculateSHA1(aaid: String): String {
    return MessageDigest.getInstance("SHA-1").digest(aaid.toByteArray())
      .joinToString("") { "%02x".format(it) }
  }

  private fun calculateAAID(): String {
    return String(
      android.util.Base64.encode(
        advertisingId.toByteArray(),
        android.util.Base64.DEFAULT
      )
    )
  }

  private fun calculateBidId(campaignId: String): String {
    val bid = "BID"
    val timeStamp: String = getCurrentTimeStamp()
    val attributes: String =
      listOf(campaignId, advertisingId, timeStamp).joinToString(separator = "")
    return listOf(bid, hash(attributes)).joinToString(separator = "")
  }

  private fun calculateImpressionId(campaignId: String): String {
    val imp = "IMP"
    val timeStamp: String = getCurrentTimeStamp()
    val attributes: String =
      listOf(campaignId, advertisingId, timeStamp).joinToString(separator = "")
    return listOf(imp, hash(attributes)).joinToString(separator = "")
  }


  private fun getCurrentTimeStamp(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      Instant.now().toString()
    } else {
      TODO("VERSION.SDK_INT < O")
    }
  }

  private fun hash(result: String): String {
    val md = MessageDigest.getInstance("MD5")
    val md5Hex: String =
      BigInteger(1, md.digest(result.toByteArray())).toString(16).padStart(32, '0')
    return convertHexToDec(md5Hex)
  }

  private fun convertHexToDec(hexString: String): String {
    return BigInteger(hexString, 16).toString()
  }
}