/*
 * Copyright (c) 2016.
 * Modified on 23/08/2016.
 */

package cm.aptoide.pt.view.fragment;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

/**
 * Created on 12/05/16.
 * <p>
 * User interface component (activity or fragment) basic methods to bind a {@link View} to this
 * component, load extras from a {@link Bundle} and setup
 * methods.
 * </p>
 */
public interface UiComponent {

  @LayoutRes int getContentViewId();

  /**
   * Bind needed views.
   */
  void bindViews(@Nullable View view);

  void loadExtras(@Nullable Bundle extras);

  /**
   * Setup previously binded views.
   */
  void setupViews();

  /**
   * Setup the toolbar, if present.
   */
  void setupToolbar();
}
