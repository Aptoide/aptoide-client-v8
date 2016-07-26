/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/07/2016.
 */

package cm.aptoide.pt.v8engine.install;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by marcelobenites on 7/22/16.
 */
public interface Installation {

	long getId();

	String getPackageName();

	int getVersionCode();

	File getFile();
}