package cm.aptoide.pt.account;

import android.content.ContentResolver;
import android.net.Uri;
import cm.aptoide.pt.account.view.ImagePickerNavigator;
import cm.aptoide.pt.account.view.ImagePickerPresenter;
import cm.aptoide.pt.account.view.ImageValidator;
import cm.aptoide.pt.account.view.PhotoFileGenerator;
import cm.aptoide.pt.account.view.UriToPathResolver;
import cm.aptoide.pt.account.view.user.ManageUserFragment;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.permission.AccountPermissionProvider;
import cm.aptoide.pt.permission.PermissionProvider;
import cm.aptoide.pt.presenter.View;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by franciscocalado on 01/03/18.
 */

public class ImagePickerPresenterTest {

  private static int GALLERY_PICK = 5;
  private static int CAMERA_PICK = 6;

  @Mock ManageUserFragment userView;
  @Mock CrashReport crashReport;
  @Mock AccountPermissionProvider permissionProvider;
  @Mock PhotoFileGenerator photoFileGenerator;
  @Mock ImageValidator imageValidator;
  @Mock UriToPathResolver uriToPathResolver;
  @Mock ImagePickerNavigator navigator;
  @Mock ContentResolver contentResolver;
  @Mock ImageLoader imageLoader;

  private ImagePickerPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private List<PermissionProvider.Permission> cameraPermissions;
  private List<PermissionProvider.Permission> galeryPermissions;

  @Before public void setupImagePickerPresenterTest() {
    MockitoAnnotations.initMocks(this);
    lifecycleEvent = PublishSubject.create();
    cameraPermissions = new ArrayList<>();
    galeryPermissions = new ArrayList<>();

    PermissionProvider.Permission permission1 =
        new PermissionProvider.Permission(CAMERA_PICK, "test", true);
    PermissionProvider.Permission permission2 =
        new PermissionProvider.Permission(GALLERY_PICK, "Test", true);

    cameraPermissions.add(permission1);
    galeryPermissions.add(permission2);

    presenter =
        new ImagePickerPresenter(userView, crashReport, permissionProvider, photoFileGenerator,
            imageValidator, Schedulers.immediate(), uriToPathResolver, navigator, contentResolver,
            imageLoader);

    when(userView.getLifecycle()).thenReturn(lifecycleEvent);
  }

  @Test public void handlePickImageClickTest() {
    when(userView.selectStoreImageClick()).thenReturn(Observable.just(null));

    presenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(userView).showImagePickerDialog();
  }

  @Test public void handleCameraSelectionTest() {
    when(userView.dialogCameraSelected()).thenReturn(Observable.just(null));

    presenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(permissionProvider).requestCameraPermission(CAMERA_PICK);
  }

  @Test public void handleCameraImageResultTest() {
    when(permissionProvider.permissionResultCamera(CAMERA_PICK)).thenReturn(
        Observable.just(cameraPermissions));
    when(photoFileGenerator.generateNewImageFileUriAsString()).thenReturn(Single.just("Test"));
    when(navigator.navigateToCameraWithImageUri(CAMERA_PICK, Uri.parse("Test"))).thenReturn(
        Observable.just(null));
    when(imageLoader.loadBitmap("Test")).thenReturn(null);
    when(uriToPathResolver.getCameraStoragePath(Uri.parse("Test"))).thenReturn("Test");
    when(imageValidator.validateOrGetException("Test")).thenReturn(Completable.complete());
    doNothing().when(userView)
        .loadImage("Test");

    presenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(userView).dismissLoadImageDialog();
    verify(navigator).navigateToCameraWithImageUri(CAMERA_PICK, Uri.parse("Test"));
  }

  @Test public void handleGallerySelectionTest() {
    when(userView.dialogGallerySelected()).thenReturn(Observable.just(null));

    presenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(permissionProvider).requestGalleryPermission(GALLERY_PICK);
  }

  @Test public void handleGalleryImageResultTest() {

    when(permissionProvider.permissionResultCamera(GALLERY_PICK)).thenReturn(
        Observable.just(galeryPermissions));
    when(navigator.navigateToGalleryForImageUri(GALLERY_PICK)).thenReturn(Observable.just("Test"));
    when(imageValidator.validateOrGetException(Matchers.anyString())).thenReturn(
        Completable.complete());
    doNothing().when(userView)
        .loadImage("Test");

    presenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(userView).dismissLoadImageDialog();
  }
}
