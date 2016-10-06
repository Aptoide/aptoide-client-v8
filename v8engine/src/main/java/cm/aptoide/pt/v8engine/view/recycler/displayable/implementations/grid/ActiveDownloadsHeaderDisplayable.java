package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.model.v7.Type;
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

  @Override public Type getType() {
    return Type.ACTIVE_DOWNLOAD_HEADER;
  }

  @Override public int getViewLayout() {
    return R.layout.active_downloads_header_row;
  }

  public void pauseAllDownloads(Context context) {
    installManager.stopAllInstallations(context);
  }
}
