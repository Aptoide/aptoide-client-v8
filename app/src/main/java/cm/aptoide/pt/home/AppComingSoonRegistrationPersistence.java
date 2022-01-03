package cm.aptoide.pt.home;

import cm.aptoide.pt.database.room.RoomAppComingSoonRegistration;
import rx.Completable;
import rx.Observable;

public interface AppComingSoonRegistrationPersistence {

  Observable<Boolean> isRegisteredForApp(String packageName);

  Completable registerForAppNotification(
      RoomAppComingSoonRegistration roomAppComingSoonRegistration);

  Completable unregisterForAppNotification(
      RoomAppComingSoonRegistration roomAppComingSoonRegistration);
}
