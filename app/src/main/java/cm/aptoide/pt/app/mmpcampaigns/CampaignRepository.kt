package cm.aptoide.pt.app.mmpcampaigns

interface CampaignRepository {
  suspend fun knock(url: String)
}