package cm.aptoide.pt.spotandshareandroid;

import android.os.Bundle;

public class ShareAptoideActivity extends ActivityView implements ShareAptoideView {

  private ShareAptoidePresenter presenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_share_aptoide);

    presenter = new ShareAptoidePresenter(this,
        new ShareAptoideManager(new HotspotManager(getApplicationContext())));
    attachPresenter(presenter);
  }
}
