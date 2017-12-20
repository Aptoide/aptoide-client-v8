/*
 * Copyright (c) 2016.
 * Modified on 04/07/2016.
 */

package cm.aptoide.pt.app.view.displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.store.StoreCredentialsProvider;

/**
 * Created on 30/06/16.
 */
public class AppViewRateAndCommentsDisplayable extends AppViewDisplayable {

  private StoreCredentialsProvider storeCredentialsProvider;
  private InstalledRepository installedRepository;

  public AppViewRateAndCommentsDisplayable() {
  }

  public AppViewRateAndCommentsDisplayable(GetApp getApp,
      StoreCredentialsProvider storeCredentialsProvider, AppViewAnalytics appViewAnalytics,
      InstalledRepository installedRepository) {
    super(getApp, appViewAnalytics);
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.installedRepository = installedRepository;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_rate_and_comment;
  }

  public BaseRequestWithStore.StoreCredentials getStoreCredentials() {
    return storeCredentialsProvider.get(getPojo().getNodes()
        .getMeta()
        .getData()
        .getStore()
        .getName());
  }

  public InstalledRepository getInstalledRepository() {
    return this.installedRepository;
  }
}
