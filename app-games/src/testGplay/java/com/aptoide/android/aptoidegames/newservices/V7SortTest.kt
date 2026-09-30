package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

// Shared code names sorts the v7 way. The new services have four of their own.
internal class V7SortTest {

  @ParameterizedTest(name = "{0} is {1}")
  @CsvSource(
    "trending60d, trending",
    "trending30d, trending",
    "pdownloads, downloads",
    "downloads7d, downloads",
    "added, latest",
    "updated, latest",
    "latest, latest",
    "alpha, alpha",
    "something_else, downloads",
    "trending_downloads, trending",
    "new, downloads",
  )
  fun `A v7 sort maps to the closest sort of the new services`(
    v7Sort: String,
    expected: String,
  ) = scenario {
    m Given "a sort named the v7 way"

    m When "it is mapped"
    val sort = v7Sort.toNewServicesSort()

    m Then "it is the closest one, downloads when there is none"
    assertEquals(expected, sort)
  }
}
