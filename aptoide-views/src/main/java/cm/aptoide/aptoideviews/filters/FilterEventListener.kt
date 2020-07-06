package cm.aptoide.aptoideviews.filters

interface FilterEventListener {
  fun onFilterEvent(eventType: EventType, filter: Filter?)
  enum class EventType { FILTER_CLICK, CLEAR_EVENT_CLICK }
}