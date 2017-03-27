/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.dataprovider;

import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.preferences.Application;
import rx.Single;

/**
 * Created by neuro on 20-04-2016.
 */
public abstract class DataProvider extends Application {
  private static TokenInvalidator tokenInvalidator;

  public static Single<String> invalidateAccessToken() {
    return tokenInvalidator.invalidateAccessToken();
  }

  @Override public void onCreate() {
    super.onCreate();
    tokenInvalidator = getTokenInvalidator();
  }

  abstract protected TokenInvalidator getTokenInvalidator();
}
