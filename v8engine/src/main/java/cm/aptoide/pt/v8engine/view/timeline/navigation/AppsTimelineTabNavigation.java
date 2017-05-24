package cm.aptoide.pt.v8engine.view.timeline.navigation;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.view.navigator.TabNavigation;

/**
 * Created by jdandrade on 02/05/2017.
 */

public class AppsTimelineTabNavigation implements TabNavigation {
  public static final String CARD_ID_KEY = "CARD_ID_KEY";
  private String cardId;

  public AppsTimelineTabNavigation(String cardId) {
    this.cardId = cardId;
  }

  @Override public Bundle getBundle() {
    Bundle bundle = new Bundle();
    bundle.putString(CARD_ID_KEY, cardId);
    return bundle;
  }

  @Override public int getTab() {
    return TIMELINE;
  }

  public String getCardId() {
    return cardId;
  }
}
