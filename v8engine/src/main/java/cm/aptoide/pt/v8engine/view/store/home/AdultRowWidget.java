package cm.aptoide.pt.v8engine.view.store.home;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.preferences.AdultContent;
import cm.aptoide.pt.v8engine.preferences.Preferences;
import cm.aptoide.pt.v8engine.preferences.SecurePreferences;
import cm.aptoide.pt.v8engine.view.ReloadInterface;
import cm.aptoide.pt.v8engine.view.dialog.EditableTextDialog;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import cm.aptoide.pt.v8engine.view.rx.RxSwitch;
import cm.aptoide.pt.v8engine.view.settings.PinDialog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 11-07-2016.
 */
public class AdultRowWidget extends Widget<AdultRowDisplayable> {

  private SwitchCompat adultSwitch;
  private SwitchCompat adultPinSwitch;
  private boolean trackAnalytics = true;
  private AdultContent adultContent;
  private RxAlertDialog adultContentConfirmationDialog;
  private EditableTextDialog enableAdultContentPinDialog;
  private boolean ignoreCheck;
  private boolean ignorePinCheck;

  public AdultRowWidget(View itemView) {
    super(itemView);
  }

  @Partners @Override protected void assignViews(View itemView) {
    adultSwitch = (SwitchCompat) itemView.findViewById(R.id.adult_content);
    adultPinSwitch = (SwitchCompat) itemView.findViewById(R.id.pin_adult_content);
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
    trackAnalytics = true;
  }

  @Override public void bindView(final AdultRowDisplayable displayable) {
    final ReloadInterface reloader = displayable;
    final SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(getContext());
    adultContent =
        new AdultContent(((V8Engine) getContext().getApplicationContext()).getAccountManager(),
            new Preferences(sharedPreferences), new SecurePreferences(sharedPreferences,
            new SecureCoderDecoder.Builder(getContext()).create()));

    compositeSubscription.add(adultContent.pinRequired()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(pinRequired -> {
          if (pinRequired) {
            adultPinSwitch.setVisibility(View.VISIBLE);
            adultSwitch.setVisibility(View.GONE);
          } else {
            adultSwitch.setVisibility(View.VISIBLE);
            adultPinSwitch.setVisibility(View.GONE);
          }
        })
        .subscribe());

    compositeSubscription.add(RxSwitch.checks(adultSwitch)
        .filter(check -> shouldCheck())
        .flatMap(checked -> {
          ignoreCheck = false;
          rollbackCheck(adultSwitch);
          if (checked) {
            adultContentConfirmationDialog.show();
            return Observable.empty();
          } else {
            adultSwitch.setEnabled(false);
            return adultContent.disable()
                .doOnCompleted(() -> trackLock())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> adultSwitch.setEnabled(true))
                .doOnTerminate(() -> reload(reloader))
                .toObservable();
          }
        })
        .retry()
        .subscribe());

    compositeSubscription.add(RxSwitch.checks(adultPinSwitch)
        .filter(check -> shouldPinCheck())
        .flatMap(checked -> {
          rollbackCheck(adultPinSwitch);
          if (checked) {
            enableAdultContentPinDialog.show();
            return Observable.empty();
          } else {
            adultPinSwitch.setEnabled(false);
            return adultContent.disable()
                .doOnCompleted(() -> trackLock())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> adultPinSwitch.setEnabled(true))
                .doOnTerminate(() -> reload(reloader))
                .toObservable();
          }
        })
        .retry()
        .subscribe());

    compositeSubscription.add(adultContentConfirmationDialog.positiveClicks()
        .doOnNext(click -> adultSwitch.setEnabled(false))
        .flatMap(click -> adultContent.enable()
            .doOnCompleted(() -> trackUnlock())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate(() -> adultSwitch.setEnabled(true))
            .doOnTerminate(() -> reload(reloader))
            .toObservable())
        .retry()
        .subscribe());

    compositeSubscription.add(enableAdultContentPinDialog.positiveClicks()
        .doOnNext(clock -> adultPinSwitch.setEnabled(false))
        .flatMap(pin -> adultContent.enable(Integer.valueOf(pin.toString()))
            .doOnCompleted(() -> trackUnlock())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(throwable -> {
              if (throwable instanceof SecurityException) {
                ShowMessage.asSnack(getContext(), R.string.adult_pin_wrong);
              }
            })
            .doOnTerminate(() -> adultPinSwitch.setEnabled(true))
            .doOnTerminate(() -> reload(reloader))
            .toObservable())
        .retry()
        .subscribe());

    compositeSubscription.add(adultContent.enabled()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(enabled -> {

          if (enabled != adultSwitch.isChecked()) {
            ignoreCheck = true;
            adultSwitch.setChecked(enabled);
          }

          if (enabled != adultPinSwitch.isChecked()) {
            ignorePinCheck = true;
            adultPinSwitch.setChecked(enabled);
          }
        })
        .subscribe());
  }

  private boolean shouldCheck() {
    if (!ignoreCheck) {
      return true;
    }
    ignoreCheck = false;
    return false;
  }

  private boolean shouldPinCheck() {
    if (!ignorePinCheck) {
      return true;
    }
    ignorePinCheck = false;
    return false;
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

  private void reload(ReloadInterface reloader) {
    reloader.load(true, true, null);
  }

  private void rollbackCheck(SwitchCompat adultSwitch) {
    adultSwitch.setChecked(!adultSwitch.isChecked());
  }
}
