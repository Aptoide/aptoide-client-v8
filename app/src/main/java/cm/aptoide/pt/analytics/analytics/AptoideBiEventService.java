package cm.aptoide.pt.analytics.analytics;

import rx.Completable;

public interface AptoideBiEventService {
  Completable send(Event event);
}
