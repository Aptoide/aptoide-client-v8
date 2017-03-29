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
public abstract class BaseToolbarFragment extends UIComponentFragment {

  private Toolbar toolbar;

  protected Toolbar getToolbar() {
    return toolbar;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    toolbar = null;
  }

  @Override public void setupViews() {
    setupToolbar();
  }

  protected boolean hasToolbar() {
    return toolbar != null;
  }

  protected boolean displayHomeUpAsEnabled() {
    return false;
  }

  protected void setupToolbarDetails(Toolbar toolbar) {
    // does nothing. placeholder method.
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
      actionBar.setTitle(toolbar.getTitle());
      setupToolbarDetails(toolbar);
    }
  }

  @Override public void bindViews(View view) {
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
  }
}
