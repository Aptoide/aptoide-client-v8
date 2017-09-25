package cm.aptoide.pt.spotandshareapp;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import cm.aptoide.pt.spotandshareandroid.transfermanager.Transfer;
import cm.aptoide.pt.spotandshareandroid.transfermanager.TransferReceiving;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by filipe on 10-07-2017.
 */

public class SpotAndShareTransferRecordManager {

  private final String downloadsPath =
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
          .toString();
  private Context context;
  private List<Transfer> transferList;
  private SpotAndShareUserMapper userMapper;
  private FileSizeConverter fileSizeConverter;

  public SpotAndShareTransferRecordManager(Context context, SpotAndShareUserMapper userMapper,
      FileSizeConverter fileSizeConverter) {
    this.context = context;
    this.userMapper = userMapper;
    this.fileSizeConverter = fileSizeConverter;
  }

  public List<TransferAppModel> getTransferAppModelList(List<Transfer> transferList) {
    this.transferList = transferList;
    List<TransferAppModel> appModelList = new LinkedList<>();
    for (Transfer transfer : transferList) {
      appModelList.add(new TransferAppModel(transfer.getAndroidAppInfo()
          .getAppName(), transfer.getAndroidAppInfo()
          .getPackageName(), fileSizeConverter.convertToMB((double) transfer.getAndroidAppInfo()
          .getFilesSize()), downloadsPath + "/" + transfer.getAndroidAppInfo()
          .getPackageName(), convertByteToDrawable(transfer.getAndroidAppInfo()
          .getIcon()), transfer.getState(), userMapper.getSpotAndShareUser(
          transfer.getAndroidAppInfo()
              .getFriend()), transfer.hashCode()));
    }
    return appModelList;
  }

  public List<SpotAndShareTransfer> getTransfersList(List<Transfer> transferList) {
    this.transferList = transferList;
    List<SpotAndShareTransfer> newTransfersList = new LinkedList<>();
    for (int i = 0; i < transferList.size(); i++) {
      Transfer transfer = transferList.get(i);

      if (i > 0 && transferList.get(i - 1)
          .getAndroidAppInfo()
          .getFriend()
          .getUsername()
          .equals(transfer.getAndroidAppInfo()
              .getFriend()
              .getUsername())) {//add to the previous card

        newTransfersList.get(newTransfersList.size() - 1)
            .getAppsList()
            .add(new TransferAppModel(transfer.getAndroidAppInfo()
                .getAppName(), transfer.getAndroidAppInfo()
                .getPackageName(), fileSizeConverter.convertToMB(
                (double) transfer.getAndroidAppInfo()
                    .getFilesSize()), downloadsPath + "/" + transfer.getAndroidAppInfo()
                .getPackageName(), convertByteToDrawable(transfer.getAndroidAppInfo()
                .getIcon()), transfer.getState(), userMapper.getSpotAndShareUser(
                transfer.getAndroidAppInfo()
                    .getFriend()), transfer.hashCode()));
      } else { //create new element

        List<TransferAppModel> appList = new LinkedList<>();

        appList.add(new TransferAppModel(transfer.getAndroidAppInfo()
            .getAppName(), transfer.getAndroidAppInfo()
            .getPackageName(), fileSizeConverter.convertToMB((double) transfer.getAndroidAppInfo()
            .getFilesSize()), downloadsPath + "/" + transfer.getAndroidAppInfo()
            .getPackageName(), convertByteToDrawable(transfer.getAndroidAppInfo()
            .getIcon()), transfer.getState(), userMapper.getSpotAndShareUser(
            transfer.getAndroidAppInfo()
                .getFriend()), transfer.hashCode()));

        newTransfersList.add(new SpotAndShareTransfer(userMapper.getSpotAndShareUser(
            transferList.get(i)
                .getAndroidAppInfo()
                .getFriend()), appList));
      }
    }
    return newTransfersList;
  }

  private Drawable convertByteToDrawable(byte[] icon) {
    return new BitmapDrawable(context.getResources(),
        BitmapFactory.decodeByteArray(icon, 0, icon.length));
  }

  public void acceptApp(TransferAppModel transferAppModel) {
    for (int i = 0; i < transferList.size(); i++) {
      if (transferAppModel.getHashcode() == transferList.get(i)
          .hashCode() && transferList.get(i) instanceof TransferReceiving) {
        ((TransferReceiving) transferList.get(i)).accept();
        break;
      }
    }
  }
}
