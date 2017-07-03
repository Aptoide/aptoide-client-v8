package cm.aptoide.pt.v8engine.view.account;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.view.account.exception.InvalidImageException;
import cm.aptoide.pt.v8engine.view.permission.AccountPermissionProvider;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;

public class ImagePickerPresenter implements Presenter {

  private static final int GALLERY_PICK = 5;
  private static final int CAMERA_PICK = 6;

  private final ImagePickerView view;
  private final CrashReport crashReport;
  private final AccountPermissionProvider accountPermissionProvider;
  private final PhotoFileGenerator photoFileGenerator;
  private final ImageValidator imageValidator;
  private final Scheduler uiScheduler;
  private final UriToPathResolver uriToPathResolver;
  private final ImagePickerNavigator navigator;

  public ImagePickerPresenter(ImagePickerView view, CrashReport crashReport,
      AccountPermissionProvider accountPermissionProvider, PhotoFileGenerator photoFileGenerator,
      ImageValidator imageValidator, Scheduler viewScheduler, UriToPathResolver uriToPathResolver,
      ImagePickerNavigator navigator) {
    this.view = view;
    this.crashReport = crashReport;
    this.accountPermissionProvider = accountPermissionProvider;
    this.photoFileGenerator = photoFileGenerator;
    this.imageValidator = imageValidator;
    this.uiScheduler = viewScheduler;
    this.uriToPathResolver = uriToPathResolver;
    this.navigator = navigator;
  }

  public void handlePickImageClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.selectStoreImageClick()
            .retry()
            .doOnNext(__2 -> view.showImagePickerDialog()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  @NonNull private Completable loadValidImageOrThrow(String createdUri) {
    return imageValidator.validateOrGetException(createdUri)
        .observeOn(uiScheduler)
        .doOnCompleted(() -> view.loadImage(createdUri));
  }

  @NonNull private Observable<Void> getFileFromCameraWithUri(String createdUri) {
    final Uri fileUri = Uri.parse(createdUri);
    return navigator.navigateToCameraWithImageUri(CAMERA_PICK, fileUri);
  }

  private void handleCameraSelection() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.dialogCameraSelected()
            .doOnNext(__2 -> accountPermissionProvider.requestCameraPermission(CAMERA_PICK)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleGalleryImageResult() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> accountPermissionProvider.singlePermissionResult(GALLERY_PICK)
            .filter(success -> success)
            .doOnNext(__2 -> view.dismissLoadImageDialog())
            .flatMap(__2 -> navigator.navigateToGalleryForImageUri(GALLERY_PICK))
            .flatMapCompletable(selectedImageUri -> loadValidImageOrThrow(selectedImageUri))
            .doOnError(err -> {
              crashReport.log(err);
              if (err instanceof InvalidImageException) {
                view.showIconPropertiesError((InvalidImageException) err);
              }
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  @NonNull private Completable loadValidImageOrThrow(Uri selectedImageUri) {
    return imageValidator.validateOrGetException(
        uriToPathResolver.getMediaStoragePath(selectedImageUri))
        .observeOn(uiScheduler)
        .doOnCompleted(() -> view.loadImage(selectedImageUri.toString()));
  }

  private void handleGallerySelection() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.dialogGallerySelected()
            .doOnNext(__2 -> accountPermissionProvider.requestGalleryPermission(GALLERY_PICK)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleCameraImageResult() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> accountPermissionProvider.singlePermissionResult(CAMERA_PICK)
            .filter(success -> success)
            .doOnNext(__2 -> view.dismissLoadImageDialog())
            .flatMapSingle(__2 -> photoFileGenerator.generateNewImageFileUriAsString())
            .flatMap(createdUri -> getFileFromCameraWithUri(createdUri).flatMapCompletable(
                createdPictureFile -> loadValidImageOrThrow(createdUri)))
            .doOnError(err -> {
              if (err instanceof InvalidImageException) {
                view.showIconPropertiesError((InvalidImageException) err);
              }
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override public void present() {
    handlePickImageClick();
    handleCameraSelection();
    handleCameraImageResult();
    handleGallerySelection();
    handleGalleryImageResult();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }
}
