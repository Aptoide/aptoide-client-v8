/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/01/2017.
 */

package cm.aptoide.pt.v8engine.billing;

import rx.Observable;
import rx.Single;

public interface Payer {

  Single<String> getId();

  Observable<Boolean> isAuthenticated();
}
