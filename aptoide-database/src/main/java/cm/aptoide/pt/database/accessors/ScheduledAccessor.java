/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Scheduled;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.List;
import lombok.Cleanup;
import rx.Observable;

/**
 * Created on 01/09/16.
 */
public class ScheduledAccessor extends SimpleAccessor<Scheduled> {

  public ScheduledAccessor(Database db) {
    super(db, Scheduled.class);
  }

  public Observable<List<Scheduled>> getAll() {
    return database.getAll(Scheduled.class);
  }

  public Observable<Scheduled> get(String md5) {
    return database.get(Scheduled.class, Scheduled.MD5, md5);
  }

  public void delete(String md5) {
    database.delete(Scheduled.class, Scheduled.MD5, md5);
  }

  public Observable<List<Scheduled>> setInstalling(List<Scheduled> scheduledList) {
    return Observable.fromCallable(() -> {

      String[] md5s = new String[scheduledList.size()];
      Scheduled s;
      for (int i = 0; i < scheduledList.size(); ++i) {
        s = scheduledList.get(i);
        s.setDownloading(true);
        md5s[i] = s.getMd5();
      }

      @Cleanup Realm realm = database.get();
      realm.beginTransaction();
      realm.insertOrUpdate(scheduledList);
      RealmResults<Scheduled> results = realm.where(Scheduled.class)
          .in(Scheduled.MD5, md5s)
          .findAll();
      for (Scheduled dbScheduled : results) {
        dbScheduled.setDownloading(true);
      }
      realm.commitTransaction();
      return scheduledList;
    });
  }

  public boolean hasScheduleDownloads() {
    @Cleanup Realm realm = database.get();
    return realm.where(Scheduled.class)
        .findAll()
        .size() != 0;
  }
}
