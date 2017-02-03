package cm.aptoide.pt.v8engine.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.UiComponentBasics;

/**
 * Created by neuro on 06-05-2016.
 */
public abstract class AptoideSimpleFragmentActivity extends AptoideBaseActivity
    implements UiComponentBasics {

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.fragment_placeholder, createFragment())
          .commit();
    }
  }

  protected abstract Fragment createFragment();

  protected Fragment getCurrentFragment() {
    if (getSupportFragmentManager().getFragments() != null
        && getSupportFragmentManager().getFragments().size() > 0) {
      return getSupportFragmentManager().getFragments()
          .get(getSupportFragmentManager().getFragments().size() - 1);
    } else {
      return null;
    }
  }

  @Override public void bindViews(View view) {
    // does nothing
  }

  @Override public void loadExtras(Bundle extras) {
    // does nothing
  }

  @Override public void setupViews() {
    // does nothing
  }

  @Override public void setupToolbar() {
    // does nothing
  }

  @Override public int getContentViewId() {
    return R.layout.frame_layout;
  }

  @Override protected String getAnalyticsScreenName() {
    return null;
  }
}
