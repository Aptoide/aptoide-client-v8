package cm.aptoide.pt.remotebootconfig.datamodel;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Multistore class
 */

public class Multistore {
  private boolean enable;
  private boolean search;

  public boolean isEnable() {
    return enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  public boolean isSearch() {
    return search;
  }

  public void setSearch(boolean search) {
    this.search = search;
  }
}