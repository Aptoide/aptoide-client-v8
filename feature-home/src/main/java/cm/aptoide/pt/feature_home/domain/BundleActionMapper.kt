package cm.aptoide.pt.feature_home.domain

class BundleActionMapper {

  fun mapWidgetActionToBundleAction(widget: Widget): WidgetActionEventName? =
    widget.action
      ?.filter { it.type == WidgetActionType.BUTTON }
      ?.mapNotNull { it.event?.name }
      ?.firstOrNull { it == WidgetActionEventName.listApps || it == WidgetActionEventName.groups }
}
