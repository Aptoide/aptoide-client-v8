package cm.aptoide.pt.view.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.view.ThemeUtils;
import cm.aptoide.pt.view.permission.PermissionServiceFragment;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class UIComponentFragment extends PermissionServiceFragment implements UiComponent {

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      loadExtras(getArguments());
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    bindViews(view);
    setupViews();
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

  @CallSuper @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    String storeTheme =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultTheme();
    if (storeTheme != null) {
      ThemeUtils.setStoreTheme(getActivity(), storeTheme);
      ThemeUtils.setStatusBarThemeColor(getActivity(), StoreTheme.get(storeTheme));
    }
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(getContentViewId(), container, false);
  }
}
