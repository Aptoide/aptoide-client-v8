package cm.aptoide.pt.remotebootconfig.datamodel;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Appearance Class
 */

public class Appearance {
  private String theme;
  private Splash splash;
  private Toolbar toolbar;

  public String getTheme() {
    return theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  public Splash getSplash() {
    return splash;
  }

  public void setSplash(Splash splash) {
    this.splash = splash;
  }

  public Toolbar getToolbar() {
    return toolbar;
  }

  public void setToolbar(Toolbar toolbar) {
    this.toolbar = toolbar;
  }
}