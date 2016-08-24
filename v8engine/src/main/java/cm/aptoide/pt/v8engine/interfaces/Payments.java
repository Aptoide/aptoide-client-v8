/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 23/08/2016.
 */

package cm.aptoide.pt.v8engine.interfaces;

import cm.aptoide.pt.model.v7.GetAppMeta;

/**
 * Created by sithengineer on 29/07/16.
 * <p>
 * Has the method {@link #buyApp(GetAppMeta.App)} to expose the asynchronous behaviour of buying an application and other necessary payment behaviours.
 * </p>
 */
public interface Payments {
	void buyApp(GetAppMeta.App app);
}
