/*
 * Copyright (c) 2016.
 * Modified on 16/08/2016.
 */

package cm.aptoide.pt.updates.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.updates.UpdateRepository;
import cm.aptoide.pt.updates.UpdatesAnalytics;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 17-05-2016.
 */
public class UpdateWidget extends Widget<UpdateDisplayable> {

  private static final String TAG = UpdateWidget.class.getSimpleName();

  private View updateRowLayout;
  private TextView labelTextView;
  private ImageView icon;
  private ImageView imgUpdateLayout;
  private TextView installedVersionName;
  private TextView updateVersionName;
  private TextView textUpdateLayout;
  private ViewGroup updateButtonLayout;
  private ProgressBar progressBar;

  private UpdateRepository updateRepository;
  private UpdatesAnalytics updatesAnalytics;

  public UpdateWidget(View itemView) {
    super(itemView);
    final AnalyticsManager analyticsManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAnalyticsManager();
    final NavigationTracker navigationTracker =
        ((AptoideApplication) getContext().getApplicationContext()).getNavigationTracker();
    updatesAnalytics = new UpdatesAnalytics(analyticsManager, navigationTracker);
  }

  @Override protected void assignViews(View itemView) {
    updateRowLayout = itemView.findViewById(R.id.updateRowRelativeLayout);
    labelTextView = (TextView) itemView.findViewById(R.id.name);
    icon = (ImageView) itemView.findViewById(R.id.icon);
    installedVersionName = (TextView) itemView.findViewById(R.id.app_installed_version);
    updateVersionName = (TextView) itemView.findViewById(R.id.app_update_version);
    updateButtonLayout = (ViewGroup) itemView.findViewById(R.id.updateButtonLayout);
    imgUpdateLayout = (ImageView) itemView.findViewById(R.id.img_update_layout);
    textUpdateLayout = (TextView) itemView.findViewById(R.id.text_update_layout);
    progressBar = (ProgressBar) itemView.findViewById(R.id.row_progress_bar);

    updateRepository = RepositoryFactory.getUpdateRepository(getContext(),
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());
  }

  @Override public void unbindView() {
    showProgress(false);
    super.unbindView();
  }

  @Override public void bindView(UpdateDisplayable updateDisplayable) {
    FragmentActivity context = getContext();

    labelTextView.setText(updateDisplayable.getLabel());
    updateVersionName.setText(updateDisplayable.getUpdateVersionName());

    // load row image
    ImageLoader.with(context)
        .load(updateDisplayable.getIcon(), icon);

    final Observable<Void> handleUpdateButtonClick =
        handleUpdateButtonClick(updateDisplayable, context);

    compositeSubscription.add(updateDisplayable.shouldShowProgress()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(shouldShow -> showProgress(shouldShow), throwable -> CrashReport.getInstance()
            .log(throwable)));

    final Observable<Void> showInstalledVersionName =
        showInstalledVersionName(updateDisplayable.getPackageName(),
            updateDisplayable.getInstalledRepository());

    final Observable<Void> handleLongClicks =
        handleLongClicks(updateDisplayable.getPackageName(), context);

    final Observable<Void> handleUpdateRowClick = handleUpdateRowClick(updateDisplayable);

    compositeSubscription.add(
        Observable.merge(handleUpdateButtonClick, showInstalledVersionName, handleLongClicks,
            handleUpdateRowClick)
            .subscribe(__ -> {/* do nothing */}, err -> CrashReport.getInstance()
                .log(err)));
  }

  private Observable<Void> handleUpdateButtonClick(UpdateDisplayable displayable, Context context) {
    return RxView.clicks(updateButtonLayout)
        .doOnNext(__ -> updatesAnalytics.updates("Update"))
        .flatMapCompletable(
            click -> displayable.downloadAndInstall(context, (PermissionService) context,
                getContext().getResources()))
        .retry()
        .map(__ -> null);
  }

  private Observable<Void> showInstalledVersionName(String packageName,
      InstalledRepository installedRepository) {
    return installedRepository.getInstalled(packageName)
        .first()
        .filter(installed -> installed != null && !TextUtils.isEmpty(installed.getVersionName()))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(installed -> installedVersionName.setText(installed.getVersionName()))
        .map(__ -> null);
  }

  private Observable<Void> handleLongClicks(String packageName, FragmentActivity context) {
    return RxView.longClicks(updateRowLayout)
        .flatMap(__ -> getLongClickListener(packageName, context));
  }

  @NonNull private Observable<Void> handleUpdateRowClick(UpdateDisplayable updateDisplayable) {
    return RxView.clicks(updateRowLayout)
        .doOnNext(v -> {
          updatesAnalytics.updates("Open App View");
          final Fragment fragment = AptoideApplication.getFragmentProvider()
              .newAppViewFragment(updateDisplayable.getAppId(), updateDisplayable.getPackageName(),
                  "");
          getFragmentNavigator().navigateTo(fragment, true);
        });
  }

  @NonNull
  private Observable<Void> getLongClickListener(String packageName, FragmentActivity context) {
    return Observable.fromCallable(() -> {
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setTitle(R.string.ignore_update)
          .setCancelable(true)
          .setNegativeButton(R.string.no, null)
          .setPositiveButton(R.string.yes, (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
              compositeSubscription.add(updateRepository.setExcluded(packageName, true)
                  .subscribe(success -> Logger.d(TAG,
                      String.format("Update with package name %s was excluded", packageName)),
                      throwable -> {
                        ShowMessage.asSnack(context, R.string.unknown_error);
                        CrashReport.getInstance()
                            .log(throwable);
                      }));
            }
            dialog.dismiss();
          });

      builder.create()
          .show();
      return null;
    });
  }

  @UiThread private void showProgress(boolean showProgress) {
    if (showProgress) {
      textUpdateLayout.setVisibility(View.GONE);
      imgUpdateLayout.setVisibility(View.GONE);
      progressBar.setVisibility(View.VISIBLE);
    } else {
      textUpdateLayout.setVisibility(View.VISIBLE);
      imgUpdateLayout.setVisibility(View.VISIBLE);
      progressBar.setVisibility(View.GONE);
    }
  }
}
