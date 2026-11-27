package com.aptoide.android.aptoidegames.play_and_earn.presentation.restricted

import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// AND-951: a restricted screen asks again, so a lifted ban lets the user back in where they are.
@OptIn(ExperimentalCoroutinesApi::class)
internal class PaERestrictedRecheckTest {

  private val resumed = MutableStateFlow(true)
  private var asked = 0

  @Test
  fun `A restricted screen in front asks once a minute`() = coScenario { scope ->
    m Given "the restricted screen shown, the app in front"
    scope.recheck()

    m When "it stays on screen for two minutes"
    scope.advanceTimeBy(INTERVAL * 2)
    scope.runCurrent()

    m Then "it asked on showing and once each minute after"
    assertEquals(3, asked)
  }

  @Test
  fun `A restricted screen in the background asks nothing`() = coScenario { scope ->
    m Given "the restricted screen shown, then the app sent to the background"
    scope.recheck()
    resumed.value = false
    scope.runCurrent()

    m When "five minutes pass"
    scope.advanceTimeBy(INTERVAL * 5)
    scope.runCurrent()

    m Then "it asked only when it was shown"
    assertEquals(1, asked)
  }

  @Test
  fun `Coming back to the app asks at once`() = coScenario { scope ->
    m Given "the restricted screen shown, the app in the background for a while"
    scope.recheck()
    resumed.value = false
    scope.advanceTimeBy(INTERVAL / 2)
    scope.runCurrent()

    m When "the user comes back to the app"
    resumed.value = true
    scope.runCurrent()

    m Then "it asks again at once"
    assertEquals(2, asked)
  }

  private fun TestScope.recheck() {
    backgroundScope.launch {
      recheckWhileShown(resumed = resumed, intervalMillis = INTERVAL) { asked++ }
    }
    runCurrent()
  }

  private companion object {
    const val INTERVAL = 60_000L
  }
}
