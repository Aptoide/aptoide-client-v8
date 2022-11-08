package cm.aptoide.pt.feature_apps.data.network.model

import androidx.annotation.Keep

@Keep
internal class WidgetsJSON {
  @Keep
  data class WidgetNetwork(
    var type: WidgetTypeJSON? = null,
    var title: String? = null, // Highlighted, Games, Categories, Timeline, Recommended for you,
    var tag: String? = null, // Aptoide Publishers
    var view: String? = null,
    var actions: List<ActionJSON>? = null,
    var data: DataJSON? = null
  )

  @Keep
  data class DataJSON(
    var layout: Layout? = null,
    var icon: String? = null,
    var message: String? = null,
    var groupId: Long? = null //only for eskills widget
  )

  @Keep
  data class ActionJSON(
    var type: String? = null, // button
    var label: String? = null,
    var tag: String? = null,
    var event: EventJSON? = null
  )

  @Keep
  data class EventJSON(
    var type: WidgetActionEventType? = null,
    var name: WidgetActionEventName? = null,
    var action: String? = null,
    var data: DataJSON? = null
  )
}