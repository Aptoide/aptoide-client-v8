package cm.aptoide.analytics.implementation;

import cm.aptoide.analytics.implementation.data.Event;
import rx.Completable;

public interface AptoideBiEventService {
  Completable send(Event event);
}
