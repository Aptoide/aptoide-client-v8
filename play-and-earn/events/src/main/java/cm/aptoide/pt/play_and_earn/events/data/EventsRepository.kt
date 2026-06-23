package cm.aptoide.pt.play_and_earn.events.data

import cm.aptoide.pt.play_and_earn.events.domain.EventType

interface EventsRepository {

  suspend fun submitEvent(eventType: EventType): Result<Unit>
}
