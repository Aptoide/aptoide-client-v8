package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.Translator;

/**
 * Created by trinkes on 16/12/2016.
 */

public class GridRecyclerSwipeWithToolbarFragment extends GridRecyclerSwipeFragment {
  public static final String TITLE_KEY = "TITLE_KEY";
  private String title;

  @Override public void setupToolbar() {
    // It's not calling super cause it does nothing in the middle class}
    // StoreTabGridRecyclerFragment.
    if (toolbar != null) {
      ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
      ((AppCompatActivity) getActivity()).getSupportActionBar()
          .setTitle(Translator.translate(title));
      ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      toolbar.setLogo(R.drawable.ic_aptoide_toolbar);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(TITLE_KEY, title);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    title = args.getString(TITLE_KEY);
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
