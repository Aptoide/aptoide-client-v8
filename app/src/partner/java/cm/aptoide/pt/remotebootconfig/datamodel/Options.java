package cm.aptoide.pt.remotebootconfig.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Option Class
 */

public class Options {

  private boolean shortcut;
  private Multistore multistore;
  @SerializedName("first_install") private FirstInstall firstInstall;

  public boolean isShortcut() {
    return shortcut;
  }

  public void setShortcut(boolean shortcut) {
    this.shortcut = shortcut;
  }

  public Multistore getMultistore() {
    return multistore;
  }

  public void setMultistore(Multistore multistore) {
    this.multistore = multistore;
  }

  public FirstInstall getFirstInstall() {
    return firstInstall;
  }

  public void setFirstInstall(FirstInstall firstInstall) {
    this.firstInstall = firstInstall;
  }
}