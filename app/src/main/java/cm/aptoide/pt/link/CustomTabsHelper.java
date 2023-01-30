package cm.aptoide.pt.link;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Browser;
import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import cm.aptoide.pt.R;

/**
 * Created by jdandrade on 02/09/16.
 */
public class CustomTabsHelper {

  private static CustomTabsHelper customTabsHelper;

  private CustomTabsHelper() {
    if (customTabsHelper != null) {
      throw new IllegalStateException("Already instantiated");
    }
  }

  public static CustomTabsHelper getInstance() {
    if (customTabsHelper == null) {
      customTabsHelper = new CustomTabsHelper();
    }
    return customTabsHelper;
  }

  /**
   * <p>Launches specified url in a Chrome Custom Tab using no warm-up.</p>
   * <p>Referrers are injected into the intent before launching.</p>
   * <p>If the device/Android version doesn't support Chrome Custom Tabs,
   * it will launch the intent for the user to choose where he wants to open the url.</p>
   *
   * <p>The Custom Chrome tab is customized with an orange Action Bar,
   * a Share Url option (share intent), an enter/exit slide animation and a overflow menu item
   * ("Open in App"), that allows user to open the url in a native application that can handle those
   * kind of urls (excluding browsers).</p>
   *
   * @param url     Url to be launched in the Custom Chrome Tab
   * @param context Context
   */
  public void openInChromeCustomTab(String url, Context context, int color) {
    CustomTabsIntent.Builder builder = getBuilder(context, color);
    CustomTabsIntent customTabsIntent = builder.build();
    addRefererHttpHeader(context, customTabsIntent);
    customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    customTabsIntent.launchUrl(context, Uri.parse(url));
  }

  @NonNull private CustomTabsIntent.Builder getBuilder(Context context, int color) {
    Intent openInNativeIntent =
        new Intent(context.getApplicationContext(), CustomTabNativeReceiver.class);
    PendingIntent pendingIntent =
        PendingIntent.getBroadcast(context.getApplicationContext(), 0, openInNativeIntent,
            PendingIntent.FLAG_IMMUTABLE);
    return new CustomTabsIntent.Builder().setToolbarColor(ContextCompat.getColor(context, color))
        .setShowTitle(true)
        .setCloseButtonIcon(
            BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_back))
        .addDefaultShareMenuItem()
        .addMenuItem(context.getString(R.string.customtabs_open_native_app), pendingIntent)
        .setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
        .setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right);
  }

  /**
   * Injects Referrers to the intent so they can be extracted by the url source. This way the url
   * source can see that we are generating traffic to their page.
   */
  private void addRefererHttpHeader(Context context, CustomTabsIntent customTabsIntent) {
    Bundle httpHeaders = new Bundle();
    httpHeaders.putString("Referer", "http://m.aptoide.com");
    customTabsIntent.intent.putExtra(Browser.EXTRA_HEADERS, httpHeaders);
    customTabsIntent.intent.getExtras();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
      customTabsIntent.intent.putExtra(Intent.EXTRA_REFERRER_NAME,
          "android-app://" + context.getPackageName() + "/");
    }
  }
}
