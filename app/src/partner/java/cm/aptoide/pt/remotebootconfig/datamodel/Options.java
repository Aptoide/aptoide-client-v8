package cm.aptoide.pt.remotebootconfig.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Option Class
 */

public class Options {

  private boolean shortcut;
  private Multistore multistore;
  private FirstInstall first_install;

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

  public FirstInstall getFirst_install() {
    return first_install;
  }

  public void setFirst_install(FirstInstall firstInstall) {
    this.first_install = firstInstall;
  }
}