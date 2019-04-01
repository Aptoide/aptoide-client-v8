package cm.aptoide.pt.app.view.similar;

import cm.aptoide.pt.app.AppViewSimilarApp;
import cm.aptoide.pt.app.view.AppViewSimilarAppsAdapter;

/**
 * Created by franciscocalado on 14/05/18.
 */

public class SimilarAppClickEvent {

  private AppViewSimilarApp similar;
  private AppViewSimilarAppsAdapter.SimilarAppType type;
  private int position;

  public SimilarAppClickEvent(AppViewSimilarApp similar,
      AppViewSimilarAppsAdapter.SimilarAppType type, int position) {
    this.similar = similar;
    this.type = type;
    this.position = position;
  }

  public AppViewSimilarApp getSimilar() {
    return similar;
  }

  public AppViewSimilarAppsAdapter.SimilarAppType getType() {
    return type;
  }

  public int getPosition() {
    return position;
  }
}
