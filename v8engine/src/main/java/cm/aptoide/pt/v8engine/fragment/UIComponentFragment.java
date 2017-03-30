package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.util.ScreenTrackingUtils;
import cm.aptoide.pt.v8engine.interfaces.UiComponent;
import cm.aptoide.pt.v8engine.view.PermissionServiceFragment;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class UIComponentFragment extends PermissionServiceFragment implements UiComponent {

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
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

  @Override public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser) {
      ScreenTrackingUtils.getInstance().addScreenToHistory(getClass().getSimpleName());
    }
  }

  @CallSuper @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(getContentViewId(), container, false);
  }
}
