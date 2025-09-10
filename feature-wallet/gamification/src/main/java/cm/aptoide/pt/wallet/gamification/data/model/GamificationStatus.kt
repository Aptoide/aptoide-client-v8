package cm.aptoide.pt.wallet.gamification.data.model

import androidx.annotation.Keep

@Keep
internal enum class GamificationStatus {
  NONE,
  STANDARD,
  APPROACHING_NEXT_LEVEL,
  APPROACHING_VIP,
  VIP,
  APPROACHING_VIP_MAX,
  VIP_MAX;
}
