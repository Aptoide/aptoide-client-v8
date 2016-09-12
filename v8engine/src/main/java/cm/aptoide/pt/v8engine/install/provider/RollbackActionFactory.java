package cm.aptoide.pt.v8engine.install.provider;

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
import cm.aptoide.pt.v8engine.install.RollbackInstallation;
import cm.aptoide.pt.v8engine.install.RollbackInstallationFactory;
import java.io.File;
import lombok.AllArgsConstructor;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 9/8/16.
 */
@AllArgsConstructor public class RollbackActionFactory implements RollbackInstallationFactory {

  @Override public Observable<Rollback> createRollback(RollbackInstallation installation,
      Rollback.Action action) {
    return Observable.fromCallable(() -> createRollbackItem(installation, action))
        .subscribeOn(Schedulers.computation());
  }

  /**
   * @param icon
   * @param context used to get app info context.getPackageManager().getPackageInfo()
   */
  @Override public Observable<Rollback> createRollback(Context context, String packageName,
      Rollback.Action action, @Nullable String icon) {

    return Observable.fromCallable(() -> {
      PackageManager packageManager = context.getPackageManager();
      PackageInfo info = packageManager.getPackageInfo(packageName, 0);

      Rollback rollback = new Rollback();
      rollback.setAction(action.name());
      rollback.setConfirmed(false);
      rollback.setAppName(AptoideUtils.SystemU.getApkLabel(info));
      rollback.setPackageName(info.packageName);
      rollback.setVersionCode(info.versionCode);
      rollback.setTimestamp(System.currentTimeMillis());
      rollback.setMd5(AptoideUtils.AlgorithmU.computeMd5(info));
      if (!TextUtils.isEmpty(icon)) {
        rollback.setIcon(icon);
      } else {
        String apkIconPath = AptoideUtils.SystemU.getApkIconPath(info);
        Bitmap theBitmap = ImageLoader.loadBitmap(context, apkIconPath);
        String imagesCachePath = Application.getConfiguration().getImagesCachePath();
        FileUtils.saveBitmapToFile(new File(imagesCachePath), packageName, theBitmap,
            Bitmap.CompressFormat.PNG, 100);
        rollback.setIcon(imagesCachePath + packageName);
      }
      return rollback;
    }).subscribeOn(Schedulers.computation());
  }

  @NonNull
  private Rollback createRollbackItem(RollbackInstallation installation, Rollback.Action action) {
    Rollback rollback = new Rollback();
    rollback.setAction(action.name());
    rollback.setAppName(installation.getAppName());
    rollback.setPackageName(installation.getPackageName());
    rollback.setAppId(installation.getId());
    rollback.setConfirmed(false);
    rollback.setIcon(installation.getIcon());
    rollback.setVersionCode(installation.getVersionCode());
    rollback.setTimestamp(System.currentTimeMillis());
    rollback.setMd5(AptoideUtils.AlgorithmU.computeMd5(installation.getFile()));
    return rollback;
  }
}
