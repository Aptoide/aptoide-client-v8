/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.v8engine.install;

import rx.Observable;

/**
 * Created by marcelobenites on 7/25/16.
 */
public interface InstallationProvider {

	Observable<RollbackInstallation> getInstallation(long id);

}
