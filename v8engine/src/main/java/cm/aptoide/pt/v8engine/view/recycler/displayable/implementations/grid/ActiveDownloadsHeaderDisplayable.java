package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.Getter;

/**
 * Created by trinkes on 8/17/16.
 */
public class ActiveDownloadsHeaderDisplayable extends Displayable {

  private static final String TAG = ActiveDownloadsHeaderDisplayable.class.getSimpleName();
  @Getter private String label;
  private InstallManager installManager;

  public ActiveDownloadsHeaderDisplayable() {
  }

  public ActiveDownloadsHeaderDisplayable(String label, InstallManager installManager) {
    this.label = label;
    this.installManager = installManager;
  }

  @Override public int getViewLayout() {
    return R.layout.active_downloads_header_row;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  public void pauseAllDownloads(Context context) {
    installManager.stopAllInstallations(context);
  }
}
