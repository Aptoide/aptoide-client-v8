package cm.aptoide.pt.v8engine.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.Translator;

/**
 * Created by trinkes on 16/12/2016.
 */

public class GridRecyclerSwipeWithToolbarFragment extends GridRecyclerSwipeFragment {
  public static final String TITLE_KEY = "TITLE_KEY";
  private String title;

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(Translator.translate(title));
    toolbar.setLogo(R.drawable.logo_toolbar);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    title = args.getString(TITLE_KEY);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(TITLE_KEY, title);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_empty, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public int getContentViewId() {
    return R.layout.recycler_swipe_fragment_with_toolbar;
  }
}
