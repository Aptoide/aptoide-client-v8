package cm.aptoide.pt.social;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.social.data.TimelineService;
import cm.aptoide.pt.social.data.User;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 06/09/2017.
 */

public class StatsUserProvider implements TimelineUserProvider {

  private AptoideAccountManager accountManager;
  private TimelineService service;

  public StatsUserProvider(AptoideAccountManager accountManager, TimelineService service) {
    this.accountManager = accountManager;
    this.service = service;
  }

  @Override public Completable notificationRead(NotificationType notificationType) {
    return Completable.complete();
  }

  @Override public Observable<User> getUser() {
    return accountManager.accountStatus()
        .map(account -> account.isLoggedIn())
        .flatMap(isLogged -> service.getTimelineStats()
            .toObservable()
            .map(timelineStats -> new User(timelineStats.getFollowers(),
                timelineStats.getFollowings(), isLogged)));
  }
}
