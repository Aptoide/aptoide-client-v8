/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/07/2016.
 */

package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

import cm.aptoide.pt.utils.AptoideUtils;
import rx.Observable;

/**
 * Created by marcelobenites on 7/18/16.
 */
public class InstallManager {

	public void install(Context context, File apkFile) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public void uninstall(Context context, String packageName) {
		Uri uri = Uri.fromParts("package", packageName, null);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		context.startActivity(intent);
	}
}