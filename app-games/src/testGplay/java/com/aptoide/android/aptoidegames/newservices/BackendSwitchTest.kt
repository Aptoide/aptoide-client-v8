package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

// Until the migration is complete the Play build has to behave exactly as before, so with the
// switch off the new services must not even be built.
internal class BackendSwitchTest {

  @Test
  fun `With the switch off the v7 implementation is kept`() = scenario {
    m Given "the switch off"
    var built = 0

    m When "the backend is selected"
    val selected = selectBackend(enabled = false, v7 = "v7") { built++; "new services" }

    m Then "it is v7, and the other one was never built"
    assertSame("v7", selected)
    assertEquals(0, built)
  }

  @Test
  fun `With the switch on the new services are used`() = scenario {
    m Given "the switch on"

    m When "the backend is selected"
    val selected = selectBackend(enabled = true, v7 = "v7") { "new services" }

    m Then "it is the new services"
    assertEquals("new services", selected)
  }
}
