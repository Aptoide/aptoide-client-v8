package cm.aptoide.pt.campaigns.domain

import cm.aptoide.pt.campaigns.data.PaECampaignsRepository
import javax.inject.Inject

class GetAvailablePaEPackagesUseCase @Inject constructor(
  private val paeCampaignsRepository: PaECampaignsRepository
) {

  suspend operator fun invoke(): Result<Set<String>> {
    return paeCampaignsRepository.getAvailablePackages()
  }
}
