package cm.aptoide.pt.v8engine.view.downloads.active;

import android.content.Context;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import rx.Observable;
import rx.functions.Action0;

/**
 * Created by trinkes on 7/18/16.
 */
public class ActiveDownloadDisplayable extends Displayable {

  private final InstallManager installManager;
  private final Download download;
  private Action0 onResumeAction;
  private Action0 onPauseAction;

  public ActiveDownloadDisplayable() {
    this.installManager = null;
    this.download = null;
  }

  public ActiveDownloadDisplayable(Download download, InstallManager installManager) {
    this.download = download;
    this.installManager = installManager;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.active_download_row_layout;
  }

  @Override public void onResume() {
    super.onResume();
    if (onResumeAction != null) {
      onResumeAction.call();
    }
  }

  @Override public void onPause() {
    if (onPauseAction != null) {
      onPauseAction.call();
    }
    super.onPause();
  }

  public void pauseInstall(Context context) {
    installManager.stopInstallation(context, download.getMd5());
  }

  public Observable<Download> getDownloadObservable() {
    return installManager.getInstallation(download.getMd5())
        .map(downloadProgress -> downloadProgress.getRequest());
  }

  public void setOnPauseAction(Action0 onPauseAction) {
    this.onPauseAction = onPauseAction;
  }

  public void setOnResumeAction(Action0 onResumeAction) {
    this.onResumeAction = onResumeAction;
  }
}
