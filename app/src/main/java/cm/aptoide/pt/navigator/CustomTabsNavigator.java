package cm.aptoide.pt.navigator;

import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import rx.Observable;

public interface CustomTabsNavigator {

  void navigateToCustomTabs(CustomTabsIntent intent, Uri uri);

  Observable<Uri> customTabResults();
}
