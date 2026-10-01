package com.aptoide.android.aptoidegames.play_and_earn.domain.mmp

import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.network.repository.AdvertisingIdsRepository
import com.aptoide.android.aptoidegames.play_and_earn.di.PaEMmpClickRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fires the developer's MMP click when a Play & Earn game install starts (AND-877), so the MMP can
 * credit the install to Aptoide and send the postbacks back with the user's wallet.
 */
@Singleton
class PaEMmpClickSender internal constructor(
  private val urlSource: PaEMmpClickUrlSource,
  private val campaignRepository: CampaignRepository,
  private val walletAddress: suspend () -> String?,
  private val advertisingId: () -> String?,
  private val newClickId: () -> String,
  private val scope: CoroutineScope,
) {

  @Inject
  constructor(
    urlSource: PaEMmpClickUrlSource,
    @PaEMmpClickRepository campaignRepository: CampaignRepository,
    walletCoreDataSource: WalletCoreDataSource,
    advertisingIdsRepository: AdvertisingIdsRepository,
  ) : this(
    urlSource = urlSource,
    campaignRepository = campaignRepository,
    walletAddress = walletCoreDataSource::getCurrentWalletAddress,
    advertisingId = { advertisingIdsRepository.advertisingId },
    newClickId = { UUID.randomUUID().toString() },
    // Outlives the screen: the user may leave the app view right after tapping Install.
    scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
  )

  fun send(packageName: String) {
    scope.launch {
      // A click that fails must never crash the install flow.
      try {
        sendNow(packageName)
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Timber.w(e, "PaE MMP click failed for $packageName")
      }
    }
  }

  internal suspend fun sendNow(packageName: String) {
    // Without the wallet the postbacks can't be linked to the user, so the click is useless.
    val wallet = walletAddress()?.takeUnless { it.isBlank() } ?: return
    val template = urlSource.clickTemplate(packageName) ?: return
    val url = PaEMmpClickUrlBuilder.build(
      template = template.url,
      params = PaEMmpClickParams(
        wallet = wallet,
        clickId = newClickId(),
        advertisingId = advertisingId(),
        campaignId = template.campaignId,
        channel = CHANNEL,
      ),
    ) ?: return
    if (BuildConfig.DEBUG) Timber.tag("PaEMmp").i("MMP click for $packageName: $url")
    campaignRepository.knock(url)
  }

  private companion object {
    const val CHANNEL = "aptoide_games"
  }
}
