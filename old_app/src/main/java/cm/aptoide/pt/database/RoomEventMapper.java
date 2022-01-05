package cm.aptoide.pt.database;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.data.Event;
import cm.aptoide.pt.database.room.RoomEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomEventMapper {
  private final ObjectMapper objectMapper;

  public RoomEventMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public RoomEvent map(Event event) throws JsonProcessingException {
    return new RoomEvent(event.getTimeStamp(), event.getEventName(), event.getAction()
        .ordinal(), event.getContext(), objectMapper.writeValueAsString(event.getData()));
  }

  public List<Event> map(List<RoomEvent> roomEvents) throws IOException {
    ArrayList<Event> events = new ArrayList<>();
    for (RoomEvent roomEvent : roomEvents) {
      events.add(new Event(roomEvent.getEventName(),
          objectMapper.readValue(roomEvent.getData(), new TypeReference<Map<String, Object>>() {
          }), AnalyticsManager.Action.values()[roomEvent.getAction()], roomEvent.getContext(),
          roomEvent.getTimestamp()));
    }
    return events;
  }
}
