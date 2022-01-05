package cm.aptoide.pt.view.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.Translator;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by trinkes on 16/12/2016.
 */

public abstract class GridRecyclerSwipeWithToolbarFragment extends GridRecyclerSwipeFragment {
  public static final String TITLE_KEY = "TITLE_KEY";
  @Inject @Named("marketName") String marketName;
  private String title;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    getFragmentComponent(savedInstanceState).inject(this);
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(Translator.translate(title, getContext().getApplicationContext(), marketName));
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
