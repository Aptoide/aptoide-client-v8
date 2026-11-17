package cm.aptoide.pt.campaigns.domain

import cm.aptoide.pt.test.gherkin.scenario
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

// Hackathon 2026: a game can again have play-time missions and MMP-confirmed ones (purchases) in
// the same campaign. The backend labels such a campaign MMP-tracked; Play must still measure its
// play time, or the time missions would never move.
internal class PaEPlayTrackingTest {

  private fun mission(type: PaEMissionType) = PaEMission(
    title = type.name,
    description = null,
    icon = null,
    type = type,
    arguments = JsonObject(),
    units = 10,
    progress = null,
  )

  private fun missions(tracking: PaETracking?, vararg types: PaEMissionType) = PaEMissions(
    checkpoints = emptyList(),
    missions = types.map(::mission),
    tracking = tracking,
  )

  @Test
  fun `A mixed campaign is played with play-time tracking`() = scenario {
    m Given "a campaign the backend calls MMP-tracked, with a purchase and a play-time mission"
    val mixed = missions(PaETracking.MMP, PaEMissionType.EVENT, PaEMissionType.PLAY_TIME)

    m When "Play asks how to track it"
    m Then "the app measures play time, while the label itself is unchanged"
    assertEquals(PaETracking.APTOIDE, mixed.playTracking())
    assertEquals(PaETracking.MMP, mixed.tracking())
  }

  @Test
  fun `A campaign of one kind is played as before`() = scenario {
    m Given "an events-only campaign, a play-time-only one, and one with no missions"
    m When "Play asks how to track each"
    m Then "events wait for the MMP, play time is measured, and nothing is asked without missions"
    assertEquals(PaETracking.MMP, missions(PaETracking.MMP, PaEMissionType.EVENT).playTracking())
    assertEquals(
      PaETracking.APTOIDE,
      missions(PaETracking.APTOIDE, PaEMissionType.PLAY_TIME).playTracking(),
    )
    assertNull(missions(null).playTracking())
  }
}
