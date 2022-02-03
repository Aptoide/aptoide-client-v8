package cm.aptoide.pt.feature_apps.data.network.model

internal class WidgetsJSON {
  data class WidgetNetwork(
    var type: WidgetType? = null,
    var title: String? = null, // Highlighted, Games, Categories, Timeline, Recommended for you,
    var tag: String? = null, // Aptoide Publishers
    var view: String? = null,
    var actions: List<ActionJSON>? = null,
    var data: DataJSON? = null
  )

  data class DataJSON(
    var layout: Layout? = null,
    var icon: String? = null,
    var message: String? = null,
    var groupId: Long? = null //only for eskills widget
  )

  data class ActionJSON(
    var type: String? = null, // button
    var label: String? = null,
    var tag: String? = null,
  )
}