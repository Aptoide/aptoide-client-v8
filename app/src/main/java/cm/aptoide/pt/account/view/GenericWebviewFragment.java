package cm.aptoide.pt.account.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.fragment.BaseToolbarFragment;

/**
 * Created by franciscocalado on 28/03/18.
 */

public class GenericWebviewFragment extends BaseToolbarFragment implements NotBottomNavigationView {

  private String url;
  private String title;

  public static GenericWebviewFragment newInstance(String url, String title) {
    Bundle args = new Bundle();
    args.putString("url", url);
    args.putString("title", title);
    GenericWebviewFragment fragment = new GenericWebviewFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    WebView webView = (WebView) view.findViewById(R.id.webview);
    webView.loadUrl(url);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    url = args.getString("url");
    title = args.getString("title");
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    super.setupToolbarDetails(toolbar);
    toolbar.setTitle(title);
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_generic_webview;
  }
}
