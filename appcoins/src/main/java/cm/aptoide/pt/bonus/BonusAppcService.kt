package cm.aptoide.pt.bonus


public interface BonusAppcService {
  suspend fun getBonusAppc(): BonusAppcModel
}