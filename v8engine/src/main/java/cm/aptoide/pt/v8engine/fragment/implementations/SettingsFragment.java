/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 07/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagedKeys;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.CrashReports;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.dialog.AdultDialog;
import cm.aptoide.pt.v8engine.util.SettingsConstants;
import io.realm.Realm;
import java.io.File;
import java.text.DecimalFormat;
import lombok.Cleanup;

/**
 * Created by fabio on 26-10-2015.
 *
 * @author fabio
 * @author sithengineer
 */
public class SettingsFragment extends PreferenceFragmentCompat
    implements SharedPreferences.OnSharedPreferenceChangeListener {

  private static boolean isSetingPIN = false;
  private final String aptoide_path = null;
  private final String icon_path = aptoide_path + "icons/";
  protected Toolbar toolbar;
  private boolean unlocked = false;
  private Context context;

  public static Fragment newInstance() {
    return new SettingsFragment();
  }

  static public boolean deleteDirectory(File path) {
    if (path.exists()) {
      File[] files = path.listFiles();
      if (files == null) {
        return true;
      }
      for (File file : files) {
        if (file.isDirectory()) {
          deleteDirectory(file);
        } else {
          file.delete();
        }
      }
    }
    return true;
  }

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    // TODO
    if (key.equals(ManagedKeys.UPDATES_FILTER_ALPHA_BETA_KEY)) {
      @Cleanup Realm realm = DeprecatedDatabase.get();
      DeprecatedDatabase.dropTable(Update.class, realm);
      DataproviderUtils.checkUpdates();
    }
  }

  @Override public void onCreatePreferences(Bundle bundle, String s) {
    addPreferencesFromResource(R.xml.settings);
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(getActivity());
    sharedPreferences.registerOnSharedPreferenceChangeListener(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    context = getContext();
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);

    final AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
    if (toolbar != null) {
      parentActivity.setSupportActionBar(toolbar);

      toolbar.setTitle(R.string.settings);
      toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());

      ActionBar supportActionBar = parentActivity.getSupportActionBar();
      if (supportActionBar != null) {
        supportActionBar.setDisplayHomeAsUpEnabled(true);
      }
    }
    setupClickHandlers();
  }

  private void settingsResult() {
    getActivity().setResult(Activity.RESULT_OK);
  }

  private void setupClickHandlers() {
    int pin = SecurePreferences.getAdultContentPin();
    final Preference mp = findPreference("Maturepin");
    if (pin != -1) {
      Log.d("PINTEST", "PinBuild");
      mp.setTitle(R.string.remove_mature_pin_title);
      mp.setSummary(R.string.remove_mature_pin_summary);
    }
    mp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override public boolean onPreferenceClick(Preference preference) {
        maturePinSetRemoveClick();
        return true;
      }
    });

    CheckBoxPreference matureChkBox = (CheckBoxPreference) findPreference("matureChkBox");
    if (AptoideAccountManager.isMatureSwitchOn()) {
      matureChkBox.setChecked(true);
    } else {
      matureChkBox.setChecked(false);
    }

    findPreference(SettingsConstants.ADULT_CHECK_BOX).setOnPreferenceClickListener(
        new Preference.OnPreferenceClickListener() {
          @Override public boolean onPreferenceClick(Preference preference) {
            final CheckBoxPreference cb = (CheckBoxPreference) preference;

            if (cb.isChecked()) {
              cb.setChecked(false);
              AdultDialog.buildAreYouAdultDialog(getActivity(),
                  new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                      if (which == DialogInterface.BUTTON_POSITIVE) {
                        cb.setChecked(true);
                        AptoideAccountManager.updateMatureSwitch(true);
                      }
                    }
                  }).show();
            } else {
              Logger.d(AdultDialog.class.getName(), "FLURRY TESTING : LOCK ADULT CONTENT");
              Analytics.AdultContent.lock();
              AptoideAccountManager.updateMatureSwitch(false);
            }

            return true;
          }
        });

    findPreference(SettingsConstants.FILTER_APPS).setOnPreferenceClickListener(
        new Preference.OnPreferenceClickListener() {
          @Override public boolean onPreferenceClick(Preference preference) {
            final CheckBoxPreference cb = (CheckBoxPreference) preference;
            boolean filterApps = false;

            if (cb.isChecked()) {
              cb.setChecked(true);
              filterApps = true;
            } else {
              cb.setChecked(false);
            }

            ManagerPreferences.setHWSpecsFilter(filterApps);

            return true;
          }
        });

    findPreference(SettingsConstants.SHOW_ALL_UPDATES).setOnPreferenceClickListener(
        new Preference.OnPreferenceClickListener() {
          @Override public boolean onPreferenceClick(Preference preference) {
            settingsResult();
            return true;
          }
        });

    findPreference(SettingsConstants.CLEAR_CACHE).setOnPreferenceClickListener(
        new Preference.OnPreferenceClickListener() {
          @Override public boolean onPreferenceClick(Preference preference) {
            if (unlocked) {
              new DeleteDir().execute(new File(icon_path));
            }
            return false;
          }
        });

    findPreference(SettingsConstants.CLEAR_RANK).setOnPreferenceClickListener(
        new Preference.OnPreferenceClickListener() {
          @Override public boolean onPreferenceClick(Preference preference) {
            if (unlocked) {
              new DeleteDir().execute(new File(aptoide_path));
            }
            return false;
          }
        });

    Preference hwSpecs = findPreference(SettingsConstants.HARDWARE_SPECS);

    //findPreference(SettingsConstants.THEME).setOnPreferenceChangeListener(new Preference
    //		.OnPreferenceChangeListener() {
    //	@Override
    //	public boolean onPreferenceChange(Preference preference, Object newValue) {
    //		// FIXME ??
    //		ShowMessage.asSnack(getView(), getString(R.string.restart_aptoide));
    //		return true;
    //	}
    //});

    hwSpecs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override public boolean onPreferenceClick(Preference preference) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(getString(R.string.setting_hwspecstitle));
        alertDialogBuilder.setIcon(android.R.drawable.ic_menu_info_details)
            .setMessage(getString(R.string.setting_sdk_version)
                    + ": "
                    + AptoideUtils.SystemU.getSdkVer()
                    + "\n"
                    +
                    getString(R.string.setting_screen_size)
                    + ": "
                    + AptoideUtils.ScreenU.getScreenSize()
                    +
                    "\n"
                    +
                    getString(R.string.setting_esgl_version)
                    + ": "
                    + AptoideUtils.SystemU.getGlEsVer()
                    + "\n"
                    +
                    getString(R.string.screenCode)
                    + ": "
                    + AptoideUtils.ScreenU.getNumericScreenSize()
                    +
                    "/"
                    + AptoideUtils.ScreenU.getDensityDpi()
                    + "\n"
                    +
                    getString(R.string.cpuAbi)
                    + ": "
                    + AptoideUtils.SystemU.getAbis()
                //                            + (ApplicationAptoide.PARTNERID!=null ? "\nPartner ID:"
                // + ApplicationAptoide.PARTNERID : "")
            )
            .setCancelable(false)
            .setNeutralButton(getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {
                    //                                FlurryAgent.logEvent
                    // ("Setting_Opened_Dialog_Hardware_Filters");
                  }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        return true;
      }
    });

    EditTextPreference maxFileCache =
        (EditTextPreference) findPreference(SettingsConstants.MAX_FILE_CACHE);
    maxFileCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

      @Override public boolean onPreferenceClick(Preference preference) {
        ((EditTextPreference) preference).setText(
            String.valueOf(ManagerPreferences.getCacheLimit()));
        return false;
      }
    });

    Preference about = findPreference(SettingsConstants.ABOUT_DIALOG);
    about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override public boolean onPreferenceClick(Preference preference) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_about, null);
        String versionName = "";

        try {
          versionName = getActivity().getPackageManager()
              .getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
          Logger.printException(e);
          CrashReports.logException(e);
        }

        ((TextView) view.findViewById(R.id.aptoide_version)).setText(
            getString(R.string.version) + " " +
                versionName);
        ((TextView) view.findViewById(R.id.credits)).setMovementMethod(
            LinkMovementMethod.getInstance());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context).setView(view);
        final AlertDialog aboutDialog = alertDialogBuilder.create();
        aboutDialog.setTitle(getString(R.string.about_us));
        aboutDialog.setIcon(android.R.drawable.ic_menu_info_details);
        aboutDialog.setCancelable(false);
        aboutDialog.setButton(Dialog.BUTTON_NEUTRAL, getString(android.R.string.ok),
            new Dialog.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                //                        FlurryAgent.logEvent("Setting_Opened_About_Us_Dialog");
                dialog.cancel();
              }
            });
        aboutDialog.show();

        return true;
      }
    });

    if (isSetingPIN) {
      dialogSetAdultPin(mp).show();
    }
  }

  private void redrawSizes(Double[] size) {
    final Context ctx = getContext();
    if (!Build.DEVICE.equals(AptoideUtils.SystemU.JOLLA_ALIEN_DEVICE)) {
      findPreference(SettingsConstants.CLEAR_RANK).setSummary(
          getString(R.string.clearcontent_sum) + " (" +
              AptoideUtils.StringU.getFormattedString(R.string.cache_using_X_mb,
                  new DecimalFormat("#.##").format(size[0])) +
              ")");
      findPreference(SettingsConstants.CLEAR_CACHE).setSummary(
          getString(R.string.clearcache_sum) + " (" +
              AptoideUtils.StringU.getFormattedString(R.string.cache_using_X_mb,
                  new DecimalFormat("#.##").format(size[1])) + ")");
    } else {
      findPreference(SettingsConstants.CLEAR_RANK).setSummary(
          getString(R.string.clearcontent_sum_jolla)
              + " ("
              + AptoideUtils.StringU.getFormattedString(R.string.cache_using_X_mb,
              new DecimalFormat("#.##").format(size[0]))
              +
              ")");
      findPreference(SettingsConstants.CLEAR_CACHE).setSummary(
          getString(R.string.clearcache_sum_jolla) + " (" +
              AptoideUtils.StringU.getFormattedString(R.string.cache_using_X_mb,
                  new DecimalFormat("#.##").format(size[1])) +
              ")");
    }
  }

  private Dialog dialogSetAdultPin(final Preference mp) {
    isSetingPIN = true;

    return AdultDialog.setAdultPinDialog(
        getActivity(),
        mp,
        (v, which) -> isSetingPIN = false
    );
  }

  private void maturePinSetRemoveClick() {

    int pin = SecurePreferences.getAdultContentPin();
    final Preference adultPinPreference = findPreference(SettingsConstants.ADULT_PIN);
    if (pin != -1) {
      // With Pin
      AdultDialog.buildMaturePinInputDialog(getActivity(), new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialog, int which) {
          if (which == Dialog.BUTTON_POSITIVE) {
            SecurePreferences.setAdultContentPin(-1);
            final Preference mp = findPreference(SettingsConstants.ADULT_PIN);
            mp.setTitle(R.string.set_mature_pin_title);
            mp.setSummary(R.string.set_mature_pin_summary);
          }
        }
      }).show();
    } else {
      dialogSetAdultPin(adultPinPreference).show();// Without Pin
    }
  }

  public class DeleteDir extends AsyncTask<File, Void, Void> {

    ProgressDialog pd;

    @Override protected Void doInBackground(File... params) {
      deleteDirectory(params[0]);
      return null;
    }

    @Override protected void onPreExecute() {
      super.onPreExecute();
      pd = new ProgressDialog(context);
      pd.setMessage(getString(R.string.please_wait));
      pd.show();
    }

    @Override protected void onPostExecute(Void result) {
      super.onPostExecute(result);
      pd.dismiss();
      ShowMessage.asSnack(getView(), getString(R.string.clear_cache_sucess));
      new GetDirSize().execute(new File(aptoide_path), new File(icon_path));
    }
  }

  public class GetDirSize extends AsyncTask<File, Void, Double[]> {

    double getDirSize(File dir) {
      double size = 0;
      try {
        if (dir.isFile()) {
          size = dir.length();
        } else {
          File[] subFiles = dir.listFiles();
          for (File file : subFiles) {
            if (file.isFile()) {
              size += file.length();
            } else {
              size += this.getDirSize(file);
            }
          }
        }
      } catch (Exception e) {
        Logger.printException(e);
        CrashReports.logException(e);
      }
      return size;
    }

    @Override protected Double[] doInBackground(File... dir) {
      Double[] sizes = new Double[2];

      for (int i = 0; i != sizes.length; i++) {
        sizes[i] = this.getDirSize(dir[i]) / 1024 / 1024;
      }
      return sizes;
    }

    @Override protected void onPostExecute(Double[] result) {
      super.onPostExecute(result);
      redrawSizes(result);
      unlocked = true;
    }
  }
}
