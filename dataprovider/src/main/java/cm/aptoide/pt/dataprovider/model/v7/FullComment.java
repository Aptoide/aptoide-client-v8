/*
 * Copyright (c) 2016.
 * Modified on 03/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created on 02/08/16.
 */
public class FullComment {
  //	private GetAppMeta.App data;

  private AppData data;

  public static class AppData {

    private GetAppMeta.App app;

    public AppData() {
    }

    public GetAppMeta.App getApp() {
      return this.app;
    }

    public void setApp(GetAppMeta.App app) {
      this.app = app;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $app = this.getApp();
      result = result * PRIME + ($app == null ? 43 : $app.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof AppData)) return false;
      final AppData other = (AppData) o;
      if (!other.canEqual(this)) return false;
      final Object this$app = this.getApp();
      final Object other$app = other.getApp();
      return this$app == null ? other$app == null : this$app.equals(other$app);
    }

    public String toString() {
      return "FullComment.AppData(app=" + this.getApp() + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof AppData;
    }
  }
}
