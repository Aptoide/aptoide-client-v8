package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.NewDatabase;
import cm.aptoide.pt.database.realm.Update;
import java.util.List;
import rx.Observable;

/**
 * Created by trinkes on 9/2/16.
 */
public class UpdatesAccessor {

  private final NewDatabase database;

  public UpdatesAccessor(NewDatabase db) {
    this.database = db;
  }

  public Observable<List<Update>> getAll() {
    return database.getAll(Update.class);
  }
}
