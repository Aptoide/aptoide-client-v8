/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.downloadmanager;

import java.io.File;

import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.downloadmanager.interfaces.DownloadSettingsInterface;
import cm.aptoide.pt.utils.FileUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import lombok.Cleanup;

/**
 * Created by trinkes on 7/7/16.
 */
public class CacheHelper {

	public static void cleanCache(DownloadSettingsInterface settingsInterface, String cacheDirPath) {
		long maxCacheSize = settingsInterface.getMaxCacheSize() * 1024 * 1024 * 8;
		@Cleanup Realm realm = DeprecatedDatabase.get();
		RealmResults<Download> allSorted = realm.where(Download.class).findAllSorted("timeStamp", Sort.ASCENDING);

		int i = 0;
		while (i < allSorted.size() - 1 && FileUtils.dirSize(new File(cacheDirPath)) > maxCacheSize) {
			Download download = allSorted.get(i);
			for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
				FileUtils.removeFile(fileToDownload.getFilePath());
			}
			DeprecatedDatabase.delete(download, realm);
			i++;
		}
	}
}
