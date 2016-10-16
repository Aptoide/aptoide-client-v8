package cm.aptoide.pt.v8engine.util;

import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by neuro on 15-10-2016.
 */

public class UpdateUtils {
  public static void checkUpdates() {
    checkUpdates(null);
  }

  public static void checkUpdates(
      @Nullable SuccessRequestListener<ListAppsUpdates> successRequestListener) {
    @Cleanup Realm realm1 = DeprecatedDatabase.get();
    if (DeprecatedDatabase.StoreQ.getAll(realm1).size() == 0) {
      return;
    }

    ListAppsUpdatesRequest.of(StoreUtils.getSubscribedStoresIds(),
        AptoideAccountManager.getAccessToken()).execute(listAppsUpdates -> {
      @Cleanup Realm realm = DeprecatedDatabase.get();
      for (App app : listAppsUpdates.getList()) {
        Update update = DeprecatedDatabase.UpdatesQ.get(app.getPackageName(), realm);
        if (update == null || !update.isExcluded()) {
          DeprecatedDatabase.save(new Update(app), realm);
        }
      }

      if (successRequestListener != null) {
        successRequestListener.call(listAppsUpdates);
      }
    }, Throwable::printStackTrace, true);
  }
}
