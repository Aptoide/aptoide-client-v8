/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/01/2017.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.accountmanager.AptoideAccountManager;
import rx.Single;

public interface Payer {

  public Single<String> getId();

}
