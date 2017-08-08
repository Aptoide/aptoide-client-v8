package cm.aptoide.pt.remotebootconfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.PartnersSecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.remotebootconfig.datamodel.RemoteBootConfig;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by diogoloureiro on 02/03/2017.
 *
 * basic utils read and save the boot config JSON file
 */

public class BootConfigJSONUtils {

  private static final String TAG = BootConfigJSONUtils.class.getSimpleName();

  /**
   * get remote boot config that was saved
   */
  public static RemoteBootConfig getSavedRemoteBootConfig(Context context) {
    if (isSavedRemoteBootConfigAvailable(getSharedPreferences(context))) {
      Gson gson = new Gson();
      String json =
          PartnersSecurePreferences.getRemoteBootConfigJSONString(getSharedPreferences(context));
      return gson.fromJson(json, RemoteBootConfig.class);
    } else {
      return getLocalBootConfig(context);
    }
  }

  /**
   * save JSON response in shared preferences as a string
   *
   * @param context context
   * @param remoteBootConfig remote boot config object to save as a shared preference
   */
  public static void saveRemoteBootConfig(Context context, RemoteBootConfig remoteBootConfig) {
    if (isSavedRemoteBootConfigAvailable(getSharedPreferences(context))) {
      if (isRemoteBootConfigValid(remoteBootConfig, getSavedRemoteBootConfig(context))) {
        saveBootConfigToSharedPreferences(remoteBootConfig, getSharedPreferences(context));
      } else {
        Logger.d(TAG, "can't save it, something is wrong with it");
      }
    } else {
      getLocalBootConfig(context);
    }
  }

  /**
   * get local raw .json boot config
   */
  private static RemoteBootConfig getLocalBootConfig(Context context) {
    RemoteBootConfig remoteBootConfig =
        new Gson().fromJson(readJSONBootConfigFromRawFile(context).toString(),
            RemoteBootConfig.class);
    saveBootConfigToSharedPreferences(remoteBootConfig, getSharedPreferences(context));
    return remoteBootConfig;
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
      SharedPreferences sharedPreferences) {
    Gson gson = new Gson();
    String json = gson.toJson(remoteBootConfig);
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
   * @return true if boot config is valid
   */
  private static boolean isRemoteBootConfigValid(RemoteBootConfig newRemoteBootConfig,
      RemoteBootConfig oldRemoteBootConfig) {
    if (newRemoteBootConfig.getData().getPartner().getUid() == null
        || newRemoteBootConfig.getData().getPartner().getStore().getName() == null
        || newRemoteBootConfig.getData().getPartner().getStore().getLabel() == null
        || newRemoteBootConfig.getData().getPartner().getAppearance().getTheme() == null) {
      CrashReport.getInstance()
          .log(new JSONException(
              "Remote boot config response returning null: Uid/storeName/Label/Theme"));
      return false;
    }
    if (newRemoteBootConfig.getData().getPartner().getUid().isEmpty()
        || newRemoteBootConfig.getData().getPartner().getStore().getName().isEmpty()
        || newRemoteBootConfig.getData().getPartner().getStore().getLabel().isEmpty()
        || newRemoteBootConfig.getData().getPartner().getAppearance().getTheme().isEmpty()) {
      CrashReport.getInstance()
          .log(new JSONException(
              "Remote boot config response returning empty Uid/storeName/Label/Theme"));
      return false;
    }
    if (!newRemoteBootConfig.getData()
        .getPartner()
        .getUid()
        .equals(oldRemoteBootConfig.getData().getPartner().getUid())) {
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
        .equals(oldRemoteBootConfig.getData().getPartner().getStore().getName())) {
      CrashReport.getInstance()
          .log(new JSONException(
              "Careful the new boot config tried to change the store name! For security reasons,"
                  + " the new boot config will not be saved on the device"));
      return false;
    }
    if (newRemoteBootConfig.getData().getPartner().getFeedback().getEmail() == null
        || newRemoteBootConfig.getData().getPartner().getFeedback().getEmail().isEmpty()) {
      newRemoteBootConfig.getData().getPartner().getFeedback().setEmail("support@aptoide.com");
    }
    return true;
  }

  /**
   * Read JSON object from raw file present in the assets of the apk.
   * this file is read only, and should be pre-loaded in the res/raw.
   *
   * @param context context it's being called
   */
  private static JSONObject readJSONBootConfigFromRawFile(Context context) {
    JSONObject jsonObject = null;

    try {
      InputStream inputStream = context.getResources().openRawResource(R.raw.boot_config);

      if (inputStream != null) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String receiveString;
        StringBuilder stringBuilder = new StringBuilder();

        while ((receiveString = bufferedReader.readLine()) != null) {
          stringBuilder.append(receiveString);
        }

        inputStream.close();
        jsonObject = new JSONObject(stringBuilder.toString());
      }
    } catch (FileNotFoundException e) {
      Logger.e(TAG, "boot_config.json file not found: " + e.toString());
      CrashReport.getInstance().log(e);
    } catch (IOException e) {
      Logger.e(TAG, "Can not read boot_config.json file: " + e.toString());
      CrashReport.getInstance().log(e);
    } catch (JSONException e) {
      Logger.e(TAG, "Failed to create JSONObject from boot_config.json String: " + e.toString());
      CrashReport.getInstance().log(e);
    }
    return jsonObject;
  }

  /**
   * get shared preferences
   *
   * @param context given context
   * @return secure shared preferences implementation
   */
  private static SharedPreferences getSharedPreferences(Context context) {
    return SecurePreferencesImplementation.getInstance(context,
        PreferenceManager.getDefaultSharedPreferences(context));
  }
}