/*
 * Copyright (c) 2016.
 * Modified on 04/07/2016.
 */

package cm.aptoide.pt.v8engine.view.app.displayable;

import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.app.AppViewAnalytics;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
import lombok.Getter;

/**
 * Created on 30/06/16.
 */
public class AppViewRateAndCommentsDisplayable extends AppViewDisplayable {

  private StoreCredentialsProvider storeCredentialsProvider;
  @Getter private InstalledRepository installedRepository;

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
}
