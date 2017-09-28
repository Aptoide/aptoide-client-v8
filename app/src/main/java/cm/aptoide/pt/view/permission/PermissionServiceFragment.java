/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.view.permission;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.view.BackButtonFragment;
import rx.functions.Action0;

/**
 * Created by marcelobenites on 18/01/17.
 */

public abstract class PermissionServiceFragment extends BackButtonFragment
    implements PermissionService {

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToAccounts(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionService) this.getActivity()).requestAccessToAccounts(toRunWhenAccessIsGranted,
          toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionService.class.getName());
    }
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToAccounts(boolean forceShowRationale,
      @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionService) this.getActivity()).requestAccessToAccounts(forceShowRationale,
          toRunWhenAccessIsGranted, toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionService.class.getName());
    }
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToContacts(boolean forceShowRationale,
      @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionService) this.getActivity()).requestAccessToContacts(forceShowRationale,
          toRunWhenAccessIsGranted, toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionService.class.getName());
    }
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestDownloadAccess(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionService) this.getActivity()).requestDownloadAccess(toRunWhenAccessIsGranted,
          toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionService.class.getName());
    }
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToCamera(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionService) this.getActivity()).requestAccessToCamera(toRunWhenAccessIsGranted,
          toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionService.class.getName());
    }
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToExternalFileSystem(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionService) this.getActivity()).requestAccessToExternalFileSystem(
          toRunWhenAccessIsGranted, toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionService.class.getName());
    }
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToExternalFileSystem(boolean forceShowRationale,
      @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionService) this.getActivity()).requestAccessToExternalFileSystem(forceShowRationale,
          toRunWhenAccessIsGranted, toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionService.class.getName());
    }
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToExternalFileSystem(boolean forceShowRationale,
      @StringRes int rationaleMessage, @Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionService) this.getActivity()).requestAccessToExternalFileSystem(forceShowRationale,
          rationaleMessage, toRunWhenAccessIsGranted, toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionService.class.getName());
    }
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToLocationAndExternalStorage(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionService) this.getActivity()).requestAccessToLocationAndExternalStorage(
          toRunWhenAccessIsGranted, toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionService.class.getName());
    }
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToWriteSettings(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionService) this.getActivity()).requestAccessToWriteSettings(
          toRunWhenAccessIsGranted, toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionService.class.getName());
    }
  }

  @Override public void requestToEnableLocation(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionService) this.getActivity()).requestToEnableLocation(toRunWhenAccessIsGranted,
          toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionService.class.getName());
    }
  }
}
