package cm.aptoide.pt.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.V8Engine;
import cm.aptoide.pt.VanillaConfiguration;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.remotebootconfig.BootConfigJSONUtils;
import cm.aptoide.pt.remotebootconfig.BootConfigServices;
import cm.aptoide.pt.remotebootconfig.datamodel.RemoteBootConfig;
import cm.aptoide.pt.view.entry.EntryActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by diogoloureiro on 31/05/2017.
 */

public class PartnersLaunchActivity extends ActivityView {

  private Intent intent;
  private boolean usesSplashScreen;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    usesSplashScreen = ((VanillaConfiguration) Application.getConfiguration()).getBootConfig()
        .getPartner()
        .getAppearance()
        .getSplash()
        .isEnable();
    if (usesSplashScreen) {
      setContentView(R.layout.partners_launch);
      ImageLoader.with(this)
          .load(((VanillaConfiguration) Application.getConfiguration()).getBootConfig()
              .getPartner()
              .getAppearance()
              .getSplash()
              .getPortrait(), (ImageView) findViewById(R.id.splashscreen));
    }
    intent = getIntent();
    getBootConfigRequest(this);
  }

  /**
   * gets the Remote bootconfig from the webservice.
   */
  private void getBootConfigRequest(Context context) {
    Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_V7_HOST
        + "/api/7/client/boot/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(((V8Engine) context.getApplicationContext()).getDefaultClient())
        .build();
    Call<RemoteBootConfig> call = retrofit.create(BootConfigServices.class)
        .getRemoteBootConfig(Application.getConfiguration().getAppId(),
            Application.getConfiguration().getVerticalDimension(),
            Application.getConfiguration().getPartnerId(),
            String.valueOf(BuildConfig.VERSION_CODE));
    call.enqueue(new Callback<RemoteBootConfig>() {
      @Override
      public void onResponse(Call<RemoteBootConfig> call, Response<RemoteBootConfig> response) {
        if (response.body() != null) {
          BootConfigJSONUtils.saveRemoteBootConfig(context, response.body());
          ((VanillaConfiguration) Application.getConfiguration()).setBootConfig(
              BootConfigJSONUtils.getSavedRemoteBootConfig(context).getData());
        }
        startMainActivityPartners();
      }

      @Override public void onFailure(Call<RemoteBootConfig> call, Throwable t) {
        Logger.e("PartnersConfiguration",
            "Failed to get First remote boot config: " + t.getMessage());
        startMainActivityPartners();
      }
    });
  }

  /**
   * startMainActivityPartners
   */
  private void startMainActivityPartners() {
    if (usesSplashScreen) {
      new java.util.Timer().schedule(new java.util.TimerTask() {
        @Override public void run() {
          startActivity();
        }
      }, ((VanillaConfiguration) Application.getConfiguration()).getBootConfig()
          .getPartner()
          .getAppearance()
          .getSplash()
          .getTimeout() * 1000);
    } else {
      startActivity();
    }
  }

  private void startActivity() {
    Intent i = new Intent(this, EntryActivity.class);
    if (intent.getExtras() != null) {
      i.putExtras(intent.getExtras());
    }
    startActivity(i);
    finish();
  }
}