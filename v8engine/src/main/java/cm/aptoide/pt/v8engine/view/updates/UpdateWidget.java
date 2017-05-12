/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.view.updates;

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
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.updates.UpdateRepository;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 17-05-2016.
 */
@Displayables({ UpdateDisplayable.class }) public class UpdateWidget
    extends Widget<UpdateDisplayable> {

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

  public UpdateWidget(View itemView) {
    super(itemView);
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

    updateRepository = RepositoryFactory.getUpdateRepository(getContext());
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

    final Observable<Void> showInstalledVersionName =
        showInstalledVersionName(updateDisplayable.getPackageName(),
            AccessorFactory.getAccessorFor(Installed.class));

    final Observable<Void> showProgress =
        showProgress(updateDisplayable.getInstallManager(), updateDisplayable.getMd5());

    final Observable<Void> handleLongClicks =
        handleLongClicks(updateDisplayable.getPackageName(), context);

    final Observable<Void> handleUpdateRowClick = handleUpdateRowClick(updateDisplayable);

    compositeSubscription.add(
        Observable.merge(handleUpdateButtonClick, showInstalledVersionName, showProgress,
            handleLongClicks, handleUpdateRowClick)
            .subscribe(__ -> {/* do nothing */}, err -> CrashReport.getInstance()
                .log(err)));
  }

  private Observable<Void> handleUpdateButtonClick(UpdateDisplayable displayable, Context context) {
    return RxView.clicks(updateButtonLayout)
        .flatMap(click -> displayable.downloadAndInstall(context, (PermissionService) context))
        .retry()
        .map(__ -> null);
  }

  private Observable<Void> showInstalledVersionName(String packageName,
      InstalledAccessor accessor) {
    return accessor.get(packageName)
        .first()
        .filter(installed -> installed != null && !TextUtils.isEmpty(installed.getVersionName()))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(installed -> installedVersionName.setText(installed.getVersionName()))
        .map(__ -> null);
  }

  @NonNull private Observable<Void> showProgress(InstallManager installManager, String md5) {
    return getUpdateProgress(installManager, md5).map(
        downloadProgress -> isDownloadingOrInstalling(downloadProgress))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(shouldShow -> showProgress(shouldShow))
        .map(__ -> null);
  }

  private Observable<Void> handleLongClicks(String packageName, FragmentActivity context) {
    return RxView.longClicks(updateRowLayout)
        .flatMap(__ -> getLongClickListener(packageName, context));
  }

  @NonNull private Observable<Void> handleUpdateRowClick(UpdateDisplayable updateDisplayable) {
    return RxView.clicks(updateRowLayout)
        .doOnNext(v -> {
          final Fragment fragment = V8Engine.getFragmentProvider()
              .newAppViewFragment(updateDisplayable.getAppId(), updateDisplayable.getPackageName());
          getFragmentNavigator().navigateTo(fragment);
        });
  }

  /**
   * *  <dt><b>Scheduler:</b></dt>
   * <dd>{@code getUpdates} operates by default on the {@code io} {@link Scheduler}..</dd>
   * </dl>
   */
  private Observable<Progress<Download>> getUpdateProgress(InstallManager installManager,
      String md5) {
    return installManager.getInstallationsAsList()
        .filter(listProgress -> listProgress != null && !listProgress.isEmpty())
        .flatMap(list -> {
          for (Progress<Download> progress : list) {
            if (progress.getRequest() != null && progress.getRequest()
                .getMd5()
                .equalsIgnoreCase(md5)) {
              return Observable.just(progress);
            }
          }
          return Observable.empty();
        });
  }

  private boolean isDownloadingOrInstalling(Progress<Download> progress) {
    return progress.getRequest()
        .getOverallDownloadStatus() == Download.PROGRESS
        || progress.getRequest()
        .getOverallDownloadStatus() == Download.PENDING
        || progress.getRequest()
        .getOverallDownloadStatus() == Download.IN_QUEUE;
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
