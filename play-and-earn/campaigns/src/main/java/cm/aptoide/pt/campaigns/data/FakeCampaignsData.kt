package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaEBundle
import cm.aptoide.pt.campaigns.domain.PaEBundles
import cm.aptoide.pt.campaigns.domain.PaEProgress
import cm.aptoide.pt.campaigns.domain.PaEApp

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
  packageName = "com.appcoins.wallet",
  icon = "https://pool.img.aptoide.com/catappult/d42291cca5551c6e9e39d56aa8d420d0_icon.png",
  graphic = "https://pool.img.aptoide.com/catappult/277096cced71e00af898784187110a7b_fgraphic.png",
  name = "Aptoide Wallet",
  uname = "appcoins-wallet",
  progress = PaEProgress(
    current = 15,
    target = 50,
    type = "Mission",
    status = "Ongoing"
  )
)

val paEApp3 = PaEApp(
  packageName = "com.aptoide.diceroll.sdk.dev",
  icon = "https://cdn6.aptoide.com/imgs/d/7/a/d7a1e9676547714cbaa366997183e136_icon.png",
  graphic = "https://cdn6.aptoide.com/imgs/c/e/f/ceffc555296046d89ca0c3e69b4c9439_fgraphic.jpg",
  name = "Aptoide Diceroll SDK Dev",
  uname = "aptoide-diceroll-sdk-dev",
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