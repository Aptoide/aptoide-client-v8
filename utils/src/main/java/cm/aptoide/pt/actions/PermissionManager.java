/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.actions;

import rx.Observable;

/**
 * Created by marcelobenites on 7/25/16.
 */
public class PermissionManager {

  public Observable<Void> requestExternalStoragePermission(PermissionService permissionService) {
    return Observable.create(new RequestAccessToExternalFileSystemOnSubscribe(permissionService));
  }

  public Observable<Void> requestDownloadAccess(PermissionService permissionService) {
    return Observable.create(new RequestDownloadAccessOnSubscribe(permissionService, false));
  }

  public Observable<Void> requestDownloadAllowingMobileData(PermissionService permissionService) {
    return Observable.create(new RequestDownloadAccessOnSubscribe(permissionService, true));
  }

  public Observable<Boolean> hasDownloadAccess(PermissionService permissionService) {
    return Observable.create(new HasDownloadAccessOnSubscribe(permissionService));
  }
}
