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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dialog.AndroidBasicDialog;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.managed.ManagedKeys;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.dialog.AdultDialog;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.UpdateRepository;
import cm.aptoide.pt.v8engine.util.SettingsConstants;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by fabio on 26-10-2015.
 *
 * @author fabio
 * @author sithengineer
 */
public class SettingsFragment extends PreferenceFragmentCompat
    implements SharedPreferences.OnSharedPreferenceChangeListener {
  private static final String TAG = SettingsFragment.class.getSimpleName();

  private static boolean isSetingPIN = false;
  protected Toolbar toolbar;
  private Context context;
  private FileUtils fileUtils;
  private CompositeSubscription subscriptions;
  private InstallManager installManager;
  private String[] cacheFolders;

  public static Fragment newInstance() {
    return new SettingsFragment();
  }

  @Override public void onDestroyView() {
    subscriptions.clear();
    super.onDestroyView();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    fileUtils = new FileUtils();
    subscriptions = new CompositeSubscription();
    installManager = new InstallManager(AptoideDownloadManager.getInstance(),
        new InstallerFactory().create(getContext(), InstallerFactory.ROLLBACK),
        AccessorFactory.getAccessorFor(Download.class),
        AccessorFactory.getAccessorFor(Installed.class));
    cacheFolders = new String[] {
        Application.getContext().getCacheDir().getPath(),
        Application.getConfiguration().getCachePath()
    };
  }

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    // TODO
    if (key.equals(ManagedKeys.UPDATES_FILTER_ALPHA_BETA_KEY)) {
      UpdateAccessor updateAccessor = AccessorFactory.getAccessorFor(Update.class);
      updateAccessor.removeAll();
      UpdateRepository repository = RepositoryFactory.getRepositoryFor(Update.class);
      repository.getUpdates(true)
          .first()
          .subscribe(updates -> Logger.d(TAG, "updates refreshed"), throwable -> {
            throwable.printStackTrace();
            CrashReports.logException(throwable);
          });
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
      Logger.d("PINTEST", "PinBuild");
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

    findPreference(SettingsConstants.FILTER_APPS).setOnPreferenceClickListener(preference -> {
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
            ProgressDialog dialog = GenericDialogs.createGenericPleaseWaitDialog(getContext());
            subscriptions.add(GenericDialogs.createGenericContinueCancelMessage(getContext(),
                getString(R.string.storage_dialog_title),
                getString(R.string.clear_cache_dialog_message))
                .filter(eResponse -> eResponse.equals(GenericDialogs.EResponse.YES))
                .doOnNext(eResponse -> dialog.show())
                .observeOn(Schedulers.io())
                .flatMap(eResponse -> checkInstalling())
                .flatMap(eResponse -> fileUtils.deleteFolder(cacheFolders))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> dialog.dismiss())
                .subscribe(deletedSize -> {
                  deleteFilesSuccessMessage(deletedSize);
                }, throwable -> {
                  deleteFilesErrorMessage(throwable);
                }));
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
        int versionCode = 0;

        try {
          versionName = getActivity().getPackageManager()
              .getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
          Logger.printException(e);
          CrashReports.logException(e);
        }
        try {
          versionCode = getActivity().getPackageManager()
              .getPackageInfo(getActivity().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
          Logger.printException(e);
          CrashReports.logException(e);
        }

        ((TextView) view.findViewById(R.id.aptoide_version)).setText(
            getString(R.string.version) + " " +
                versionName + " (" + versionCode + ")");

        ((TextView) view.findViewById(R.id.credits)).setMovementMethod(
            LinkMovementMethod.getInstance());

        AndroidBasicDialog.build(getContext(), view)
            .setPositiveButton(android.R.string.ok)
            .setMessage(R.string.about_us)
            .setTitle(getString(R.string.about_us))
            .setIcon(android.R.drawable.ic_menu_info_details)
            .show();

        return true;
      }
    });

    if (isSetingPIN) {
      dialogSetAdultPin(mp).show();
    }
  }

  private void deleteFilesErrorMessage(Throwable throwable) {
    if (throwable instanceof DownloadIsRunningException) {
      ShowMessage.asSnack(SettingsFragment.this, R.string.download_is_running_error);
    } else {
      ShowMessage.asSnack(SettingsFragment.this, R.string.error_SYS_1);
      throwable.printStackTrace();
    }
  }

  private void deleteFilesSuccessMessage(Long deletedSize) {
    ShowMessage.asSnack(SettingsFragment.this,
        AptoideUtils.StringU.getFormattedString(R.string.freed_space,
            AptoideUtils.StringU.formatBytes(deletedSize)));
  }

  private Observable<Void> checkInstalling() {
    return installManager.getInstallationsAsList()
        .first()
        .flatMapIterable(progresses -> progresses)
        .filter(progress -> progress.getState() == Progress.ACTIVE)
        .toList()
        .map(progresses -> progresses != null && progresses.size() > 0)
        .flatMap(isDownload -> {
          if (isDownload) {
            return Observable.error(new DownloadIsRunningException());
          } else {
            return Observable.just(null);
          }
        });
  }

  private Dialog dialogSetAdultPin(final Preference mp) {
    isSetingPIN = true;

    return AdultDialog.setAdultPinDialog(getActivity(), mp, (v, which) -> isSetingPIN = false);
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

  class DownloadIsRunningException extends RuntimeException {
  }
}
