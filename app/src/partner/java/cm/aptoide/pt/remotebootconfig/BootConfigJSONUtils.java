package cm.aptoide.pt.remotebootconfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.PartnersSecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.remotebootconfig.datamodel.RemoteBootConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.json.JSONException;

/**
 * Created by diogoloureiro on 11/08/2017.
 *
 * Boot Config JSON utils to extract the boot config from the raw file, as well save and return the
 * remote boot config and maintain it's integrity
 */

public class BootConfigJSONUtils {

  private static final String TAG = BootConfigJSONUtils.class.getSimpleName();

  /**
   * get remote boot config that was saved
   */
  public static RemoteBootConfig getSavedRemoteBootConfig(Context context,
      ObjectMapper objectMapper) {
    if (isSavedRemoteBootConfigAvailable(getSharedPreferences(context))) {
      try {
        final String json =
            PartnersSecurePreferences.getRemoteBootConfigJSONString(getSharedPreferences(context));
        return objectMapper.readValue(json, RemoteBootConfig.class);
      } catch (IOException e) {
        throw new IllegalStateException("Could not load local boot configuration", e);
      }
    } else {
      return getLocalBootConfig(context, objectMapper);
    }
  }

  /**
   * save JSON response in shared preferences as a string
   *
   * @param context context
   * @param remoteBootConfig remote boot config object to save as a shared preference
   * @param feedbackEmail
   * @param objectMapper
   */
  public static void saveRemoteBootConfig(Context context, RemoteBootConfig remoteBootConfig,
      String feedbackEmail, ObjectMapper objectMapper) {
    if (isSavedRemoteBootConfigAvailable(getSharedPreferences(context))) {
      if (isRemoteBootConfigValid(remoteBootConfig, getSavedRemoteBootConfig(context, objectMapper),
          feedbackEmail)) {
        try {
          saveBootConfigToSharedPreferences(remoteBootConfig, getSharedPreferences(context),
              objectMapper);
        } catch (JsonProcessingException ignored) {
          getLocalBootConfig(context, objectMapper);
        }
      } else {
        getLocalBootConfig(context, objectMapper);
      }
    } else {
      getLocalBootConfig(context, objectMapper);
    }
  }

  /**
   * get local raw .json boot config
   */
  private static RemoteBootConfig getLocalBootConfig(Context context, ObjectMapper objectMapper) {
    try {
      final RemoteBootConfig remoteBootConfig = objectMapper.readValue(context.getResources()
          .openRawResource(R.raw.boot_config), RemoteBootConfig.class);
      saveBootConfigToSharedPreferences(remoteBootConfig, getSharedPreferences(context),
          objectMapper);
      return remoteBootConfig;
    } catch (IOException e) {
      throw new IllegalStateException("Could not load local boot configuration", e);
    }
  }

  /**
   * check if a remote boot config is available
   *
   * @return true if is available
   */
  private static boolean isSavedRemoteBootConfigAvailable(SharedPreferences sharedPreferences) {
    String bootConfigJson =
        PartnersSecurePreferences.getRemoteBootConfigJSONString(sharedPreferences);
    return !bootConfigJson.isEmpty();
  }

  /**
   * saves the boot config to the shared preferences
   */
  private static void saveBootConfigToSharedPreferences(RemoteBootConfig remoteBootConfig,
      SharedPreferences sharedPreferences, ObjectMapper objectMapper)
      throws JsonProcessingException {
    final String json = objectMapper.writeValueAsString(remoteBootConfig);
    PartnersSecurePreferences.setRemoteBootConfigJSONString(json, sharedPreferences);
    Logger.d(TAG + " saved ", json);
  }

  /**
   * @param newRemoteBootConfig new boot config
   * @param oldRemoteBootConfig old boot config
   *
   * Uid can't be empty/null/changed
   * storeName can't be empty/null/changed
   * label can't be empty/null
   * theme can't be empty/null
   *
   * if email is null or empty, will be replaced by the default value
   * @param feedbackEmail
   *
   * @return true if boot config is valid
   */
  private static boolean isRemoteBootConfigValid(RemoteBootConfig newRemoteBootConfig,
      RemoteBootConfig oldRemoteBootConfig, String feedbackEmail) {
    if (newRemoteBootConfig.getData()
        .getPartner()
        .getUid() == null
        || newRemoteBootConfig.getData()
        .getPartner()
        .getStore()
        .getName() == null
        || newRemoteBootConfig.getData()
        .getPartner()
        .getStore()
        .getLabel() == null
        || newRemoteBootConfig.getData()
        .getPartner()
        .getAppearance()
        .getTheme() == null) {
      CrashReport.getInstance()
          .log(new JSONException(
              "Remote boot config response returning null: Uid/storeName/Label/Theme"));
      return false;
    }
    if (newRemoteBootConfig.getData()
        .getPartner()
        .getUid()
        .isEmpty() || newRemoteBootConfig.getData()
        .getPartner()
        .getStore()
        .getName()
        .isEmpty() || newRemoteBootConfig.getData()
        .getPartner()
        .getStore()
        .getLabel()
        .isEmpty() || newRemoteBootConfig.getData()
        .getPartner()
        .getAppearance()
        .getTheme()
        .isEmpty()) {
      CrashReport.getInstance()
          .log(new JSONException(
              "Remote boot config response returning empty Uid/storeName/Label/Theme"));
      return false;
    }
    if (!newRemoteBootConfig.getData()
        .getPartner()
        .getUid()
        .equals(oldRemoteBootConfig.getData()
            .getPartner()
            .getUid())) {
      CrashReport.getInstance()
          .log(new JSONException(
              "Careful the new boot config tried to change the UID! For security reasons,"
                  + " the new boot config will not be saved on the device"));
      return false;
    }

    if (!newRemoteBootConfig.getData()
        .getPartner()
        .getStore()
        .getName()
        .equals(oldRemoteBootConfig.getData()
            .getPartner()
            .getStore()
            .getName())) {
      CrashReport.getInstance()
          .log(new JSONException(
              "Careful the new boot config tried to change the store name! For security reasons,"
                  + " the new boot config will not be saved on the device"));
      return false;
    }
    if (newRemoteBootConfig.getData()
        .getPartner()
        .getFeedback()
        .getEmail() == null || newRemoteBootConfig.getData()
        .getPartner()
        .getFeedback()
        .getEmail()
        .isEmpty()) {
      newRemoteBootConfig.getData()
          .getPartner()
          .getFeedback()
          .setEmail(feedbackEmail);
    }
    return true;
  }

  /**
   * get shared preferences
   *
   * @param context given context
   *
   * @return secure shared preferences implementation
   */
  private static SharedPreferences getSharedPreferences(Context context) {
    return SecurePreferencesImplementation.getInstance(context,
        PreferenceManager.getDefaultSharedPreferences(context));
  }
}