package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.support.v4.app.FragmentActivity;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.Getter;
import rx.Observable;

/**
 * Created by neuro on 02-08-2016.
 */
public class UpdatesHeaderDisplayable extends Displayable {

  @Getter private String label;
  @Getter private InstallManager installManager;

  public UpdatesHeaderDisplayable() {
  }

  public UpdatesHeaderDisplayable(InstallManager installManager, String label) {
    this.installManager = installManager;
    this.label = label;
  }

  @Override public Type getType() {
    return Type.UPDATES_HEADER;
  }

  @Override public int getViewLayout() {
    return R.layout.updates_header_row;
  }

  public Observable<Progress<Download>> install(FragmentActivity context, Download download) {
    return installManager.install(context, download);
  }
}
