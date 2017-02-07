/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.DialogInterface;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.crashreports.CrashReport;
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
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.UpdateRepository;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by neuro on 17-05-2016.
 */
@Displayables({ UpdateDisplayable.class }) public class UpdateWidget
    extends Widget<UpdateDisplayable> {

  private static final String TAG = UpdateWidget.class.getSimpleName();

  private View updateRowRelativeLayout;
  private TextView labelTextView;
  private ImageView iconImageView;
  private ImageView imgUpdateLayout;
  private TextView installedVernameTextView;
  private TextView updateVernameTextView;
  private TextView textUpdateLayout;
  private ViewGroup updateButtonLayout;
  private UpdateDisplayable displayable;
  private LinearLayout updateLayout;
  private ProgressBar progressBar;

  private UpdateRepository updateRepository;

  public UpdateWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    updateRowRelativeLayout = itemView.findViewById(R.id.updateRowRelativeLayout);
    labelTextView = (TextView) itemView.findViewById(R.id.name);
    iconImageView = (ImageView) itemView.findViewById(R.id.icon);
    installedVernameTextView = (TextView) itemView.findViewById(R.id.app_installed_version);
    updateVernameTextView = (TextView) itemView.findViewById(R.id.app_update_version);
    updateButtonLayout = (ViewGroup) itemView.findViewById(R.id.updateButtonLayout);
    updateLayout = (LinearLayout) itemView.findViewById(R.id.update_layout);
    imgUpdateLayout = (ImageView) itemView.findViewById(R.id.img_update_layout);
    textUpdateLayout = (TextView) itemView.findViewById(R.id.text_update_layout);
    progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);

    updateRepository = RepositoryFactory.getUpdateRepository();
  }

  @Override public void bindView(UpdateDisplayable updateDisplayable) {
    this.displayable = updateDisplayable;
    final String packageName = updateDisplayable.getPackageName();
    final InstalledAccessor accessor = AccessorFactory.getAccessorFor(Installed.class);

    compositeSubscription.add(accessor.get(packageName)
        .first()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(installed -> installedVernameTextView.setText(installed.getVersionName()),
            throwable -> throwable.printStackTrace()));

    labelTextView.setText(updateDisplayable.getLabel());
    updateVernameTextView.setText(updateDisplayable.getUpdateVersionName());
    final FragmentActivity context = getContext();
    ImageLoader.with(context).load(updateDisplayable.getIcon(), iconImageView);

    compositeSubscription.add(RxView.clicks(updateRowRelativeLayout).subscribe(v -> {
      final Fragment fragment = V8Engine.getFragmentProvider()
          .newAppViewFragment(updateDisplayable.getAppId(), updateDisplayable.getPackageName());
      getNavigationManager().navigateTo(fragment);
    }, throwable -> throwable.printStackTrace()));

    final Action1<Void> longClickListener = __ -> {
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
                        CrashReport.getInstance().log(throwable);
                      }));
            }
            dialog.dismiss();
          });

      builder.create().show();
    };

    compositeSubscription.add(RxView.longClicks(updateRowRelativeLayout)
        .subscribe(longClickListener, throwable -> throwable.printStackTrace()));
    compositeSubscription.add(RxView.clicks(updateButtonLayout)
        .flatMap(click -> displayable.downloadAndInstall(context, (PermissionRequest) context))
        .retry()
        .subscribe(o -> {
        }, throwable -> throwable.printStackTrace()));

    // FIXME: 24/1/2017 sithengineer do individual progress tracking
    //compositeSubscription.add(displayable.getUpdates()
    //    .filter(downloadProgress -> downloadProgress.getRequest()
    //        .getMd5()
    //        .equals(displayable.getDownload().getMd5()))
    //    .map(downloadProgress -> displayable.isDownloadingOrInstalling(downloadProgress))
    //    .observeOn(AndroidSchedulers.mainThread())
    //    .subscribe(shouldShow -> showProgress(shouldShow),
    //        throwable -> throwable.printStackTrace()));

    // create the download object and listen to changes to it...

    final InstallManager installManager = displayable.getInstallManager();
    final String md5 = displayable.getMd5();

    compositeSubscription.add(
        getUpdateProgress(installManager, md5).observeOn(AndroidSchedulers.mainThread())
            .map(downloadProgress -> isDownloadingOrInstalling(downloadProgress))
            .subscribe(shouldShow -> showProgress(shouldShow),
                throwable -> throwable.printStackTrace()));
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
    return progress.getRequest().getOverallDownloadStatus() == Download.PROGRESS
        || progress.getRequest().getOverallDownloadStatus() == Download.PENDING
        || progress.getRequest().getOverallDownloadStatus() == Download.IN_QUEUE;
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
