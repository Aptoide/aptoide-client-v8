package cm.aptoide.pt.feature_editorial.data.network

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7Response
import cm.aptoide.pt.aptoide_network.data.network.model.Stats


data class EditorialDetailJson(
  var data: Data? = null,
) : BaseV7Response()

data class Data(
  val id: String,
  val type: String,
  val subtype: String,
  val flair: String?,
  val title: String,
  val slug: String,
  val caption: String,
  val background: String,
  val views: Long,
  val appearance: Appearance,
  val date: String,
  val content: List<ContentJSON>,
)

data class Appearance(val caption: Caption)
data class Caption(val theme: String)

data class ContentJSON(
  val title: String?,
  val message: String?,
  val action: String?,
  val media: List<Media>,
  val app: App,
)

data class Media(
  val type: String,
  val description: String,
  val image: String,
  val url: String,
)

data class App(
  val icon: String,
  val name: String,
  val stats: Stats,
)

