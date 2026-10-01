package cm.aptoide.pt.campaigns.domain

/**
 * Whether the developer's MMP credited this user's install to Aptoide, for the game's campaign.
 * Sent by the backend in the missions response, for signed-in users only.
 */
data class PaEAttribution(
  val status: PaEAttributionStatus,
  val reason: String? = null,
)

enum class PaEAttributionStatus {
  NOT_STARTED,
  ACTIVE,
  NOT_ELIGIBLE,
  COMPLETED,
  EXPIRED;

  companion object {
    /** The backend's value (`not_started`, …); null for one this app doesn't know. */
    fun fromApi(value: String?): PaEAttributionStatus? =
      entries.find { it.name.equals(value?.trim(), ignoreCase = true) }
  }
}

/** What the game's Rewards tab shows. */
enum class PaERewardsState {
  MISSIONS,
  PAUSED,
  NOT_ELIGIBLE,
}

/**
 * Missions show normally from the start. Once the game is installed, they wait for the game's
 * tracking tool to confirm the install ([PaERewardsState.PAUSED]): the backend doesn't know about
 * the install until then, so the app works this state out itself. A status the backend doesn't
 * send (guests, older backends) keeps the missions.
 */
fun paeRewardsState(
  attribution: PaEAttribution?,
  isInstalled: Boolean,
): PaERewardsState = when (attribution?.status) {
  PaEAttributionStatus.NOT_ELIGIBLE -> PaERewardsState.NOT_ELIGIBLE
  PaEAttributionStatus.NOT_STARTED ->
    if (isInstalled) PaERewardsState.PAUSED else PaERewardsState.MISSIONS

  PaEAttributionStatus.ACTIVE,
  PaEAttributionStatus.COMPLETED,
  PaEAttributionStatus.EXPIRED,
  null,
    -> PaERewardsState.MISSIONS
}
