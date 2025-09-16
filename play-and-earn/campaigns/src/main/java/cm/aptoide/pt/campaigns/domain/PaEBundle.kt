package cm.aptoide.pt.campaigns.domain

data class PaEBundles(
  val keepPlaying: PaEBundle?,
  val trending: PaEBundle?
)

data class PaEBundle(
  val title: String,
  val apps: List<PaEApp>
)
