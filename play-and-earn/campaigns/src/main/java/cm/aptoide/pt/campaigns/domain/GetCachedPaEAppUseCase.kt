package cm.aptoide.pt.campaigns.domain

import cm.aptoide.pt.campaigns.data.PaECampaignsRepository
import javax.inject.Inject

class GetCachedPaEAppUseCase @Inject constructor(
  private val paeCampaignsRepository: PaECampaignsRepository
) {

  suspend operator fun invoke(packageName: String): Result<PaEApp?> =
    paeCampaignsRepository.getCachedApp(packageName)
}
