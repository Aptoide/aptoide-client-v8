package cm.aptoide.pt.remotebootconfig.datamodel;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Splash class
 */

public class Splash {
  private boolean enable;
  private String portrait;
  private String landscape;
  private int timeout;

  public boolean isEnable() {
    return enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  public String getPortrait() {
    return portrait;
  }

  public void setPortrait(String portrait) {
    this.portrait = portrait;
  }

  public String getLandscape() {
    return landscape;
  }

  public void setLandscape(String landscape) {
    this.landscape = landscape;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }
}