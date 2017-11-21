package cm.aptoide.pt.download.view.active;

import android.content.Context;
import cm.aptoide.pt.R;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.view.recycler.displayable.Displayable;

/**
 * Created by trinkes on 8/17/16.
 */
public class ActiveDownloadsHeaderDisplayable extends Displayable {

  private String label;
  private InstallManager installManager;

  public ActiveDownloadsHeaderDisplayable() {
  }

  public ActiveDownloadsHeaderDisplayable(String label, InstallManager installManager) {
    this.label = label;
    this.installManager = installManager;
  }

  public String getLabel() {
    return label;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.active_downloads_header_row;
  }

  public void pauseAllDownloads(Context context) {
    installManager.stopAllInstallations();
  }
}
