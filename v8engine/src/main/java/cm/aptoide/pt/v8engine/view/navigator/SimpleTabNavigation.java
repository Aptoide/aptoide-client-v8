package cm.aptoide.pt.v8engine.view.navigator;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by jdandrade on 02/05/2017.
 *
 * Simple Tab Navigation implementation that doesn't have bundled information in it.
 */

public class SimpleTabNavigation implements TabNavigation {

  private int tab;

  /**
   * @param tab One of the {@link TabNavigation} fields.
   */
  public SimpleTabNavigation(int tab) {
    this.tab = tab;
  }

  @Nullable @Override public Bundle getBundle() {
    return null;
  }

  @Override public int getTab() {
    return tab;
  }
}
