package com.aptoide.android.aptoidegames.play_and_earn.domain.mmp

import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.network.repository.AdvertisingIdsRepository
import com.aptoide.android.aptoidegames.play_and_earn.di.PaEMmpClickRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fires the developer's MMP click when a Play & Earn game install starts (AND-877), so the MMP can
 * credit the install to Aptoide and send the postbacks back with the user's wallet. The click id
 * is the install's own (AND-954), the one the backend's install click carries too.
 */
@Singleton
class PaEMmpClickSender internal constructor(
  private val urlSource: PaEMmpClickUrlSource,
  private val campaignRepository: CampaignRepository,
  private val walletAddress: suspend () -> String?,
  private val advertisingId: () -> String?,
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
  )

  /** Sends the click of [packageName]'s campaign, when it has a link, carrying [clickId]. */
  suspend fun sendNow(packageName: String, clickId: String) {
    // Without the wallet the postbacks can't be linked to the user, so the click is useless.
    val wallet = walletAddress()?.takeUnless { it.isBlank() } ?: return
    val template = urlSource.clickTemplate(packageName) ?: return
    val url = PaEMmpClickUrlBuilder.build(
      template = template.url,
      params = PaEMmpClickParams(
        wallet = wallet,
        clickId = clickId,
        advertisingId = advertisingId(),
        campaignId = template.campaignId,
        channel = CHANNEL,
      ),
      mmp = template.mmp,
    ) ?: return
    if (BuildConfig.DEBUG) {
      Timber.tag("PaEMmp").i("${template.mmp} click for $packageName: $url")
    }
    campaignRepository.knock(url)
  }

  private companion object {
    const val CHANNEL = "aptoide_games"
  }
}
