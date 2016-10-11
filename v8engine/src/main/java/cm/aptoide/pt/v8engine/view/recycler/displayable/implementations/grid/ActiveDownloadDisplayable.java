package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import rx.Observable;

/**
 * Created by trinkes on 7/18/16.
 */
public class ActiveDownloadDisplayable extends DisplayablePojo<Progress<Download>> {

  private InstallManager installManager;

  public ActiveDownloadDisplayable() {
    super();
  }

  public ActiveDownloadDisplayable(Progress<Download> pojo, InstallManager installManager) {
    super(pojo);
    this.installManager = installManager;
  }

  public ActiveDownloadDisplayable(Progress<Download> pojo, boolean fixedPerLineCount) {
    super(pojo, fixedPerLineCount);
  }

  @Override public Type getType() {
    return Type.ACTIVE_DOWNLOAD;
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
