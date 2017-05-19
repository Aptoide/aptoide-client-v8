package cm.aptoide.pt.spotandshareandroid;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import cm.aptoide.pt.utils.design.ShowMessage;

public class ShareAptoideActivity extends ActivityView implements ShareAptoideView {

  private Toolbar mToolbar;
  private LinearLayout shareAptoideLinearLayout;
  private ShareAptoidePresenter presenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_share_aptoide);

    bindViews();

    presenter = new ShareAptoidePresenter(this,
        new ShareAptoideManager(new HotspotManager(getApplicationContext())));
    attachPresenter(presenter);
  }

  private void bindViews() {
    mToolbar = (Toolbar) findViewById(R.id.shareAppsToolbar);
    shareAptoideLinearLayout = (LinearLayout) findViewById(R.id.share_aptoide_layout);
    setUpToolbar();
  }

  private void setUpToolbar() {
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayShowTitleEnabled(true);
      getSupportActionBar().setTitle(getResources().getString(R.string.spot_share));
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int i = item.getItemId();

    presenter.pressedBack();

    return super.onOptionsItemSelected(item);
  }

  @Override public void buildBackDialog() {

  }

  @Override public void showUnsuccessHotspotCreation() {
    buildSnackBar();
  }

  private void buildSnackBar() {
    ShowMessage.asSnack(shareAptoideLinearLayout,
        getResources().getString(R.string.spotandshare_message_create_hotspot_error),
        getResources().getString(R.string.spotandshare_button_retry_enable_hotspot),
        click -> presenter.pressedRetryOpenHotspot(), Snackbar.LENGTH_LONG);
  }
}
