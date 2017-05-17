package cm.aptoide.pt.v8engine.view.timeline;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.fragment.BaseLoaderToolbarFragment;

/**
 * Created by trinkes on 7/11/16.
 */
public class SocialFragment extends BaseLoaderToolbarFragment {

  public static final String SOCIAL_URL = "Social URL";
  public static final String PAGE_TITLE = "Social Page Title";
  private WebView socialWebview;
  private String socialUrl;
  private String pageTitle;

  public static SocialFragment newInstance(String socialUrl, String pageTitle) {
    SocialFragment socialFragment = new SocialFragment();
    Bundle extras = new Bundle();
    extras.putString(SOCIAL_URL, socialUrl);
    extras.putString(PAGE_TITLE, pageTitle);
    socialFragment.setArguments(extras);
    return socialFragment;
  }

  @Override public int getContentViewId() {
    return R.layout.social_fragment_layout;
  }

  @Override protected int[] getViewsToShowAfterLoadingId() {
    return new int[] {};
  }

  @Override protected int getViewToShowAfterLoadingId() {
    return R.id.social_fragment_layout;
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    socialWebview.loadUrl(socialUrl);
  }

  @Override public void loadExtras(Bundle args) {
    socialUrl = args.getString(SOCIAL_URL);
    pageTitle = args.getString(PAGE_TITLE);
  }

  @Override public void setupViews() {
    super.setupViews();
    socialWebview.setWebChromeClient(new WebChromeClient() {
    });
    socialWebview.setWebViewClient(new WebViewClient() {
      @Override public void onPageFinished(WebView view, String url) {
        finishLoading();
      }
    });
    setHasOptionsMenu(true);
    socialWebview.getSettings()
        .setJavaScriptEnabled(true);
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(pageTitle);
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    socialWebview = (WebView) view.findViewById(R.id.social_fragment_layout);
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_empty, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
