/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 28/06/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 05-05-2016.
 */
public abstract class AptoideFragmentActivity extends UIComponentActivity {



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
}
