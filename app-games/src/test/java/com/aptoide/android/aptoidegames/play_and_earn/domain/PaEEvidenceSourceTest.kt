package com.aptoide.android.aptoidegames.play_and_earn.domain

import cm.aptoide.pt.campaigns.domain.PaEEvidenceSource
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

// AND-900: who measures a mission, as the backend names it.
internal class PaEEvidenceSourceTest {

  @Test
  fun `The three backend values map in any case and with surrounding spaces`() = scenario {
    m Given "the backend's values in various cases"
    val values = listOf("aptoide_usage", "MMP", " sdk ")

    m When "they are mapped"
    val mapped = values.map(PaEEvidenceSource::fromApi)

    m Then "each one maps to its source"
    assertEquals(
      listOf(PaEEvidenceSource.APTOIDE_USAGE, PaEEvidenceSource.MMP, PaEEvidenceSource.SDK),
      mapped,
    )
  }

  @Test
  fun `Unknown, blank and missing values map to none`() = scenario {
    m Given "a value this app doesn't know, blank ones and a missing one"
    val values = listOf("blockchain", "", "   ", null)

    m When "they are mapped"
    val mapped = values.map(PaEEvidenceSource::fromApi)

    m Then "none of them maps"
    mapped.forEach { assertNull(it) }
  }
}
