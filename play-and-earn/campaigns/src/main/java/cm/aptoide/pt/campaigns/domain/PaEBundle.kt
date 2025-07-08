package cm.aptoide.pt.campaigns.domain

import cm.aptoide.pt.extensions.getRandomString
import kotlin.random.Random
import kotlin.random.nextInt

data class PaEBundles(
  val keepPlaying: PaEBundle?,
  val trending: PaEBundle?
)

data class PaEBundle(
  val title: String,
  val apps: List<PaEApp>
)

val randomPaEBundle
  get() = PaEBundle(
    title = getRandomString(range = 2..5, capitalize = true),
    apps = List(Random.nextInt(2..10)) { randomPaEApp }
  )

val randomPaEBundles
  get() = PaEBundles(
    keepPlaying = randomPaEBundle,
    trending = randomPaEBundle
  )
