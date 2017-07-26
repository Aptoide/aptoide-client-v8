package cm.aptoide.pt.downloadmanager;

import android.support.annotation.CheckResult;
import rx.Observable;

/**
 * Created by trinkes on 05/06/2017.
 */

public interface DownloadStatus {

  Observable<Integer> getPending();

  Observable<DownloadProgress> getProgress();

  Observable<Integer> getPause();

  Observable<Integer> getComplete();

  Observable<DownloadProgress> getError();

  Observable<Integer> getWarn();

  int getId();

  String getPath();

  String fileName();

  boolean isCompleted();

  @CheckResult boolean startDownload();
}
