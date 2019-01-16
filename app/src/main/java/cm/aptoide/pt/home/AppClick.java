package cm.aptoide.pt.home;

import cm.aptoide.pt.view.app.AptoideApp;

/**
 * Created by jdandrade on 28/03/2018.
 */

public class AppClick {

  private final AptoideApp app;
  private final int position;
  private final Type type;

  public AppClick(AptoideApp app, int position, Type type) {
    this.app = app;
    this.position = position;
    this.type = type;
  }

  public AptoideApp getApp() {
    return app;
  }

  public int getPosition() {
    return position;
  }

  public Type getType() {
    return type;
  }

  public enum Type {
    SOCIAL_CLICK, SOCIAL_INSTALL
  }
}
