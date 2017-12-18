package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.User;
import java.util.List;
import rx.Observable;

/**
 * Created by franciscocalado on 12/15/17.
 */

public class UserAccessor extends SimpleAccessor<User> {

  public UserAccessor(Database db) {
    super(db, User.class);
  }

  public Observable<List<User>> getAll() {
    return database.getAll(User.class);
  }

  public Observable<User> get(String username) {
    return database.get(User.class, User.USERNAME, username);
  }

  public void remove(long userId) {
    database.delete(User.class, User.USER_ID, userId);
  }

  public void remove(String username) {
    database.delete(User.class, User.USERNAME, username);
  }

  public void save(User user) {
    database.insert(user);
  }

  public Observable<List<User>> getAsList(long userId) {
    return database.getAsList(User.class, User.USER_ID, userId);
  }

  public Observable<List<User>> getAsList(String username) {
    return database.getAsList(User.class, User.USERNAME, username);
  }
}
