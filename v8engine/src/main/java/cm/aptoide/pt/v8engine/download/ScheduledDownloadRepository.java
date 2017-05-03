package cm.aptoide.pt.v8engine.download;

import cm.aptoide.pt.database.accessors.ScheduledAccessor;
import cm.aptoide.pt.database.realm.Scheduled;
import java.util.List;
import rx.Observable;

public class ScheduledDownloadRepository {

  private final ScheduledAccessor scheduledAccessor;

  public ScheduledDownloadRepository(ScheduledAccessor scheduledAccessor) {
    this.scheduledAccessor = scheduledAccessor;
  }

  public void save(Scheduled entity) {
    scheduledAccessor.insert(entity);
  }

  public Observable<Scheduled> get(String md5) {
    return scheduledAccessor.get(md5);
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
