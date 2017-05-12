/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 07/07/2016.
 */

package cm.aptoide.pt.v8engine.view.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.managed.ManagedKeys;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.filemanager.FileManager;
import cm.aptoide.pt.v8engine.preferences.AdultContent;
import cm.aptoide.pt.v8engine.preferences.Preferences;
import cm.aptoide.pt.v8engine.preferences.SecurePreferences;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.updates.UpdateRepository;
import cm.aptoide.pt.v8engine.util.SettingsConstants;
import cm.aptoide.pt.v8engine.view.dialog.EditableTextDialog;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import cm.aptoide.pt.v8engine.view.rx.RxPreference;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
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

  private static final String ADULT_CONTENT_PIN_PREFERENCE_VIEW_KEY = "Maturepin";
  private static final String REMOVE_ADULT_CONTENT_PIN_PREFERENCE_VIEW_KEY = "removeMaturepin";
  private static final String ADULT_CONTENT_WITH_PIN_PREFERENCE_VIEW_KEY = "matureChkBoxWithPin";
  private static final String ADULT_CONTENT_PREFERENCE_VIEW_KEY = "matureChkBox";

  protected Toolbar toolbar;
  private Context context;
  private CompositeSubscription subscriptions;
  private FileManager fileManager;
  private PermissionManager permissionManager;
  private AdultContent adultContent;

  private RxAlertDialog adultContentConfirmationDialog;
  private EditableTextDialog enableAdultContentPinDialog;
  private EditableTextDialog setPinDialog;
  private EditableTextDialog removePinDialog;

  private Preference pinPreferenceView;
  private Preference removePinPreferenceView;
  private CheckBoxPreference adultContentPreferenceView;
  private CheckBoxPreference adultContentWithPinPreferenceView;
  private boolean trackAnalytics;

  public static Fragment newInstance() {
    return new SettingsFragment();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    trackAnalytics = true;
    fileManager =
        FileManager.build(((V8Engine) getContext().getApplicationContext()).getDownloadManager(),
            ((V8Engine) getContext().getApplicationContext()).getHttpClientCache());
    subscriptions = new CompositeSubscription();
    permissionManager = new PermissionManager();
    adultContentConfirmationDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.are_you_adult)
            .setPositiveButton(R.string.yes)
            .setNegativeButton(R.string.no)
            .build();
    enableAdultContentPinDialog =
        new PinDialog.Builder(getContext()).setMessage(R.string.request_adult_pin)
            .setPositiveButton(R.string.ok)
            .setNegativeButton(R.string.cancel)
            .setView(R.layout.dialog_requestpin)
            .setEditText(R.id.pininput)
            .build();
    removePinDialog = new PinDialog.Builder(getContext()).setMessage(R.string.request_adult_pin)
        .setPositiveButton(R.string.ok)
        .setNegativeButton(R.string.cancel)
        .setView(R.layout.dialog_requestpin)
        .setEditText(R.id.pininput)
        .build();
    setPinDialog = new PinDialog.Builder(getContext()).setMessage(R.string.asksetadultpinmessage)
        .setPositiveButton(R.string.ok)
        .setNegativeButton(R.string.cancel)
        .setView(R.layout.dialog_requestpin)
        .setEditText(R.id.pininput)
        .build();
  }

  @Override public void onCreatePreferences(Bundle bundle, String s) {
    addPreferencesFromResource(R.xml.settings);
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(getActivity());
    sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    adultContent =
        new AdultContent(((V8Engine) getContext().getApplicationContext()).getAccountManager(),
            new Preferences(sharedPreferences), new SecurePreferences(sharedPreferences,
            new SecureCoderDecoder.Builder(getContext()).create()));
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

    adultContentPreferenceView =
        (CheckBoxPreference) findPreference(ADULT_CONTENT_PREFERENCE_VIEW_KEY);
    adultContentWithPinPreferenceView =
        (CheckBoxPreference) findPreference(ADULT_CONTENT_WITH_PIN_PREFERENCE_VIEW_KEY);
    pinPreferenceView = findPreference(ADULT_CONTENT_PIN_PREFERENCE_VIEW_KEY);
    removePinPreferenceView = findPreference(REMOVE_ADULT_CONTENT_PIN_PREFERENCE_VIEW_KEY);

    setupClickHandlers();
  }

  @Override public void onDestroyView() {
    subscriptions.clear();
    super.onDestroyView();
  }

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (shouldRefreshUpdates(key)) {
      UpdateAccessor updateAccessor = AccessorFactory.getAccessorFor(Update.class);
      updateAccessor.removeAll();
      UpdateRepository repository = RepositoryFactory.getUpdateRepository(context);
      repository.sync(true)
          .andThen(repository.getAll(false))
          .first()
          .subscribe(updates -> Logger.d(TAG, "updates refreshed"), throwable -> {
            CrashReport.getInstance()
                .log(throwable);
          });
    }
  }

  private boolean shouldRefreshUpdates(String key) {
    return key.equals(ManagedKeys.UPDATES_FILTER_ALPHA_BETA_KEY) || key.equals(
        ManagedKeys.HWSPECS_FILTER) || key.equals(ManagedKeys.UPDATES_SYSTEM_APPS_KEY);
  }

  private void setupClickHandlers() {
    //set AppStore name
    findPreference(SettingsConstants.CHECK_AUTO_UPDATE_CATEGORY).setTitle(
        AptoideUtils.StringU.getFormattedString(R.string.setting_category_autoupdate,
            Application.getConfiguration()
                .getMarketName()));

    Preference autoUpdatepreference = findPreference(SettingsConstants.CHECK_AUTO_UPDATE);
    autoUpdatepreference.setTitle(
        AptoideUtils.StringU.getFormattedString(R.string.setting_category_autoupdate_title,
            Application.getConfiguration()
                .getMarketName()));
    autoUpdatepreference.setSummary(
        AptoideUtils.StringU.getFormattedString(R.string.setting_category_autoupdate_message,
            Application.getConfiguration()
                .getMarketName()));

    subscriptions.add(adultContent.enabled()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(state -> adultContentPreferenceView.setChecked(state))
        .doOnNext(state -> adultContentWithPinPreferenceView.setChecked(state))
        .subscribe());

    subscriptions.add(adultContentConfirmationDialog.positiveClicks()
        .doOnNext(click -> adultContentPreferenceView.setEnabled(false))
        .flatMap(click -> adultContent.enable()
            .doOnCompleted(() -> trackUnlock())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate(() -> adultContentPreferenceView.setEnabled(true))
            .toObservable())
        .retry()
        .subscribe());

    subscriptions.add(RxPreference.checks(adultContentPreferenceView)
        .flatMap(checked -> {
          rollbackCheck(adultContentPreferenceView);
          if (checked) {
            adultContentConfirmationDialog.show();
            return Observable.empty();
          } else {
            adultContentPreferenceView.setEnabled(false);
            return adultContent.disable()
                .doOnCompleted(() -> trackLock())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> adultContentPreferenceView.setEnabled(true))
                .toObservable();
          }
        })
        .retry()
        .subscribe());

    subscriptions.add(RxPreference.checks(adultContentWithPinPreferenceView)
        .flatMap(checked -> {
          rollbackCheck(adultContentWithPinPreferenceView);
          if (checked) {
            enableAdultContentPinDialog.show();
            return Observable.empty();
          } else {
            adultContentWithPinPreferenceView.setEnabled(false);
            return adultContent.disable()
                .doOnCompleted(() -> trackLock())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> adultContentWithPinPreferenceView.setEnabled(true))
                .toObservable();
          }
        })
        .retry()
        .subscribe());

    subscriptions.add(adultContent.pinRequired()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(pinRequired -> {
          if (pinRequired) {
            pinPreferenceView.setVisible(false);
            removePinPreferenceView.setVisible(true);
            adultContentWithPinPreferenceView.setVisible(true);
            adultContentPreferenceView.setVisible(false);
          } else {
            pinPreferenceView.setVisible(true);
            removePinPreferenceView.setVisible(false);
            adultContentWithPinPreferenceView.setVisible(false);
            adultContentPreferenceView.setVisible(true);
          }
        })
        .subscribe());

    subscriptions.add(RxPreference.clicks(pinPreferenceView)
        .doOnNext(preference -> {
          setPinDialog.show();
        })
        .subscribe());

    subscriptions.add(RxPreference.clicks(removePinPreferenceView)
        .doOnNext(preference -> {
          removePinDialog.show();
        })
        .subscribe());

    subscriptions.add(setPinDialog.positiveClicks()
        .filter(pin -> !TextUtils.isEmpty(pin))
        .flatMap(pin -> adultContent.requirePin(Integer.valueOf(pin.toString()))
            .toObservable())
        .retry()
        .subscribe());

    subscriptions.add(removePinDialog.positiveClicks()
        .flatMap(pin -> adultContent.removePin(Integer.valueOf(pin.toString()))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(throwable -> {
              if (throwable instanceof SecurityException) {
                ShowMessage.asSnack(getActivity(), R.string.adult_pin_wrong);
              }
            })
            .toObservable())
        .retry()
        .subscribe());

    subscriptions.add(enableAdultContentPinDialog.positiveClicks()
        .doOnNext(clock -> adultContentWithPinPreferenceView.setEnabled(false))
        .flatMap(pin -> adultContent.enable(Integer.valueOf(pin.toString()))
            .doOnCompleted(() -> trackUnlock())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(throwable -> {
              if (throwable instanceof SecurityException) {
                ShowMessage.asSnack(getActivity(), R.string.adult_pin_wrong);
              }
            })
            .doOnTerminate(() -> adultContentWithPinPreferenceView.setEnabled(true))
            .toObservable())
        .retry()
        .subscribe());

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

    findPreference(SettingsConstants.CLEAR_CACHE).setOnPreferenceClickListener(
        new Preference.OnPreferenceClickListener() {
          @Override public boolean onPreferenceClick(Preference preference) {
            ProgressDialog dialog = GenericDialogs.createGenericPleaseWaitDialog(getContext());
            subscriptions.add(GenericDialogs.createGenericContinueCancelMessage(getContext(),
                getString(R.string.storage_dialog_title),
                getString(R.string.clear_cache_dialog_message))
                .filter(eResponse -> eResponse.equals(GenericDialogs.EResponse.YES))
                .doOnNext(eResponse -> dialog.show())
                .flatMap(eResponse -> fileManager.deleteCache())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> dialog.dismiss())
                .subscribe(deletedSize -> {
                  ShowMessage.asSnack(SettingsFragment.this,
                      AptoideUtils.StringU.getFormattedString(R.string.freed_space,
                          AptoideUtils.StringU.formatBytes(deletedSize, false)));
                }, throwable -> {
                  ShowMessage.asSnack(SettingsFragment.this, R.string.error_SYS_1);
                  throwable.printStackTrace();
                }));
            return false;
          }
        });

    Preference hwSpecs = findPreference(SettingsConstants.HARDWARE_SPECS);

    hwSpecs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override public boolean onPreferenceClick(Preference preference) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(getString(R.string.setting_hwspecstitle));
        alertDialogBuilder.setIcon(android.R.drawable.ic_menu_info_details)
            .setMessage(getString(R.string.setting_sdk_version)
                    + ": "
                    + AptoideUtils.SystemU.getSdkVer()
                    + "\n"
                    + getString(R.string.setting_screen_size)
                    + ": "
                    + AptoideUtils.ScreenU.getScreenSize()
                    + "\n"
                    + getString(R.string.setting_esgl_version)
                    + ": "
                    + AptoideUtils.SystemU.getGlEsVer()
                    + "\n"
                    + getString(R.string.screenCode)
                    + ": "
                    + AptoideUtils.ScreenU.getNumericScreenSize()
                    + "/"
                    + AptoideUtils.ScreenU.getDensityDpi()
                    + "\n"
                    + getString(R.string.cpuAbi)
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

        View view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_about, null);
        String versionName = "";
        int versionCode = 0;

        try {
          versionName = getActivity().getPackageManager()
              .getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
          CrashReport.getInstance()
              .log(e);
        }
        try {
          versionCode = getActivity().getPackageManager()
              .getPackageInfo(getActivity().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
          CrashReport.getInstance()
              .log(e);
        }

        ((TextView) view.findViewById(R.id.aptoide_version)).setText(
            getString(R.string.version) + " " + versionName + " (" + versionCode + ")");

        ((TextView) view.findViewById(R.id.credits)).setMovementMethod(
            LinkMovementMethod.getInstance());

        new AlertDialog.Builder(context).setView(view)
            .setTitle(getString(R.string.settings_about_us))
            .setIcon(android.R.drawable.ic_menu_info_details)
            .setPositiveButton(android.R.string.ok,
                (dialogInterface, i) -> dialogInterface.cancel())
            .create()
            .show();

        return true;
      }
    });

    // AN-1533 - temporary solution was to remove root installation
    //CheckBoxPreference autoUpdatePreference =
    //    (CheckBoxPreference) findPreference(SettingsConstants.AUTO_UPDATE_ENABLE);
    //findPreference(SettingsConstants.ALLOW_ROOT_INSTALLATION).setOnPreferenceChangeListener(
    //    (preference, o) -> {
    //      final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
    //      if (checkBoxPreference.isChecked()) {
    //        ManagerPreferences.setAutoUpdateEnable(false);
    //        autoUpdatePreference.setChecked(false);
    //      }
    //      return true;
    //    });
    //
    //PermissionService permissionRequest = (PermissionService) getContext();
    //autoUpdatePreference.setDependency(SettingsConstants.ALLOW_ROOT_INSTALLATION);
    //autoUpdatePreference.setOnPreferenceClickListener(preference -> {
    //  final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
    //  if (checkBoxPreference.isChecked()) {
    //    checkBoxPreference.setChecked(false);
    //    subscriptions.add(permissionManager.requestExternalStoragePermission(permissionRequest)
    //        .flatMap(success -> permissionManager.requestDownloadAccess(permissionRequest))
    //        .subscribe(success -> {
    //          checkBoxPreference.setChecked(true);
    //          ManagerPreferences.setAutoUpdateEnable(true);
    //        }, throwable -> CrashReport.getInstance().log(throwable)));
    //  }
    //  return true;
    //});
  }

  private void rollbackCheck(CheckBoxPreference preference) {
    preference.setChecked(!preference.isChecked());
  }

  private void settingsResult() {
    getActivity().setResult(Activity.RESULT_OK);
  }

  private void trackLock() {
    if (trackAnalytics) {
      trackAnalytics = false;
      Analytics.AdultContent.lock();
    }
  }

  private void trackUnlock() {
    if (trackAnalytics) {
      trackAnalytics = false;
      Analytics.AdultContent.unlock();
    }
  }
}
