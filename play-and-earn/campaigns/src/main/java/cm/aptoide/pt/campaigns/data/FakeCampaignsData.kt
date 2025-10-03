package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaEApp
import cm.aptoide.pt.campaigns.domain.PaEBundle
import cm.aptoide.pt.campaigns.domain.PaEBundles
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.campaigns.domain.PaEProgress
import com.google.gson.JsonObject

val paEApp1 = PaEApp(
  packageName = "com.mobile.legends",
  icon = "https://pool.img.aptoide.com/aptoide-games/5436a82e945c2493203579ea65557e54_icon.png",
  graphic = "https://pool.img.aptoide.com/aptoide-games/e734990316919ba2e56c886cd4faefe6_fgraphic.jpg",
  name = "Mobile Legends: Bang Bang",
  uname = "mobile-legends",
  progress = PaEProgress(
    current = 15,
    target = 50,
    type = "Mission",
    status = "Ongoing"
  )
)

val paEApp2 = PaEApp(
  packageName = "com.cmsj.bugsquad.google",
  icon = "https://pool.img.aptoide.com/aptoide-games/c4bb8e62e47a11582bd8a6ff883792f3_icon.png",
  graphic = "https://pool.img.aptoide.com/aptoide-games/b2fd10a571c3891ddee80b9533318bf0_fgraphic.png",
  name = "Bug Brawl",
  uname = "bug-brawl",
  progress = PaEProgress(
    current = 15,
    target = 50,
    type = "Mission",
    status = "Ongoing"
  )
)

val paEApp3 = PaEApp(
  packageName = "com.td.watcherofrealms.cata",
  icon = "https://pool.img.aptoide.com/aptoide-games/e8f3ef0b3a37982b1566eaa8e5cbc9b9_icon.png",
  graphic = "https://pool.img.aptoide.com/aptoide-games/f45fda0b2c95d88267b27f776eedc3f9_fgraphic.jpg",
  name = "Watcher of Realms",
  uname = "watcher-of-realms",
  progress = null
)

val paeCampaigns = PaEBundles(
  keepPlaying = PaEBundle(
    title = "Keep Playing",
    apps = listOf(paEApp1, paEApp2)
  ),
  trending = PaEBundle(
    title = "Everyone's favourites",
    apps = listOf(paEApp1, paEApp2, paEApp3)
  )
)

val paeCheckpoint1 = PaEMission(
  title = "Checkpoint 1",
  description = "This is the first checkpoint.",
  icon = "https://pool.img.aptoide.com/catappult/d42291cca5551c6e9e39d56aa8d420d0_icon.png",
  type = PaEMissionType.CHECKPOINT,
  arguments = JsonObject(),
  units = 50,
  progress = PaEMissionProgress(
    current = 30,
    target = 50,
    type = "Checkpoint",
    status = PaEMissionStatus.ONGOING
  )
)

val paeMission1 = PaEMission(
  title = "Quick Throw",
  description = "Play for 30 seconds",
  icon = "https://pool.img.aptoide.com/catappult/d42291cca5551c6e9e39d56aa8d420d0_icon.png",
  type = PaEMissionType.PLAY_TIME,
  arguments = JsonObject(),
  units = 5,
  progress = PaEMissionProgress(
    current = 0,
    target = 30,
    type = "Mission",
    status = PaEMissionStatus.ONGOING
  )
)

val paeMission2 = PaEMission(
  title = "Dice Grinder",
  description = "Play for 1 minute",
  icon = "https://pool.img.aptoide.com/catappult/d42291cca5551c6e9e39d56aa8d420d0_icon.png",
  type = PaEMissionType.PLAY_TIME,
  arguments = JsonObject(),
  units = 10,
  progress = PaEMissionProgress(
    current = 0,
    target = 60,
    type = "Mission",
    status = PaEMissionStatus.ONGOING
  )
)

val paeMission3 = PaEMission(
  title = "Midnight Gambler",
  description = "Play for 5 minutes during night",
  icon = "https://pool.img.aptoide.com/catappult/d42291cca5551c6e9e39d56aa8d420d0_icon.png",
  type = PaEMissionType.PLAY_TIME,
  arguments = JsonObject(),
  units = 10,
  progress = null
)

val paeMissions = PaEMissions(
  checkpoints = listOf(paeCheckpoint1),
  missions = listOf(paeMission1, paeMission2, paeMission3)
)
