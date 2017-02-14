package cm.aptoide.pt.v8engine.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.navigation.NavigationManagerV4;
import cm.aptoide.pt.util.ScreenTrackingUtils;
import cm.aptoide.pt.v8engine.interfaces.UiComponentBasics;
import rx.functions.Action0;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class SupportV4BaseFragment extends FragmentView
    implements UiComponentBasics, PermissionRequest {

  private NavigationManagerV4 appNav;

  @Partners @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      loadExtras(getArguments());
    }
    ScreenTrackingUtils.getInstance().incrementNumberOfScreens();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    bindViews(view);
    setupViews();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    ScreenTrackingUtils.getInstance().decrementNumberOfScreens();
  }

  /**
   * Called after onCreate. This is where arguments should be loaded.
   *
   * @param args {@link #getArguments()}
   */
  @Override public void loadExtras(Bundle args) {
    // optional method
  }

  /**
   * Setup previously binded views.
   */
  public abstract void setupViews();

  @Override public void setupToolbar() {
    // optional method
  }

  @TargetApi(Build.VERSION_CODES.M)
  public void requestAccessToExternalFileSystem(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionRequest) this.getActivity()).requestAccessToExternalFileSystem(
          toRunWhenAccessIsGranted, toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionRequest.class.getName());
    }
  }

  @TargetApi(Build.VERSION_CODES.M)
  public void requestAccessToExternalFileSystem(boolean forceShowRationale,
      @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionRequest) this.getActivity()).requestAccessToExternalFileSystem(forceShowRationale,
          toRunWhenAccessIsGranted, toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionRequest.class.getName());
    }
  }

  @TargetApi(Build.VERSION_CODES.M)
  public void requestAccessToAccounts(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionRequest) this.getActivity()).requestAccessToAccounts(toRunWhenAccessIsGranted,
          toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionRequest.class.getName());
    }
  }

  @TargetApi(Build.VERSION_CODES.M) public void requestAccessToAccounts(boolean forceShowRationale,
      @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionRequest) this.getActivity()).requestAccessToAccounts(forceShowRationale,
          toRunWhenAccessIsGranted, toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionRequest.class.getName());
    }
  }

  public void requestDownloadAccess(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    try {
      ((PermissionRequest) this.getActivity()).requestDownloadAccess(toRunWhenAccessIsGranted,
          toRunWhenAccessIsDenied);
    } catch (ClassCastException e) {
      throw new IllegalStateException("Containing activity of this fragment must implement "
          + PermissionRequest.class.getName());
    }
  }

  @Override public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser) {
      ScreenTrackingUtils.getInstance().addScreenToHistory(getClass().getSimpleName());
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    appNav = NavigationManagerV4.Builder.buildWith(getActivity());
    return inflater.inflate(getContentViewId(), container, false);
  }

  protected NavigationManagerV4 getNavigationManager() {
    return appNav;
  }
}
