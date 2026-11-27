package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

/**
 * The reward to claim on its own once the user is back from the reward sign-in flow: only when
 * they went there from this offer, are signed in, and the bonus is offered. Right after a sign-in
 * it may still be loading (a sign-in re-offers a refused bonus, AND-953): nothing yet, the surface
 * asks again when the state changes. Null when there is nothing to claim.
 */
internal fun rewardToAutoClaim(
  awaitingSignIn: Boolean,
  signedIn: Boolean,
  rewardState: RewardState,
): PendingPaEReward? =
  if (awaitingSignIn && signedIn) (rewardState as? RewardState.Unclaimed)?.reward else null

/**
 * The reward a tap on an offer claims at once: only for a signed-in user, when the bonus is
 * offered. Null for a signed-out user, who signs in first ([rewardToAutoClaim] claims on the way
 * back), and when there is nothing to claim. A signed-in user meets an offer again after a VPN
 * refusal (AND-964), which keeps the bonus claimable.
 */
internal fun rewardToClaimOnTap(
  signedIn: Boolean,
  rewardState: RewardState,
): PendingPaEReward? = if (signedIn) (rewardState as? RewardState.Unclaimed)?.reward else null
