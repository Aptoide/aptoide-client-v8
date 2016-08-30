/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.install.exception;

/**
 * Created by marcelobenites on 7/20/16.
 */
public class InstallationException extends Exception {

	public InstallationException(String detailMessage) {
		super(detailMessage);
	}

	public InstallationException(Throwable throwable) {
		super(throwable);
	}
}
