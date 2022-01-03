package cm.aptoide.pt.database;

import cm.aptoide.pt.database.room.AppComingSoonRegistrationDAO;
import cm.aptoide.pt.database.room.RoomAppComingSoonRegistration;
import cm.aptoide.pt.home.AppComingSoonRegistrationPersistence;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.BackpressureStrategy;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RoomAppComingSoonPersistence implements AppComingSoonRegistrationPersistence {

  private final AppComingSoonRegistrationDAO appComingSoonRegistrationDAO;

  public RoomAppComingSoonPersistence(AppComingSoonRegistrationDAO appComingSoonRegistrationDAO) {
    this.appComingSoonRegistrationDAO = appComingSoonRegistrationDAO;
  }

  @Override public Observable<Boolean> isRegisteredForApp(String packageName) {
    return RxJavaInterop.toV1Observable(
        appComingSoonRegistrationDAO.isRegisteredForApp(packageName), BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io())
        .map(count -> count > 0);
  }

  @Override public Completable registerForAppNotification(
      RoomAppComingSoonRegistration roomAppComingSoonRegistration) {
    return Completable.fromAction(
        () -> appComingSoonRegistrationDAO.save(roomAppComingSoonRegistration))
        .subscribeOn(Schedulers.io());
  }

  @Override public Completable unregisterForAppNotification(
      RoomAppComingSoonRegistration roomAppComingSoonRegistration) {
    return Completable.fromAction(
        () -> appComingSoonRegistrationDAO.remove(roomAppComingSoonRegistration))
        .subscribeOn(Schedulers.io());
  }
}
