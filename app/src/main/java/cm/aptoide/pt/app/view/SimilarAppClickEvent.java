package cm.aptoide.pt.app.view;

import cm.aptoide.pt.app.AppViewSimilarApp;

/**
 * Created by franciscocalado on 14/05/18.
 */

public class SimilarAppClickEvent {

  private AppViewSimilarApp similar;
  private String type;

  public SimilarAppClickEvent(AppViewSimilarApp similar, String type) {
    this.similar = similar;
    this.type = type;
  }

  public AppViewSimilarApp getSimilar() {
    return similar;
  }

  public String getType() {
    return type;
  }
}
