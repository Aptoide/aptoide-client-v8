package cm.aptoide.pt.v8engine.timeline.link;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.widget.Toast;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jdandrade on 06/09/16.
 */
public class CustomTabNativeReceiver extends BroadcastReceiver {
  private static final String MOCKUP_URL = "http://www.example.com";
  private static final String REFERER_ATTRIBUTE = "Referer";
  private static final String REFERER_VALUE = "http://m.aptoide.com";

  @Override public void onReceive(Context context, Intent intent) {
    String url = intent.getDataString();

    if (url != null) {
      Set<String> listOfPackagesThatResolveUri = getNativeAppPackage(context, Uri.parse(url));
      String availableNativeAppPackageName = null;
      if (listOfPackagesThatResolveUri.iterator()
          .hasNext()) {
        availableNativeAppPackageName = listOfPackagesThatResolveUri.iterator()
            .next();
      }

      if (availableNativeAppPackageName != null) {
        Intent launchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        Bundle httpHeaders = new Bundle();
        httpHeaders.putString(REFERER_ATTRIBUTE, REFERER_VALUE);
        launchIntent.putExtra(Browser.EXTRA_HEADERS, httpHeaders);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launchIntent);
      } else {
        Toast.makeText(context, "No application to open.", Toast.LENGTH_SHORT)
            .show();
      }
    }
  }

  private Set<String> getNativeAppPackage(Context context, Uri uri) {
    PackageManager pm = context.getPackageManager();

    Intent browserActivityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MOCKUP_URL));
    Set<String> genericResolvedList =
        extractPackagenames(pm.queryIntentActivities(browserActivityIntent, 0));

    Intent specializedActivityIntent = new Intent(Intent.ACTION_VIEW, uri);
    Set<String> resolvedSpecializedList =
        extractPackagenames(pm.queryIntentActivities(specializedActivityIntent, 0));

    resolvedSpecializedList.removeAll(genericResolvedList);

    return resolvedSpecializedList;
  }

  private Set<String> extractPackagenames(List<ResolveInfo> resolveInfos) {
    Set<String> packageNameSet = new HashSet<>();
    for (ResolveInfo ri : resolveInfos) {
      packageNameSet.add(ri.activityInfo.packageName);
    }
    return packageNameSet;
  }
}
