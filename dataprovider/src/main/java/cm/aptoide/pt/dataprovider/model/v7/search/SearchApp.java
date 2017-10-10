package cm.aptoide.pt.dataprovider.model.v7.search;

import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;

public class SearchApp extends App {
  private boolean hasVersions;
  private Obb obb;

  public boolean hasVersions() {
    return hasVersions;
  }

  public void setHasVersions(boolean hasVersions) {
    this.hasVersions = hasVersions;
  }

  @Override public Obb getObb() {
    return obb;
  }

  @Override public void setObb(Obb obb) {
    this.obb = obb;
  }
}
