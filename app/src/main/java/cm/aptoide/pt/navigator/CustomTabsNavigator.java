package cm.aptoide.pt.navigator;

import android.net.Uri;
import androidx.browser.customtabs.CustomTabsIntent;
import rx.Observable;

public interface CustomTabsNavigator {

  void navigateToCustomTabs(CustomTabsIntent intent, Uri uri);

  Observable<Uri> customTabResults();
}
