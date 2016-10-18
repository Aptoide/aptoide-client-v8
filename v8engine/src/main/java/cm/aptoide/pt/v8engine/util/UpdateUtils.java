package cm.aptoide.pt.v8engine.util;

import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;

/**
 * Created by neuro on 15-10-2016.
 */

public class UpdateUtils {
  public static void checkUpdates() {
    checkUpdates(null);
  }

  public static void checkUpdates(
      @Nullable SuccessRequestListener<ListAppsUpdates> successRequestListener) {
    StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
    if (storeAccessor.getAll().toBlocking().first().size() == 0) {
      return;
    }

    ListAppsUpdatesRequest.of(StoreUtils.getSubscribedStoresIds(),
        AptoideAccountManager.getAccessToken(), AptoideAccountManager.getUserEmail())
        .execute(listAppsUpdates -> {
          UpdateAccessor updateAccessor = AccessorFactory.getAccessorFor(Update.class);
      for (App app : listAppsUpdates.getList()) {
        Update update = updateAccessor.get(app.getPackageName()).toBlocking().first();
        if (update == null || !update.isExcluded()) {
          updateAccessor.save(new Update(app));
        }
      }

      if (successRequestListener != null) {
        successRequestListener.call(listAppsUpdates);
      }
    }, Throwable::printStackTrace, true);
  }
}
