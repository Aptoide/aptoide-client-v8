package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.pt.database.realm.RealmEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by trinkes on 12/01/2018.
 */

public class RealmEventMapper {
  private final ObjectMapper objectMapper;

  public RealmEventMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public RealmEvent map(Event event) throws JsonProcessingException {
    return new RealmEvent(event.getTimeStamp(), event.getEventName(), event.getAction()
        .ordinal(), event.getContext(), objectMapper.writeValueAsString(event.getData()));
  }

  public List<Event> map(List<RealmEvent> realmEvents) throws IOException {
    ArrayList<Event> events = new ArrayList<>();
    for (RealmEvent realmEvent : realmEvents) {
      events.add(new Event(realmEvent.getEventName(),
          objectMapper.readValue(realmEvent.getData(), new TypeReference<Map<String, Object>>() {
          }), AnalyticsManager.Action.values()[realmEvent.getAction()], realmEvent.getContext(),
          realmEvent.getTimestamp()));
    }
    return events;
  }
}
