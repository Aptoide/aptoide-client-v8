package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Setter;
import rx.Observable;
import rx.functions.Action0;

/**
 * Created by trinkes on 7/18/16.
 */
public class ActiveDownloadDisplayable extends DisplayablePojo<Progress<Download>> {

  private InstallManager installManager;
  @Setter private Action0 onResumeAction;
  @Setter private Action0 onPauseAction;

  public ActiveDownloadDisplayable() {
    super();
  }

  public ActiveDownloadDisplayable(Progress<Download> pojo, InstallManager installManager) {
    super(pojo);
    this.installManager = installManager;
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

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.active_donwload_row_layout;
  }

  public void pauseInstall(Context context) {
    installManager.stopInstallation(context, getPojo().getRequest().getMd5());
  }

  public Observable<Download> getDownload() {
    return installManager.getInstallation(getPojo().getRequest().getMd5())
        .map(downloadProgress -> downloadProgress.getRequest());
  }
}
