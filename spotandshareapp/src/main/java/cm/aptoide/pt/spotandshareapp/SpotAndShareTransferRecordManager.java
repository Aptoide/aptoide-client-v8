package cm.aptoide.pt.spotandshareapp;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.Accepter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by filipe on 10-07-2017.
 */

public class SpotAndShareTransferRecordManager {

  private Context context;

  public SpotAndShareTransferRecordManager(Context context) {
    this.context = context;
  }

  private List<AndroidAppInfo> getAndroidAppInfoList(List<Accepter<AndroidAppInfo>> acceptersList) {
    List<AndroidAppInfo> androidAppInfoList = new LinkedList<>();
    for (int i = 0; i < acceptersList.size(); i++) {
      androidAppInfoList.add(acceptersList.get(i)
          .getMeta());
    }
    return androidAppInfoList;
  }

  private List<TransferAppModel> convertAndroidAppInfoToTransferModel(
      List<AndroidAppInfo> appsList) {
    List<TransferAppModel> appModelList = new LinkedList<>();
    AndroidAppInfo androidAppInfo;
    for (int i = 0; i < appsList.size(); i++) {
      androidAppInfo = appsList.get(i);
      appModelList.add(
          new TransferAppModel(androidAppInfo.getAppName(), androidAppInfo.getPackageName(), "",
              convertByteToDrawable(androidAppInfo.getIcon()), false, "sendername"));
    }
    return appModelList;
  }

  public List<TransferAppModel> getTransferAppModelList(
      List<Accepter<AndroidAppInfo>> acceptersList) {
    List<AndroidAppInfo> androidAppInfoList = getAndroidAppInfoList(acceptersList);
    return convertAndroidAppInfoToTransferModel(androidAppInfoList);
  }

  private Drawable convertByteToDrawable(byte[] icon) {
    return new BitmapDrawable(context.getResources(),
        BitmapFactory.decodeByteArray(icon, 0, icon.length));
  }
}
