package cm.aptoide.pt.v8engine.link.customtabs;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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

import cm.aptoide.pt.v8engine.V8Engine;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by jdandrade on 02/09/16.
 */
public class CustomTabsHelper {

  private static final String CHROME_PACKAGE = "com.android.chrome";
  private CustomTabsServiceConnection ctConnection;
  private CustomTabsSession customTabsSession;
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

  public void openInChromeCustomTab(String url, Context context) {
    CustomTabsIntent.Builder builder = getBuilder(context);
    CustomTabsIntent customTabsIntent = builder.build();
    addRefererHttpHeader(context, customTabsIntent);
    customTabsIntent.launchUrl((Activity) context, Uri.parse(url));
  }

  private void addRefererHttpHeader(Context context, CustomTabsIntent customTabsIntent) {
    Bundle httpHeaders = new Bundle();
    httpHeaders.putString("Referer", "http://m.aptoide.com");
    customTabsIntent.intent.putExtra(Browser.EXTRA_HEADERS, httpHeaders);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
      customTabsIntent.intent.putExtra(Intent.EXTRA_REFERRER,
          Uri.parse(Intent.URI_ANDROID_APP_SCHEME + "//" + context.getPackageName()));
    }
  }

  @NonNull public CustomTabsIntent.Builder getBuilder(Context context) {
    Intent openInNativeIntent = new Intent(V8Engine.getContext(), CustomTabNativeReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(V8Engine.getContext(), 0, openInNativeIntent, 0);
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

  private CustomTabsSession getCustomTabsSession() {
    return this.customTabsSession;
  }
}
