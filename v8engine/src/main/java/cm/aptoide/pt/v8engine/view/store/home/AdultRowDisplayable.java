package cm.aptoide.pt.v8engine.view.store.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.ReloadInterface;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by neuro on 11-07-2016.
 */
public class AdultRowDisplayable extends Displayable implements ReloadInterface {

  @Nullable private final ReloadInterface reloader;

  /**
   * This constructor is called in the auto-magic code that creates all displayable to widget
   * mapping, so it is needed to run the app.
   */
  public AdultRowDisplayable() {
    this.reloader = null;
  }

  public AdultRowDisplayable(@NonNull ReloadInterface reloader) {
    this.reloader = reloader;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.row_adult_switch;
  }

  @Override public void reload() {
    if (reloader != null) {
      reloader.reload();
    }
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    if (reloader != null) {
      reloader.load(create, refresh, savedInstanceState);
    }
  }
}
