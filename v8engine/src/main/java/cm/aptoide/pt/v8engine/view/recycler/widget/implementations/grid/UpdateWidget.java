/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.DialogInterface;
import android.support.annotation.UiThread;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.UpdateRepository;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;

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
    ImageLoader.load(updateDisplayable.getIcon(), iconImageView);

    updateRowRelativeLayout.setOnClickListener(v -> FragmentUtils.replaceFragmentV4(getContext(),
        V8Engine.getFragmentProvider()
            .newAppViewFragment(updateDisplayable.getAppId(), updateDisplayable.getPackageName())));

    final View.OnLongClickListener longClickListener = v -> {
      AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
      builder.setTitle(R.string.ignore_update)
          .setCancelable(true)
          .setNegativeButton(R.string.no, null)
          .setPositiveButton(R.string.yes, (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
              compositeSubscription.add(updateRepository.setExcluded(packageName, true)
                  .subscribe(success -> Logger.d(TAG,
                      String.format("Update with package name %s was excluded", packageName)),
                      throwable -> {
                        ShowMessage.asSnack(getContext(), R.string.unknown_error);
                        Logger.e(TAG, throwable);
                        CrashReports.logException(throwable);
                      }));
            }
            dialog.dismiss();
          });

      builder.create().show();

      return true;
    };

    updateRowRelativeLayout.setOnLongClickListener(longClickListener);

    compositeSubscription.add(RxView.clicks(updateButtonLayout)
        .flatMap(
            click -> displayable.downloadAndInstall(getContext(), (PermissionRequest) getContext()))
        .retry()
        .subscribe(o -> {
        }, throwable -> throwable.printStackTrace()));

    compositeSubscription.add(displayable.getUpdates()
        .filter(downloadProgress -> downloadProgress.getRequest()
            .getMd5()
            .equals(displayable.getDownload().getMd5()))
        .map(downloadProgress -> displayable.isDownloadingOrInstalling(downloadProgress))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(shouldShow -> showProgress(shouldShow),
            throwable -> throwable.printStackTrace()));
  }

  @UiThread private void showProgress(Boolean showProgress) {
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
