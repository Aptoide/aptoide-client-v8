package cm.aptoide.pt.v8engine.timeline.link;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;

/**
 * Created by jdandrade on 02/09/16.
 */
public class CustomTabsHelper {

  private static final String CHROME_PACKAGE = "com.android.chrome";
  private static CustomTabsHelper customTabsHelper;
  private CustomTabsServiceConnection ctConnection;
  private CustomTabsSession customTabsSession;

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

  public void setUpCustomTabsService(String url, Context context) {
    ctConnection = new CustomTabsServiceConnection() {
      @Override public void onCustomTabsServiceConnected(ComponentName componentName,
          CustomTabsClient customTabsClient) {
        customTabsClient.warmup(0);
        customTabsSession = getSession(customTabsClient);
        customTabsSession.mayLaunchUrl(Uri.parse(url), null, null);
      }

      @Override public void onServiceDisconnected(ComponentName name) {
      }
    };
    CustomTabsClient.bindCustomTabsService(context, CHROME_PACKAGE, ctConnection);
  }

  private CustomTabsSession getSession(CustomTabsClient customTabsClient) {
    if (customTabsClient != null) {
      return customTabsClient.newSession(new CustomTabsCallback() {
      });
    }
    return null;
  }

  /**
   * <p>Launches specified url in a Chrome Custom Tab using no warm-up.</p>
   * <p>Referrers are injected into the intent before launching.</p>
   * <p>If the device/Android version doesn't support Chrome Custom Tabs,
   * it will launch the intent for the user to choose where he wants to open the url.</p>
   *
   * <p>The Custom Chrome tab is customized with an orange Action Bar,
   * a Share Url option (share intent),
   * an enter/exit slide animation and
   * a overflow menu item ("Open in App"), that allows user to open the url in a native application
   * that can handle those kind of urls (excluding browsers).</p>
   *
   * @param url Url to be launched in the Custom Chrome Tab
   * @param context Context
   */
  public void openInChromeCustomTab(String url, Context context) {
    CustomTabsIntent.Builder builder = getBuilder(context);
    CustomTabsIntent customTabsIntent = builder.build();
    addRefererHttpHeader(context, customTabsIntent);
    customTabsIntent.launchUrl((Activity) context, Uri.parse(url));
  }

  @NonNull private CustomTabsIntent.Builder getBuilder(Context context) {
    Intent openInNativeIntent = new Intent(V8Engine.getContext(), CustomTabNativeReceiver.class);
    PendingIntent pendingIntent =
        PendingIntent.getBroadcast(V8Engine.getContext(), 0, openInNativeIntent, 0);
    return new CustomTabsIntent.Builder(getCustomTabsSession()).setToolbarColor(
        ContextCompat.getColor(context, R.color.aptoide_orange))
        .setShowTitle(true)
        .setCloseButtonIcon(
            BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_back))
        .addDefaultShareMenuItem()
        .addMenuItem(context.getString(R.string.customtabs_open_native_app), pendingIntent)
        .setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
        .setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right);
  }

  /**
   * Injects Referrers to the intent so they can be extracted by the url source.
   * This way the url source can see that we are generating traffic to their page.
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

  private CustomTabsSession getCustomTabsSession() {
    return this.customTabsSession;
  }
}
