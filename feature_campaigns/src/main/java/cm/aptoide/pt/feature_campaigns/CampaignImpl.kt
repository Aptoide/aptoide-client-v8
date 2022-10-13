package cm.aptoide.pt.feature_campaigns

interface Campaign {
  val id: Long
  val name: String
  val label: String

  suspend fun sendOpenConversionEvent()

  suspend fun sendDownloadEvent()

  suspend fun sendInstallEvent()
}

data class CampaignImpl constructor(
  override val id: Long,
  override val name: String,
  override val label: String,
  private val clicks: List<String>,
  private val downloads: List<String>,
  private val installs: List<String>,
  private val repository: CampaignRepository,
) : Campaign {

  override suspend fun sendOpenConversionEvent() = clicks.forEach { repository.knock(it) }
  override suspend fun sendDownloadEvent() = downloads.forEach { repository.knock(it) }
  override suspend fun sendInstallEvent() = installs.forEach { repository.knock(it) }
}

interface CampaignRepository {
  suspend fun knock(url: String)
}