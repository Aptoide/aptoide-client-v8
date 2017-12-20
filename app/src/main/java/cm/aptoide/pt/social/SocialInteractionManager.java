package cm.aptoide.pt.social;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.UserAccessor;
import cm.aptoide.pt.database.realm.User;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.GetUserMeta;
import rx.Observable;

/**
 * Created by franciscocalado on 11/30/17.
 */

public class SocialInteractionManager {

  private final UserAccessor userAccessor;
  private final AptoideAccountManager accountManager;

  public SocialInteractionManager(UserAccessor userAccessor, AptoideAccountManager accountManager) {

    this.userAccessor = userAccessor;
    this.accountManager = accountManager;
  }

  private static void saveUser(GetUserMeta userMeta, UserAccessor userAccessor) {

    User user = new User();

    user.setUsername(userMeta.getData()
        .getName());
    user.setUserId(userMeta.getData()
        .getId());
    user.setAvatar(userMeta.getData()
        .getAvatar());

    userAccessor.save(user);
  }

  public void followUser(Long userId, String username, String userIcon) {
    accountManager.accountStatus()
        .map(account -> account.isLoggedIn())
        .first()
        .subscribe(isLoggedIn -> {
          if (isLoggedIn) accountManager.subscribeUser(userId);
          User user = new User();
          user.setUserId(userId);
          user.setUsername(username);
          user.setAvatar(userIcon);
          userAccessor.save(user);
        });
  }

  public Observable<GetUserMeta> subscribeUserObservable(long userId) {
    return accountManager.getUserInfo(userId)
        .flatMap(userMeta -> accountManager.accountStatus()
            .first()
            .toSingle()
            .flatMapObservable(account -> {
              if (BaseV7Response.Info.Status.OK.equals(userMeta.getInfo()
                  .getStatus())) {
                if (account.isLoggedIn()) {
                  return accountManager.subscribeUser(userId)
                      .andThen(Observable.just(userMeta));
                } else {
                  return Observable.just(userMeta);
                }
              } else {
                return Observable.error(
                    new Exception("Something went wrong while getting user meta"));
              }
            }))
        .doOnNext(userMeta -> saveUser(userMeta, userAccessor));
  }

  public Observable<GetUserMeta> unsubscribeUserObservable(long userId) {
    return accountManager.getUserInfo(userId)
        .flatMap(userMeta -> accountManager.accountStatus()
            .first()
            .toSingle()
            .flatMapObservable(account -> {
              if (BaseV7Response.Info.Status.OK.equals(userMeta.getInfo()
                  .getStatus())) {
                if (account.isLoggedIn()) {
                  return accountManager.unsubscribeUser(userId)
                      .andThen(Observable.just(userMeta));
                } else {
                  return Observable.just(userMeta);
                }
              } else {
                return Observable.error(
                    new Exception("Something went wrong while getting user meta"));
              }
            }))
        .doOnNext(userMeta -> userAccessor.remove(userId));
  }

  public void unfollowUser(Long userId) {
    accountManager.accountStatus()
        .map(account -> account.isLoggedIn())
        .first()
        .subscribe(isLoggedIn -> {
          if (isLoggedIn) accountManager.unsubscribeUser(userId);
          userAccessor.remove(userId);
        });
  }
}
