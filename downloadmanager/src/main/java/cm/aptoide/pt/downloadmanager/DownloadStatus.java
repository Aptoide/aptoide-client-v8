package cm.aptoide.pt.downloadmanager;

import com.jakewharton.rxrelay.BehaviorRelay;

/**
 * Created by trinkes on 05/06/2017.
 */

public interface DownloadStatus {

  BehaviorRelay<Integer> getPending();

  BehaviorRelay<DownloadProgress> getProgress();

  BehaviorRelay<Integer> getPause();

  BehaviorRelay<Integer> getComplete();

  BehaviorRelay<DownloadProgress> getError();

  BehaviorRelay<Integer> getWarn();
}
