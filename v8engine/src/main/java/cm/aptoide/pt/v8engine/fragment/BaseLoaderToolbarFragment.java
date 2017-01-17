/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 22/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.support.annotation.CallSuper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 06-05-2016.
 */
public abstract class BaseLoaderToolbarFragment extends BaseLoaderFragment {

  private Toolbar toolbar;

  protected Toolbar getToolbar() {
    return toolbar;
  }

  protected boolean hasToolbar() {
    return toolbar != null;
  }

  @CallSuper @Override public void setupViews() {
    setupToolbar();
  }

  /**
   * Setup the toolbar, if present.
   */
  @CallSuper @Override public void setupToolbar() {
    if (hasToolbar()) {
      ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
      boolean showUp = displayHomeUpAsEnabled();

      ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
      actionBar.setDisplayHomeAsUpEnabled(showUp);

      setupToolbarDetails(toolbar);
    }
  }

  protected boolean displayHomeUpAsEnabled() {
    return false;
  }

  protected void setupToolbarDetails(Toolbar toolbar) {
    // does nothing. placeholder method.
  }

  @CallSuper @Override public void onDestroyView() {
    super.onDestroyView();
    toolbar = null;
  }

  @CallSuper @Override public void bindViews(View view) {
    super.bindViews(view);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
  }
}
