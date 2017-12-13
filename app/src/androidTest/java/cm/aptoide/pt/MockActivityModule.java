package cm.aptoide.pt;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import cm.aptoide.pt.account.view.ImagePickerNavigator;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by jose_messejana on 12-12-2017.
 */

public class MockActivityModule extends ActivityModule {

  private final AppCompatActivity activity;

  public MockActivityModule(AppCompatActivity activity, Intent intent, NotificationSyncScheduler notificationSyncScheduler, String marketName,
      String autoUpdateUrl, View view, String defaultTheme, String defaultStoreName,
      boolean firstCreated, String fileProviderAuthority){
    super(activity,intent,notificationSyncScheduler,marketName,autoUpdateUrl,view,defaultTheme,defaultStoreName,firstCreated,fileProviderAuthority);
    this.activity = activity;
  }

  @Override ImagePickerNavigator provideImagePickerNavigator() {
    return new ImagePickerNavigator((ActivityNavigator) activity){

      @Override public Observable<Uri> navigateToGalleryForImageUri(int requestCode){
        String imageUri = "";
        if (TestType.types.equals(TestType.TestTypes.PHOTOSUCCESS)) {
          imageUri = "content://media/external/images/media/246";
        }
        else if (TestType.types.equals(TestType.TestTypes.PHOTOMIN)){
          imageUri = "content://media/external/images/media/118";
        }
        else if (TestType.types.equals(TestType.TestTypes.PHOTOMAX)){
          imageUri = "content://media/external/images/media/1364";
        }
        Uri uri = Uri.parse(imageUri);
        return Observable.just(uri);
      }
    };
  }
}
