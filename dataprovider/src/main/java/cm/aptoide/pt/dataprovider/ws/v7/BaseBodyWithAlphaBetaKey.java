package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import lombok.Getter;

/**
 * Created by diogoloureiro on 17/02/2017.
 *
 * Requests with this body will check the key to show alpha and beta versions of apps
 * when the options is set on settings, the ws response will not contain any alpha or beta versions
 */

public class BaseBodyWithAlphaBetaKey extends BaseBody {
  @Getter private String notApkTags;

  protected BaseBodyWithAlphaBetaKey() {
    if (ManagerPreferences.getUpdatesFilterAlphaBetaKey()) {
      this.notApkTags = "alpha,beta";
    }
  }
}
