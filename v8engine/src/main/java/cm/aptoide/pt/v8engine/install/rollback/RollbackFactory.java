package cm.aptoide.pt.v8engine.install.rollback;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.v8engine.install.installer.RollbackInstallation;
import java.io.File;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 9/8/16.
 */
public class RollbackFactory {

  public RollbackFactory() {
  }

  /**
   * @param context used to get app info context.getPackageManager().getPackageInfo()
   */
  public Observable<Rollback> createRollback(Context context, String packageName,
      Rollback.Action action, @Nullable String icon, String versionName) {

    return Observable.fromCallable(() -> {
      PackageManager packageManager = context.getPackageManager();
      PackageInfo info = packageManager.getPackageInfo(packageName, 0);

      Rollback rollback = new Rollback();
      rollback.setAction(action.name());
      rollback.setConfirmed(false);
      rollback.setAppName(AptoideUtils.SystemU.getApkLabel(info));
      rollback.setPackageName(info.packageName);
      rollback.setVersionCode(info.versionCode);
      rollback.setVersionName(versionName);
      rollback.setTimestamp(System.currentTimeMillis());
      rollback.setMd5(AptoideUtils.AlgorithmU.computeMd5(info));
      if (!TextUtils.isEmpty(icon)) {
        rollback.setIcon(icon);
      } else {
        String apkIconPath = AptoideUtils.SystemU.getApkIconPath(info);
        Bitmap theBitmap = ImageLoader.with(context)
            .loadBitmap(apkIconPath);
        String imagesCachePath = Application.getConfiguration()
            .getImagesCachePath();
        FileUtils.saveBitmapToFile(new File(imagesCachePath), packageName, theBitmap,
            Bitmap.CompressFormat.PNG, 100);
        rollback.setIcon(imagesCachePath + packageName);
      }
      return rollback;
    })
        .subscribeOn(Schedulers.computation());
  }

  @NonNull
  public Rollback createRollback(RollbackInstallation installation, Rollback.Action action) {
    Rollback rollback = new Rollback();
    rollback.setMd5(installation.getId());
    rollback.setAction(action.name());
    rollback.setAppName(installation.getAppName());
    rollback.setPackageName(installation.getPackageName());
    rollback.setConfirmed(false);
    rollback.setIcon(installation.getIcon());
    rollback.setVersionCode(installation.getVersionCode());
    rollback.setVersionName(installation.getVersionName());
    rollback.setTimestamp(System.currentTimeMillis());
    return rollback;
  }
}
