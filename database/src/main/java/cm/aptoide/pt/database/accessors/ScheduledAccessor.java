/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Scheduled;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.List;
import lombok.Cleanup;
import rx.Observable;

/**
 * Created by sithengineer on 01/09/16.
 */
public class ScheduledAccessor implements Accessor {

  private final Database database;

  protected ScheduledAccessor(Database db) {
    this.database = db;
  }

  public Observable<List<Scheduled>> getAll() {
    return database.getAll(Scheduled.class);
  }

  public Observable<Scheduled> get(long appId) {
    return database.get(Scheduled.class, Scheduled.APP_ID, appId);
  }

  public void delete(long appId) {
    database.delete(Scheduled.class, Scheduled.APP_ID, appId);
  }

  public Observable<List<Scheduled>> setInstalling(List<Scheduled> scheduledList) {
    return Observable.fromCallable(() -> {

      Long[] ids = new Long[scheduledList.size()];
      Scheduled s;
      for (int i = 0; i < scheduledList.size(); ++i) {
        s = scheduledList.get(i);
        s.setDownloading(true);
        ids[i] = s.getAppId();
      }

      @Cleanup Realm realm = Database.get();
      realm.beginTransaction();
      realm.insertOrUpdate(scheduledList);
      RealmResults<Scheduled> results =
          realm.where(Scheduled.class).in(Scheduled.APP_ID, ids).findAll();
      for (Scheduled dbScheduled : results) {
        dbScheduled.setDownloading(true);
      }
      realm.commitTransaction();
      return scheduledList;
    });
  }

  public Observable<Scheduled> setInstalling(Scheduled scheduled) {
    return Observable.fromCallable(() -> {
      scheduled.setDownloading(true);

      @Cleanup Realm realm = Database.get();
      realm.beginTransaction();
      Scheduled dbScheduled =
          realm.where(Scheduled.class).equalTo(Scheduled.APP_ID, scheduled.getAppId()).findFirst();
      dbScheduled.setDownloading(true);
      realm.commitTransaction();
      return scheduled;
    });
  }
}
