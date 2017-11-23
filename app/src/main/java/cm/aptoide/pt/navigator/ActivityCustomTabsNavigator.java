package cm.aptoide.pt.navigator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import cm.aptoide.pt.view.BaseActivity;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

public abstract class ActivityCustomTabsNavigator extends BaseActivity
    implements CustomTabsNavigator {

  private PublishRelay<Uri> results;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    results = PublishRelay.create();
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    results.call(intent.getData());
  }

  @Override public void navigateToCustomTabs(CustomTabsIntent intent, Uri uri) {
    intent.launchUrl(this, uri);
  }

  @Override public Observable<Uri> customTabResults() {
    return results;
  }
}