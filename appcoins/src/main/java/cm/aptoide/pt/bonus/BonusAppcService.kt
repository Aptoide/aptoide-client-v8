package cm.aptoide.pt.bonus


interface BonusAppcService {
  suspend fun getBonusAppc(): BonusAppcModel
}