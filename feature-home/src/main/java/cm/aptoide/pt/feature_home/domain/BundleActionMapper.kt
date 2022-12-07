package cm.aptoide.pt.feature_home.domain

class BundleActionMapper {

  fun mapWidgetActionToBundleAction(widget: Widget): BundleAction? {
    return if (!widget.action.isNullOrEmpty()) {
      widget.action[0].event?.let {
        if (it.name.equals(WidgetActionEventName.listApps)) {
          BundleAction(it.name, widget.title + widget.tag)
        } else {
          return null
        }
      }
    } else {
      null
    }
  }
}