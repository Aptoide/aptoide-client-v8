package cm.aptoide.pt.view.account;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.account.exception.InvalidImageException;
import cm.aptoide.pt.view.permission.AccountPermissionProvider;
import cm.aptoide.pt.view.permission.PermissionProvider;
import java.io.File;
import rx.Completable;
import rx.Scheduler;
import rx.Single;
import rx.schedulers.Schedulers;

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
  private final ContentResolver contentResolver;
  private final ImageLoader imageLoader;

  public ImagePickerPresenter(ImagePickerView view, CrashReport crashReport,
      AccountPermissionProvider accountPermissionProvider, PhotoFileGenerator photoFileGenerator,
      ImageValidator imageValidator, Scheduler viewScheduler, UriToPathResolver uriToPathResolver,
      ImagePickerNavigator navigator, ContentResolver contentResolver, ImageLoader imageLoader) {
    this.view = view;
    this.crashReport = crashReport;
    this.accountPermissionProvider = accountPermissionProvider;
    this.photoFileGenerator = photoFileGenerator;
    this.imageValidator = imageValidator;
    this.uiScheduler = viewScheduler;
    this.uriToPathResolver = uriToPathResolver;
    this.navigator = navigator;
    this.contentResolver = contentResolver;
    this.imageLoader = imageLoader;
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

  @NonNull private Completable loadValidImageOrThrowForCamera(String createdUri) {
    return imageValidator.validateOrGetException(createdUri)
        .observeOn(uiScheduler)
        .doOnCompleted(() -> view.loadImage(createdUri));
  }

  @NonNull private Single<String> getFileNameFromCameraWithUri(String createdUri) {
    return navigator.navigateToCameraWithImageUri(CAMERA_PICK, Uri.parse(createdUri))
        .first()
        .flatMapSingle(__ -> saveCameraPictureInPublicPhotos(createdUri))
        .toSingle();
  }

  /**
   * @return absolute Uri to the public photo
   */
  private Single<String> saveCameraPictureInPublicPhotos(String createdUri) {
    return Single.fromCallable(() -> {
      Bitmap image = imageLoader.loadBitmap(createdUri);
      if (image != null) {
        String path = MediaStore.Images.Media.insertImage(contentResolver, image,
            createdUri.substring(createdUri.lastIndexOf(File.pathSeparator)), null);
        image.recycle();
        return uriToPathResolver.getCameraStoragePath(Uri.parse(path));
      } else {
        return uriToPathResolver.getCameraStoragePath(Uri.parse(createdUri));
      }
    })
        .subscribeOn(Schedulers.io());
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
        .flatMap(__ -> accountPermissionProvider.permissionResultCamera(GALLERY_PICK)
            .filter(permissions -> permissions.get(0)
                .isGranted())
            .doOnNext(__2 -> view.dismissLoadImageDialog())
            .flatMap(__2 -> navigator.navigateToGalleryForImageUri(GALLERY_PICK))
            .flatMapCompletable(
                selectedImageUri -> loadValidImageOrThrowForGallery(selectedImageUri))
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

  @NonNull private Completable loadValidImageOrThrowForGallery(Uri selectedImageUri) {
    return imageValidator.validateOrGetException(selectedImageUri.toString())
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
        .flatMap(__ -> accountPermissionProvider.permissionResultCamera(CAMERA_PICK)
            .filter(permissions -> {
              for (PermissionProvider.Permission permission : permissions) {
                if (!permission.isGranted()) {
                  return false;
                }
              }
              return true;
            })
            .doOnNext(__2 -> view.dismissLoadImageDialog())
            .flatMapSingle(__2 -> photoFileGenerator.generateNewImageFileUriAsString())
            .flatMapCompletable(
                createdUri -> getFileNameFromCameraWithUri(createdUri).observeOn(uiScheduler)
                    .flatMapCompletable(
                        fullFilePath -> loadValidImageOrThrowForCamera(fullFilePath)))
            .doOnError(err -> {
              if (err instanceof InvalidImageException) {
                view.showIconPropertiesError((InvalidImageException) err);
              } else {
                crashReport.log(err);
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
