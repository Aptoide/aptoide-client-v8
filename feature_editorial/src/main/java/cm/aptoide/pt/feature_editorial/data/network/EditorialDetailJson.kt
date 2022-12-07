package cm.aptoide.pt.feature_editorial.data.network

import androidx.annotation.Keep
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7Response
import cm.aptoide.pt.feature_apps.data.network.model.AppJSON

@Keep
data class EditorialDetailJson(
  var data: Data? = null,
) : BaseV7Response()

@Keep
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

@Keep
data class Appearance(val caption: Caption)

@Keep
data class Caption(val theme: String)

@Keep
data class ContentJSON(
  val title: String?,
  val message: String?,
  val action: String?,
  val media: List<Media>,
  val app: AppJSON?,
)

@Keep
data class Media(
  val type: String,
  val description: String,
  val image: String,
  val url: String,
)
