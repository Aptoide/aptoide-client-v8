/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 09/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.content.res.Resources;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.model.v7.Type;

/**
 * Created by neuro on 21-04-2016.
 */
public class WidgetsArgs extends HashMapNotNull<WidgetsArgs.Key, WidgetsArgs.GridSizeObject> {

  public WidgetsArgs() {
  }

  public WidgetsArgs(int appsRowSize, int storesRowSize) {
    add(Key.APPS_GROUP, appsRowSize);
    add(Key.STORES_GROUP, storesRowSize);
  }

  public static WidgetsArgs createDefault(Resources resources, WindowManager windowManager) {
    return new WidgetsArgs().add(Key.APPS_GROUP,
        Type.APPS_GROUP.getPerLineCount(resources, windowManager))
        .add(Key.STORES_GROUP, Type.STORES_GROUP.getPerLineCount(resources, windowManager))
        .add(Key.MY_STORES_SUBSCRIBED,
            Type.MY_STORES_SUBSCRIBED.getPerLineCount(resources, windowManager))
        .add(Key.STORES_RECOMMENDED,
            Type.STORES_RECOMMENDED.getPerLineCount(resources, windowManager));
  }

  public WidgetsArgs add(Key key, int gridRowSize) {
    if (!containsKey(key)) {
      put(key, new GridSizeObject(gridRowSize));
    }
    return this;
  }

  // FIXME Parece me redundante com Type! Confirmar!
  public enum Key {
    APPS_GROUP, MY_STORES_SUBSCRIBED, STORES_RECOMMENDED, STORES_GROUP
  }

  protected static class GridSizeObject {

    private int grid_row_size;

    public GridSizeObject(int grid_row_size) {
      this.grid_row_size = grid_row_size;
    }

    public int getGrid_row_size() {
      return grid_row_size;
    }

    public void setGrid_row_size(int grid_row_size) {
      this.grid_row_size = grid_row_size;
    }
  }
}
