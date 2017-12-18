package cm.aptoide.pt.account;

import cm.aptoide.pt.database.accessors.UserAccessor;
import cm.aptoide.pt.database.realm.User;
import cm.aptoide.pt.logger.Logger;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by franciscocalado on 12/15/17.
 */

public class DatabaseUserDataPersist {

  private final DatabaseUserMapper databaseUserMapper;
  private final UserAccessor accessor;

  public DatabaseUserDataPersist(UserAccessor userAccessor, DatabaseUserMapper databaseUserMapper) {

    accessor = userAccessor;
    this.databaseUserMapper = databaseUserMapper;
  }

  public Completable persist(List<cm.aptoide.accountmanager.User> users) {
    return Observable.from(users)
        .map(user -> databaseUserMapper.toDatabase(user))
        .toList()
        .doOnNext(userList -> accessor.insertAll(userList))
        .toCompletable();
  }

  public Single<List<cm.aptoide.accountmanager.User>> get() {
    return accessor.getAll()
        .first()
        .flatMapIterable(list -> list)
        .map(user -> databaseUserMapper.fromDatabase(user))
        .toList()
        .toSingle()
        .doOnSuccess(users -> {
          Logger.d("DatabaseUserDataPersist", "nr users= " + (users != null ? users.size() : 0));
        });
  }

  public static class DatabaseUserMapper {

    public User toDatabase(cm.aptoide.accountmanager.User user) {
      User result = new User();
      result.setUserId(user.getId());
      result.setAvatar(user.getAvatar());
      result.setUsername(user.getUsername());
      return result;
    }

    public cm.aptoide.accountmanager.User fromDatabase(User user) {
      return new cm.aptoide.accountmanager.User(user.getUserId(), user.getUsername(),
          user.getAvatar());
    }
  }
}
