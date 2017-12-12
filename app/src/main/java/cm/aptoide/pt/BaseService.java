package cm.aptoide.pt;

import android.app.Service;

public abstract class BaseService extends Service {

  public ApplicationComponent getApplicationComponent() {
    return ((AptoideApplication) getApplication()).getApplicationComponent();
  }
}
