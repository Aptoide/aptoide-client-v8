package cm.aptoide.analytics.implementation;

import rx.Completable;

public interface AptoideBiEventService {
  Completable send(Event event);
}
