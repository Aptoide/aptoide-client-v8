package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.ScheduledAccessor;
import cm.aptoide.pt.database.realm.Scheduled;
import java.util.List;
import rx.Observable;

public class ScheduledDownloadRepository implements Repository<Scheduled, String> {

  private final ScheduledAccessor scheduledAccessor;

  ScheduledDownloadRepository(ScheduledAccessor scheduledAccessor) {
    this.scheduledAccessor = scheduledAccessor;
  }

  public Observable<Scheduled> get(String md5) {
    return scheduledAccessor.get(md5);
  }

  @Override public void save(Scheduled entity) {
    scheduledAccessor.insert(entity);
  }

  public Observable<List<Scheduled>> getAllScheduledDownloads() {
    return scheduledAccessor.getAll();
  }

  public void deleteScheduledDownload(String md5) {
    scheduledAccessor.delete(md5);
  }

  public Observable<List<Scheduled>> setInstalling(List<Scheduled> scheduledList) {
    return scheduledAccessor.setInstalling(scheduledList);
  }

  public boolean hasScheduleDownloads() {
    return scheduledAccessor.hasScheduleDownloads();
  }
}
