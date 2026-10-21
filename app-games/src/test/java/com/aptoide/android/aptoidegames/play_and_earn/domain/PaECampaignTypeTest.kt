package com.aptoide.android.aptoidegames.play_and_earn.domain

import cm.aptoide.pt.campaigns.domain.PaECampaignType
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.campaigns.domain.campaignType
import cm.aptoide.pt.test.gherkin.scenario
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

// AND-915: a campaign is play-time or event, never both. The backend's `type` wins; without it
// (cache, older backend) the mission types decide the way the backend does.
internal class PaECampaignTypeTest {

  private fun mission(
    type: PaEMissionType,
    progressType: PaEMissionProgressType? = PaEMissionProgressType.COUNT,
  ) = PaEMission(
    title = "m-$type-$progressType",
    description = null,
    icon = null,
    type = type,
    arguments = JsonObject(),
    units = 1,
    progress = progressType?.let {
      PaEMissionProgress(current = 0, target = 10, type = it, status = PaEMissionStatus.PENDING)
    },
  )

  @Test
  fun `The backend's type wins over the mission types`() = scenario {
    m Given "event missions in a campaign the backend calls play-time, and the reverse"
    val playTime = PaEMissions(
      checkpoints = emptyList(),
      missions = listOf(mission(PaEMissionType.EVENT)),
      type = PaECampaignType.PLAY_TIME,
    )
    val event = PaEMissions(
      checkpoints = emptyList(),
      missions = listOf(mission(PaEMissionType.PLAY_TIME)),
      type = PaECampaignType.EVENT,
    )

    m When "the campaign type is read"
    val types = listOf(playTime.campaignType(), event.campaignType())

    m Then "it is the backend's"
    assertEquals(listOf(PaECampaignType.PLAY_TIME, PaECampaignType.EVENT), types)
  }

  @Test
  fun `Without the backend's type a time-based mission makes it play-time`() = scenario {
    m Given "cached missions: a play-time one among events, and a seconds checkpoint alone"
    val withPlayTime = PaEMissions(
      checkpoints = emptyList(),
      missions = listOf(mission(PaEMissionType.EVENT), mission(PaEMissionType.PLAY_TIME)),
    )
    val secondsCheckpoint = PaEMissions(
      checkpoints = listOf(mission(PaEMissionType.CHECKPOINT, PaEMissionProgressType.SECONDS)),
      missions = emptyList(),
    )

    m When "the campaign type is read"
    val types = listOf(withPlayTime.campaignType(), secondsCheckpoint.campaignType())

    m Then "both are play-time"
    assertEquals(listOf(PaECampaignType.PLAY_TIME, PaECampaignType.PLAY_TIME), types)
  }

  @Test
  fun `Without the backend's type event and count missions make it an event campaign`() =
    scenario {
      m Given "cached missions with only an event mission and a count checkpoint"
      val missions = PaEMissions(
        checkpoints = listOf(mission(PaEMissionType.CHECKPOINT)),
        missions = listOf(mission(PaEMissionType.EVENT, progressType = null)),
      )

      m When "the campaign type is read"
      val type = missions.campaignType()

      m Then "it is event"
      assertEquals(PaECampaignType.EVENT, type)
    }

  @Test
  fun `Without missions there is no type`() = scenario {
    m Given "no missions and no backend answer"
    val missions = PaEMissions(checkpoints = emptyList(), missions = emptyList())

    m When "the campaign type is read"
    val type = missions.campaignType()

    m Then "it is unknown"
    assertNull(type)
  }

  @Test
  fun `The backend's values map in any case and unknown ones map to none`() = scenario {
    m Given "the backend's values in various cases, an unknown one, blank ones and a missing one"
    val values = listOf("PLAY_TIME", "event", " Event ", "HYBRID", "", "  ", null)

    m When "they are mapped"
    val mapped = values.map(PaECampaignType::fromApi)

    m Then "the two known ones map and the others map to none"
    assertEquals(
      listOf(
        PaECampaignType.PLAY_TIME,
        PaECampaignType.EVENT,
        PaECampaignType.EVENT,
        null,
        null,
        null,
        null,
      ),
      mapped,
    )
  }
}
