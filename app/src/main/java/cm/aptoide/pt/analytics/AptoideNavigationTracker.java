package cm.aptoide.pt.analytics;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import cm.aptoide.pt.account.view.LoginSignUpCredentialsFragment;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.view.store.StoreFragment;
import cm.aptoide.pt.view.wizard.WizardFragment;
import java.util.List;

/**
 * Created by jdandrade on 06/09/2017.
 */

public class AptoideNavigationTracker {

  private static final String TAG = AptoideNavigationTracker.class.getSimpleName();
  private List<ScreenTagHistory> historyList;

  public AptoideNavigationTracker(List<ScreenTagHistory> historyList) {
    this.historyList = historyList;
  }

  public void registerScreen(ScreenTagHistory screenTagHistory) {
    if (screenTagHistory != null && filter(screenTagHistory)) {
      historyList.add(screenTagHistory);
      Logger.d(TAG, "VIEW - " + screenTagHistory);
    }
  }

  public @Nullable ScreenTagHistory getCurrentScreen() {
    if (historyList.isEmpty()) {
      return null;
    }
    return historyList.get(historyList.size() - 1);
  }

  public @Nullable ScreenTagHistory getPreviousScreen() {
    if (historyList.size() < 2) {
      return null;
    }
    return historyList.get(historyList.size() - 2);
  }

  public String getPreviousViewName() {
    if (historyList.size() < 2) {
      return "";
    }
    return historyList.get(historyList.size() - 2)
        .getFragment();
  }

  public String getCurrentViewName() {
    if (historyList.isEmpty()) {
      return "";
    } else if (historyList.get(historyList.size() - 1)
        .getFragment() == null) {
      return "";
    }
    return historyList.get(historyList.size() - 1)
        .getFragment();
  }

  private boolean filter(ScreenTagHistory screenTagHistory) {
    String viewName = screenTagHistory.getFragment();
    if (TextUtils.isEmpty(viewName)) {
      return false;
    } else if (viewName.equals(WizardFragment.class.getSimpleName())) {
      return false;
    } else if (viewName.equals(LoginSignUpCredentialsFragment.class.getSimpleName())) {
      return false;
    } else if (viewName.equals(StoreFragment.class.getSimpleName())) {
      return false;
    }
    return true;
  }
}
