/*
 * Copyright (c) 2016.
 * Modified on 03/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created on 02/08/16.
 */
public class FullReview extends Review {

  private AppData data;

  public FullReview() {
  }

  public AppData getData() {
    return this.data;
  }

  public void setData(AppData data) {
    this.data = data;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $data = this.getData();
    result = result * PRIME + ($data == null ? 43 : $data.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof FullReview;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof FullReview)) return false;
    final FullReview other = (FullReview) o;
    if (!other.canEqual(this)) return false;
    if (!super.equals(o)) return false;
    final Object this$data = this.getData();
    final Object other$data = other.getData();
    return this$data == null ? other$data == null : this$data.equals(other$data);
  }

  public String toString() {
    return "FullReview(data=" + this.getData() + ")";
  }

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
      return "FullReview.AppData(app=" + this.getApp() + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof AppData;
    }
  }
}
