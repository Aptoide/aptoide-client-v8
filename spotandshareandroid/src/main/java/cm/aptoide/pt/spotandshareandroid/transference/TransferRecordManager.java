package cm.aptoide.pt.spotandshareandroid.transference;

import android.support.annotation.Nullable;
import cm.aptoide.pt.spotandshareandroid.App;
import cm.aptoide.pt.spotandshareandroid.ApplicationsManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by filipegoncalves on 09-02-2017.
 */

public class TransferRecordManager {

  private final ExecutorService singleThreadedExecutorService = Executors.newSingleThreadExecutor();
  private ApplicationsManager applicationsManager;

  public TransferRecordManager(ApplicationsManager applicationsManager) {
    this.applicationsManager = applicationsManager;
  }

  public void deleteAllApps(DeleteAppsListener listener, List<TransferRecordItem> listOfApps) {
    List<TransferRecordItem> toRemoveList = findAppsToRemove(listOfApps);
    if (toRemoveList != null) {
      listOfApps.removeAll(toRemoveList);
      listener.onDeleteAllApps(toRemoveList);
    }
  }

  private List<TransferRecordItem> findAppsToRemove(List<TransferRecordItem> listOfApps) {
    List<TransferRecordItem> toRemoveList = new ArrayList<>();
    for (int i = 0; i < listOfApps.size(); i++) {
      if (listOfApps.get(i)
          .isSent() || listOfApps.get(i)
          .isReceived()) {
        listOfApps.get(i)
            .setDeleted(true);
        toRemoveList.add(listOfApps.get(i));
        if (listOfApps.get(i)
            .isReceived()) {
          String tmpFilePath = listOfApps.get(i)
              .getFilePath();
          applicationsManager.deleteAppFile(tmpFilePath);
        }
      }
    }
    return toRemoveList;
  }

  public void deleteAppFile(String filePath) {
    applicationsManager.deleteAppFile(filePath);
  }

  public void installApp(String filePath, String packageName) {
    applicationsManager.moveObbs(filePath, packageName);
    applicationsManager.installApp(filePath);
  }

  public void installAppAsync(String filePath, String packageName) {
    singleThreadedExecutorService.execute(() -> installApp(filePath, packageName));
  }

  public App convertTransferRecordItemToApp(TransferRecordItem item) {
    App app = applicationsManager.convertTransferRecordItemToApp(item);
    return app;
  }

  public TransferRecordItem readApkArchive(String appName, String filePath) {
    TransferRecordItem item = applicationsManager.readApkArchive(appName, filePath);
    return item;
  }

  public App readApkArchive(String filepath) {
    App app = applicationsManager.readApkArchive(filepath);
    return app;
  }

  @Nullable public TransferRecordItem startedSending(String appName, String packageName,
      boolean needReSend, boolean isSent) {
    TransferRecordItem item =
        applicationsManager.startedSending(appName, packageName, needReSend, isSent);
    if (item != null) {
      return item;
    } else {
      return null;
    }
  }

  public void stop() {
    this.applicationsManager.stop();
  }

  public interface DeleteAppsListener {

    void onDeleteAllApps(List<TransferRecordItem> toRemoveList);
  }
}
