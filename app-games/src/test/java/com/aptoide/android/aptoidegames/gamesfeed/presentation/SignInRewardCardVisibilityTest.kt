package com.aptoide.android.aptoidegames.gamesfeed.presentation

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

// APT-238: the sign-in reward card in the Games Feed bundle is a Play & Earn surface, so it only
// shows when Play & Earn is available for the user.
internal class SignInRewardCardVisibilityTest {

  private val rewardPackages = listOf(ROBLOX, FREE_FIRE)

  @Test
  fun `Hidden when Play and Earn is not available, even with a reward game installed`() =
    scenario {
      m Given "Play and Earn hidden and Roblox installed"
      m When "the reward card package is resolved"
      val rewardPackage = signInRewardPackage(
        playAndEarnVisible = false,
        rewardPackages = rewardPackages,
        isInstalled = { it == ROBLOX },
      )

      m Then "there is no reward card"
      assertNull(rewardPackage)
    }

  @Test
  fun `Shown for the installed reward game when Play and Earn is available`() = scenario {
    m Given "Play and Earn visible and Free Fire installed"
    m When "the reward card package is resolved"
    val rewardPackage = signInRewardPackage(
      playAndEarnVisible = true,
      rewardPackages = rewardPackages,
      isInstalled = { it == FREE_FIRE },
    )

    m Then "the card is shown for Free Fire"
    assertEquals(FREE_FIRE, rewardPackage)
  }

  @Test
  fun `Hidden when no reward game is installed`() = scenario {
    m Given "Play and Earn visible but no reward game installed"
    m When "the reward card package is resolved"
    val rewardPackage = signInRewardPackage(
      playAndEarnVisible = true,
      rewardPackages = rewardPackages,
      isInstalled = { false },
    )

    m Then "there is no reward card"
    assertNull(rewardPackage)
  }

  @Test
  fun `The first reward game by priority wins when several are installed`() = scenario {
    m Given "Play and Earn visible and every reward game installed"
    m When "the reward card package is resolved"
    val rewardPackage = signInRewardPackage(
      playAndEarnVisible = true,
      rewardPackages = rewardPackages,
      isInstalled = { true },
    )

    m Then "the card is shown for the first package in the priority list"
    assertEquals(ROBLOX, rewardPackage)
  }

  private companion object {
    const val ROBLOX = "com.roblox.client"
    const val FREE_FIRE = "com.dts.freefireth"
  }
}
